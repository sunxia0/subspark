/*
 * #%L
 * NanoHttpd-Core
 * %%
 * Copyright (C) 2012 - 2016 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.subspark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.http.Status;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Header parsing help, learned from NanoHttpd, copyright notice above.
 */
public class HttpParser {
    private static final Logger logger = LogManager.getLogger(HttpParser.class);

    /**
     * Initial fetch buffer for the HTTP request header
     */
    private static final int BUFFER_SIZE = 8 * 1024;

    /**
     * Maximum request / response body size (4MB)
     */
    private static final int BODY_BUFFER_SIZE = 4 * 1024 * 1024;

    /**
     * Maximum buffer page
     */
    private static final int MAXIMUM_BODY_BUFFER_PAGE = BODY_BUFFER_SIZE / BUFFER_SIZE;

    /**
     * Find the split position of header and body from `buffer[start, end)`
     */
    private static int findHeaderEnd(byte[] buffer, int start, int end) {
        int split = start;
        while (split + 1 < end) {

            // RFC 2616 (Each line ends with '\r\n')
            if (split + 3 < end && buffer[split] == '\r' && buffer[split + 1] == '\n' && buffer[split + 2] == '\r' && buffer[split + 3] == '\n')
                return split + 4;

            // Tolerance (Each line ends with '\n')
            if (buffer[split] == '\n' && buffer[split + 1] == '\n')
                return split + 2;

            split++;
        }
        return 0;
    }

    /**
     * Decode URL encoded request query string
     */
    private static String decodePercent(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }

    /**
     * Decode each k/v of request query string
     */
    private static void decodeQueryString(Request request, String queryString) throws HaltException {
        StringTokenizer tokenizer = new StringTokenizer(queryString, "&");
        String pair;
        int sep;

        while (tokenizer.hasMoreTokens()) {
            pair = tokenizer.nextToken();
            sep = pair.indexOf("=");

            if (sep == -1)
                throw new HaltException(Status.BAD_REQUEST, "Invalid query string");

            // The value of the next parameter with the same name will override the value of the previous one
            request.queryParam(decodePercent(pair.substring(0, sep)), decodePercent(pair.substring(sep + 1)));
        }
    }

    /**
     * Parse a head line and create k/v pair, return the current key
     */
    private static String parseHeadline(Request request, String headerLine, String key) {
        int sep = headerLine.indexOf(":");

        // Use trim() to ignore possible spaces and tabs
        // Consider header value written in multiple lines
        if (sep > 0) {
            key = headerLine.substring(0, sep).trim().toLowerCase();
            request.header(key, headerLine.substring(sep + 1).trim());
        } else if (key != null && (headerLine.startsWith(" ") || headerLine.startsWith("\t"))) {
            request.header(key, request.header(key) + headerLine.trim());
        }

        return key;
    }

    /**
     * Decode request header
     */
    private static void decodeRequestHeader(Request request, BufferedReader headerBuffer) throws HaltException {
        try {
            // Request line
            String requestLine = headerBuffer.readLine();
            StringTokenizer tokenizer = new StringTokenizer(requestLine);

            // Get method
            if (!tokenizer.hasMoreTokens())
                throw new HaltException(Status.BAD_REQUEST, "No HTTP verb in request");
            request.method(tokenizer.nextToken());

            // Get URI
            if (!tokenizer.hasMoreTokens())
                throw new HaltException(Status.BAD_REQUEST, "No path in request");
            String rawURI = tokenizer.nextToken();
            String path, queryString;

            // Ignore `#` tag
            int sep = rawURI.indexOf("?");
            if (sep != -1) {
                path = decodePercent(rawURI.substring(0, sep));
                queryString = rawURI.substring(sep + 1);

                request.path(path);
                request.queryString(queryString);
                request.uri(path + '?' + queryString);

                decodeQueryString(request, queryString);
            } else {
                path = decodePercent(rawURI);

                request.path(path);
                request.queryString("");
                request.uri(path);
            }

            // Get protocol version
            if (!tokenizer.hasMoreTokens())
                throw new HaltException(Status.BAD_REQUEST, "No protocol version in request");
            request.protocol(tokenizer.nextToken());

            // Get headers
            String headerLine;
            String key = null;
            while ((headerLine = headerBuffer.readLine()) != null) {
                key = parseHeadline(request, headerLine, key);
            }

        } catch (IOException e) {
            logger.error("An error occurred when parsing request header", e);
            throw new HaltException(Status.INTERNAL_SERVER_ERROR, "An IOException occurred when parsing request header");
        }
    }

    /**
     * Assemble the body buffer to create full request body
     */
    private static byte[] assembleRequestBody(List<byte[]> bodyBuffer) {
        byte[] body;
        int bodyLength = 0;
        int start = 0;

        for (byte[] parts : bodyBuffer)
            bodyLength += parts.length;

        body = new byte[bodyLength];

        for (byte[] parts : bodyBuffer) {
            System.arraycopy(parts, 0, body, start, parts.length);
            start += parts.length;
        }

        return body;
    }

    /**
     * Check `Cookie` header and create Map for the request
     */
    private static void generateCookiesHolder(Request request) throws HaltException {
        String cookies = request.header("cookie");

        if (cookies != null) {
            Map<String, String> cookiesHolder = new HashMap<>();
            String[] kvs = cookies.split(";");

            int sep;
            for (String kv : kvs) {
                kv = kv.trim();
                if (kv.length() > 0) {
                    sep = kv.indexOf("=");
                    if (sep == -1)
                        throw new HaltException(Status.BAD_REQUEST, "Invalid cookie value");
                    cookiesHolder.put(kv.substring(0, sep), kv.substring(sep + 1));
                }
            }

            request.cookiesHolder(cookiesHolder);
        }
    }

