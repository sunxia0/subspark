package org.subspark;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.http.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RequestHandler {
    private final static Logger logger = LogManager.getLogger(RequestHandler.class);

    private final Service service;

    private final Pattern URLPattern;

    public RequestHandler(Service service) {
        this.service = service;
        this.URLPattern = Pattern.compile(String.format("^(http://%s:%d)?((/.*?)(\\?.*)?)$", service.ipAddress(), service.port()));
    }

    public Response handleRequest(Request request) throws HaltException {
        RequestChecker.checkAbsoluteURL(request, URLPattern);
        RequestChecker.checkSpecification(request);

        Response response = RequestResponseFactory.createHttpResponse();
        response.protocol(request.protocol());

        if (!RequestChecker.isKeepAlive(request)) {
            response.header("connection", Constant.CONNECTION_CLOSE);
        }

        boolean isStaticConsumed = service.getStaticFilesHandler().consume(request, response);
        if (!isStaticConsumed) {
            service.getRouter().consume(request, response);
        }

        return response;
    }

    public Response handleException(HaltException exception) {
        return RequestResponseFactory.createHttpResponse(exception);
    }

    private static final class RequestChecker {
        /**
         * For checking the validity of HTTP protocol version
         */
        private static final String HTTP_PROTOCOL_PATTERN = "HTTP/\\d\\.\\d";

        /**
         * Handle absolute URL, and substitute it with relative URL
         */
        public static void checkAbsoluteURL(Request request, Pattern URLPattern) throws HaltException {
            Matcher uriMatcher = URLPattern.matcher(request.uri());

            if (!uriMatcher.find()) {
                throw new HaltException(Status.BAD_REQUEST, "Invalid Path");
            }

            // Substitute absolute URL
            // for a URL "http://[hostname][:port]/path/to/file?key1=a&key2=b"
            // group 1 capture "http://[hostname][:port]"
            // group 2 capture "/path/to/file?key1=a&key2=b"
            // group 3 capture "/path/to/file"
            if (uriMatcher.group(1) != null) {
                String newURI = uriMatcher.group(2);
                String newPath = uriMatcher.group(3);

                request.uri(newURI);
                request.path(newPath);
            }
        }

        /**
         * Check the specification of request
         */
        public static void checkSpecification(Request request) throws HaltException {
            // Invalid HTTP verb
            if (request.method() == null) {
                throw new HaltException(Status.NOT_IMPLEMENTED, "Unimplemented HTTP method");
            }

            // Invalid HTTP protocol version
            if (!Pattern.matches(HTTP_PROTOCOL_PATTERN, request.protocol())) {
                throw new HaltException(Status.BAD_REQUEST, "Invalid HTTP protocol version");
            }

            // Check `host` header for HTTP/1.1
            if (request.protocol().equals(Constant.HTTP_1_1) && request.header("host") == null) {
                throw new HaltException(Status.BAD_REQUEST, "No \"Host\" header in an HTTP/1.1 request");
            }
        }

        /**
         * Check `connection` header and return whether the connection is persistent
         */
        public static boolean isKeepAlive(Request request) {
            String protocolVersion = request.protocol();
            String connection = request.header("connection");
            return protocolVersion.equals(Constant.HTTP_1_1)
                    && (connection == null || connection.equals(Constant.CONNECTION_KEEP_ALIVE));
        }
    }
}
