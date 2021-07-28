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

    // TODO: add route support (use staticFilesHandler to handle all requests now)
    public HttpResponse handleRequest(HttpRequestBuilder requestBuilder) throws HaltException {
        RequestChecker.checkAbsoluteURL(requestBuilder, URLPattern);

        HttpRequest request = requestBuilder.toRequest();

        RequestChecker.checkSpecification(request);

        HttpResponseBuilder responseBuilder = service.getStaticFilesHandler().consumeFileRequest(request);

        if (!RequestChecker.isKeepAlive(request))
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

    private static final class RequestChecker {
        /**
         * For checking the validity of HTTP protocol version
         */
        private static final String HTTP_PROTOCOL_PATTERN = "HTTP/\\d\\.\\d";

        /**
         * Handle absolute URL, and substitute it with relative URL
         */
        public static void checkAbsoluteURL(HttpRequestBuilder requestBuilder, Pattern URLPattern) throws HaltException {
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

        /**
         * Check the specification of request
         */
        public static void checkSpecification(HttpRequest request) throws HaltException {
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
        public static boolean isKeepAlive(HttpRequest request) {
            String protocolVersion = request.protocol();
            String connection = request.header("connection");
            return protocolVersion.equals("HTTP/1.1")
                    && (connection == null || connection.equals(HttpResponseBuilder.CONNECTION_KEEP_ALIVE));
        }
    }
}
