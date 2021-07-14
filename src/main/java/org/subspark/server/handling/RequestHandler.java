package org.subspark.server.handling;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.server.WebService;
import org.subspark.server.common.MimeType;
import org.subspark.server.exceptions.HaltException;
import org.subspark.server.request.Request;
import org.subspark.server.request.RequestBuilder;
import org.subspark.server.response.Response;
import org.subspark.server.response.ResponseBuilder;
import org.subspark.server.response.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: Support Chunked Transfer-Encoding (receiving chunked message)

public class RequestHandler {
    private final static Logger logger = LogManager.getLogger(RequestHandler.class);

    private final WebService service;

    private final Pattern URLPattern;

    public RequestHandler(WebService service) {
        this.service = service;
        this.URLPattern = Pattern.compile(String.format("^(http://%s:%d)?((/.*?)(\\?.*)?)$", service.ipAddress(), service.port()));
    }

    /**
     * Handle absolute URL, substituting with relative URL
     */
    private void checkAbsoluteURL(RequestBuilder requestBuilder) throws HaltException {
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
    public Response handleRequest(RequestBuilder requestBuilder) throws HaltException {
        checkAbsoluteURL(requestBuilder);
        Request request = requestBuilder.toRequest();

        checkSpecification(request);

        ResponseBuilder builder = service.getStaticFilesHandler().consumeFileRequest(request);

        if (!isKeepAlive(request))
            builder.header("connection", "close");

        return builder.toResponse();
    }

    public Response handleException(HaltException exception) {
        ResponseBuilder builder = new ResponseBuilder();

        builder.status(exception.getStatus())
                .header("content-type", MimeType.TXT)
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
    private static void checkSpecification(Request request) throws HaltException {
        // Invalid HTTP verb
        if (request.method() == null)
            throw new HaltException(Status.BAD_REQUEST);

        // Invalid HTTP protocol version
        if (!Pattern.matches(HTTP_PROTOCOL_PATTERN, request.protocol()))
            throw new HaltException(Status.BAD_REQUEST);

        // Check `host` header for HTTP/1.1
        if (request.protocol().equals(ResponseBuilder.HTTP_1_1) && request.header("host") == null)
            throw new HaltException(Status.BAD_REQUEST);
    }

    /**
     * Check `connection` header and return whether the connection is persistent
     */
    private static boolean isKeepAlive(Request request) {
        String protocolVersion = request.protocol();
        String connection = request.header("connection");
        return protocolVersion.equals(ResponseBuilder.HTTP_1_1)
                && (connection == null || connection.equals(ResponseBuilder.CONNECTION_KEEP_ALIVE));
    }
}