    /**
     * Check `Transfer-Encoding` header.
     * If the request used chunked transfer, substitute the
     * chunked body with merged body
     */
    private static void mergeChunkedBody(Request request) throws HaltException {
        String transferEncoding = request.header("transfer-encoding");
        byte[] bodyRaw = request.bodyRaw();

        if ("chunked".equals(transferEncoding) && bodyRaw.length > 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bodyRaw)));
            boolean bodyEnd = false;
            String line, nextLine, key = null;
            int sep, lineLength, fullLength = 0;
            List<byte[]> chunks = new ArrayList<>();

            try {
                while ((line = reader.readLine()) != null) {
                    if (!bodyEnd) {
                        // Check semicolon
                        sep = line.indexOf(";");
                        if (sep != -1)
                            line = line.substring(0, sep);

                        try {
                            lineLength = Integer.parseInt(line, 16);
                            fullLength += lineLength;
                        } catch (NumberFormatException e) {
                            throw new HaltException(Status.BAD_REQUEST, "Invalid chunked body");
                        }

                        if (lineLength == 0) {
                            bodyEnd = true;

                            // Merge chunks
                            byte[] merged = new byte[fullLength];
                            int start = 0;
                            for (byte[] chunk : chunks) {
                                System.arraycopy(chunk, 0, merged, start, chunk.length);
                                start += chunk.length;
                            }
                            request.body(merged);
                        } else {
                            nextLine = reader.readLine();
                            if (nextLine == null) {
                                throw new HaltException(Status.BAD_REQUEST, "Unclosed chunked body!");
                            }
                            chunks.add(nextLine.getBytes());
                        }
                    } else {
                        // Parse footers like headers
                        key = parseHeadline(request, line, key);
                    }
                }
            } catch (IOException e) {
                throw new HaltException(Status.INTERNAL_SERVER_ERROR, "An error occurred when parsing chunked request.");
            }
        }
    }

    /**
     * Parse InputStream and create Request object
     */
    public static Request parseRequest(InputStream in) throws HaltException, IOException {
        Request request = RequestResponseFactory.createHttpRequest();

        // Enable mark/reset support
        BufferedInputStream bufferedIn = new BufferedInputStream(in, BUFFER_SIZE);

        byte[] buffer = new byte[BUFFER_SIZE];
        List<byte[]> bodyBuffer = new ArrayList<>();
        byte[] body;
        int read;
        int readLength = 0;
        int split = 0;

        // Read the first 8192 bytes.
        // Assume the full header should fit in here.

        bufferedIn.mark(BUFFER_SIZE);
        read = bufferedIn.read(buffer, 0, BUFFER_SIZE);

        if (read == -1)
            throw new HaltException(Status.BAD_REQUEST, "Empty request!");

        while (readLength <= BUFFER_SIZE && read > 0) {
            split = findHeaderEnd(buffer, readLength, readLength + read);

            if (split > 0)
                break;

            readLength += read;
            read = bufferedIn.read(buffer, readLength, BUFFER_SIZE - readLength);
        }

        // Don't find split byte, invalid request
        if (split == 0)
            throw new HaltException(Status.BAD_REQUEST, "Invalid split byte");

        BufferedReader headerBuffer = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer, 0, split)));
        decodeRequestHeader(request, headerBuffer);

        // Skip header bytes
        try {
            bufferedIn.reset();

            long skipped = bufferedIn.skip(split);

            // Abnormal skip
            if (skipped < split)
                throw new IOException();

        } catch (IOException e) {
            logger.error("An abnormal header split detected", e);
            throw new HaltException(Status.INTERNAL_SERVER_ERROR, "An abnormal header split detected, please check your request.");
        }

        // Read body
        while (bufferedIn.available() > 0){
            read = bufferedIn.read(buffer, 0, BUFFER_SIZE);

            if (read <= 0)
                break;

            // Too large request body
            if (bodyBuffer.size() == MAXIMUM_BODY_BUFFER_PAGE)
                throw new HaltException(Status.BAD_REQUEST, "Too large request body");

            body = new byte[read];
            System.arraycopy(buffer, 0, body, 0, read);
            bodyBuffer.add(body);
        }

        request.body(assembleRequestBody(bodyBuffer));

        generateCookiesHolder(request);

        mergeChunkedBody(request);

        logger.info("Receive request:\n" + createRequestHeaderString(request));

        return request;
    }

    public static void sendResponse(OutputStream out, Response response, boolean withBody) throws IOException {
        String headerString = createResponseHeaderString(response);
        byte[] body = response.bodyRaw();

        if (response.status() != Status.CONTINUE) {
            logger.info("Send response:\n" + headerString);
        }

        // Write header string (including status line and headers)
        out.write(headerString.getBytes());

        // Write separating CRLF
        out.write("\r\n".getBytes());

        // Write body bytes
        if (withBody && body != null && body.length > 0) {
            out.write(body);
        }
    }

    private static String createRequestHeaderString(Request request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.method()).append(' ').append(request.uri()).append(' ').append(request.protocol()).append("\r\n");
        for (String name : request.headers()) {
            sb.append(name).append(": ").append(request.header(name)).append("\r\n");
        }
        return sb.toString();
    }

    private static String createResponseHeaderString(Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append(response.protocol()).append(' ').append(response.statusDescription()).append("\r\n");
        for (String name : response.headers())
            sb.append(name).append(": ").append(response.header(name)).append("\r\n");
        return sb.toString();
    }
}
