package org.subspark.server.handling;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.server.WebService;
import org.subspark.server.common.MimeType;
import org.subspark.server.exceptions.HaltException;
import org.subspark.server.request.HttpRequest;
import org.subspark.server.request.HttpRequestBuilder;
import org.subspark.server.response.HttpResponse;
import org.subspark.server.response.HttpResponseBuilder;
import org.subspark.server.response.Status;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RequestHandler {
    private final static Logger logger = LogManager.getLogger(RequestHandler.class);

    private final WebService service;

    private final Pattern URLPattern;

    public RequestHandler(WebService service) {
        this.service = service;
        this.URLPattern = Pattern.compile(String.format("^(http://%s:%d)?((/.*?)(\\?.*)?)$", service.ipAddress(), service.port()));
    }

    /**
     * Check `Transfer-Encoding` header.
     * If the request used chunked transfer, substitute the
     * chunked body with merged body
     */
    private void mergeChunkedBody(HttpRequestBuilder requestBuilder) throws HaltException {
        String transferEncoding = requestBuilder.header("transfer-encoding");
        byte[] bodyRaw = requestBuilder.bodyRaw();

        if (transferEncoding != null && transferEncoding.equals("chunked") && bodyRaw.length > 0) {
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
                            throw new HaltException(Status.BAD_REQUEST);
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
                            requestBuilder.body(merged);
                        }
                        else {
                            nextLine = reader.readLine();
                            if (nextLine == null) {
                                throw new HaltException(Status.BAD_REQUEST, "Unclosed chunked body!");
                            }
                            chunks.add(nextLine.getBytes());
                        }
                    }
                    else {
                        // Parse footers like headers
                        sep = line.indexOf(":");

                        if (sep > 0) {
                            key = line.substring(0, sep).trim().toLowerCase();
                            requestBuilder.header(key, line.substring(sep + 1).trim());
                        }
                        else if (key != null && (line.startsWith(" ") || line.startsWith("\t"))) {
                            requestBuilder.header(key, requestBuilder.header(key) + line.trim());
                        }
                    }
                }
            } catch (IOException e) {
                throw new HaltException(Status.INTERNAL_SERVER_ERROR, "An error occurred when parsing chunked request.");
            }
        }
    }

    /**
     * Handle absolute URL, and substitute it with relative URL
     */
    private void checkAbsoluteURL(HttpRequestBuilder requestBuilder) throws HaltException {
        Matcher uriMatcher = URLPattern.matcher(requestBuilder.uri());

        if (!uriMatcher.find())
            throw new HaltException(Status.BAD_REQUEST, "Invalid Path");

        // Substitute absolute URL
        // for a URL "http://[hostname][:port]/path/to/file?key1=a&key2=b"
        // group 1 capture "http://[hostname][:port]"
        // group 2 capture "/path/to/file?key1=a&key2=b"
        // group 3 capture "/path/to/file"
        if (uriMatcher.group(1) != null) {
            String newURI = uriMatcher.group(2);
            String newPath = uriMatcher.group(3);

            requestBuilder.uri(newURI);
            requestBuilder.path(newPath);
        }
    }

    // TODO: add route support (use staticFilesHandler to handle all requests now)
    public HttpResponse handleRequest(HttpRequestBuilder requestBuilder) throws HaltException {
        mergeChunkedBody(requestBuilder);
        checkAbsoluteURL(requestBuilder);
        HttpRequest request = requestBuilder.toRequest();

        checkSpecification(request);

        HttpResponseBuilder responseBuilder = service.getStaticFilesHandler().consumeFileRequest(request);

        if (!isKeepAlive(request))
            responseBuilder.header("connection", "close");

        return responseBuilder.toResponse();
    }

    public HttpResponse handleException(HaltException exception) {
        HttpResponseBuilder builder = new HttpResponseBuilder();

        builder.status(exception.getStatus())
                .header("content-type", MimeType.TXT)
                .header("connection", "close")
                .body(exception.getMessage());

        return builder.toResponse();
    }

    /**
     * For checking the validity of HTTP protocol version
     */
    private static final String HTTP_PROTOCOL_PATTERN = "HTTP/\\d\\.\\d";

    /**
     * Check the specification of request
     */
    private static void checkSpecification(HttpRequest request) throws HaltException {
        // Invalid HTTP verb
        if (request.method() == null)
            throw new HaltException(Status.BAD_REQUEST);

        // Invalid HTTP protocol version
        if (!Pattern.matches(HTTP_PROTOCOL_PATTERN, request.protocol()))
            throw new HaltException(Status.BAD_REQUEST);

        // Check `host` header for HTTP/1.1
        if (request.protocol().equals("HTTP/1.1") && request.header("host") == null)
            throw new HaltException(Status.BAD_REQUEST);
    }

    /**
     * Check `connection` header and return whether the connection is persistent
     */
    private static boolean isKeepAlive(HttpRequest request) {
        String protocolVersion = request.protocol();
        String connection = request.header("connection");
        return protocolVersion.equals("HTTP/1.1")
                && (connection == null || connection.equals(HttpResponseBuilder.CONNECTION_KEEP_ALIVE));
    }
}
