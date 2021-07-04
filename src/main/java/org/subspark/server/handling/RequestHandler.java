package org.subspark.server.handling;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.server.WebService;
import org.subspark.server.exceptions.HaltException;
import org.subspark.server.request.Request;
import org.subspark.server.response.Response;
import org.subspark.server.response.ResponseBuilder;
import org.subspark.server.response.Status;

import java.util.regex.Pattern;

//TODO: Parse path with host name (absolute URL)
//TODO: Support Chunked Transfer-Encoding (receiving chunked message)

//TODO: Send "100 Continue" on receiving the first line of HTTP/1.1 request
//TODO: Handle `If-Modified-Since` and `In-Unmodified-Since`
//TODO: Handle HEAD request

public class RequestHandler {
    private final static Logger logger = LogManager.getLogger(RequestHandler.class);

    private final WebService service;
    private final StaticFilesHandler staticFilesHandler;

    public RequestHandler(WebService service) {
        this.service = service;
        this.staticFilesHandler = new StaticFilesHandler();
    }

    public void staticFileLocation(String location) {
        staticFilesHandler.staticFileLocation(location);
    }

    // TODO: add route support (use staticFilesHandler to handle all requests now)
    public Response handleRequest(Request request) throws HaltException {
        checkSpecification(request);
        return null;
    }

    public Response handleException(HaltException exception) {
        return null;
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
