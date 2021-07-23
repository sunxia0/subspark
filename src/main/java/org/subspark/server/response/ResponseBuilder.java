package org.subspark.server.response;

import org.subspark.server.utils.DateUtils;

public class ResponseBuilder {
    public static final String HTTP_1_0 = "HTTP/1.0";

    public static final String HTTP_1_1 = "HTTP/1.1";

    public static final String HEADER_SERVER = "subhttpd/0.1";

    public static final String CONNECTION_KEEP_ALIVE = "keep-alive";

    public static final String CONNECTION_CLOSE = "close";

    private final Response response;

    public ResponseBuilder() {
        this.response = new Response();

        //Set HTTP version
        this.response.protocol(HTTP_1_1);

        String now = DateUtils.now();

        //Set essential headers
        this.response.header("server", HEADER_SERVER);
        this.response.header("date", now);

        this.response.header("connection", CONNECTION_KEEP_ALIVE);
    }

    public ResponseBuilder protocol(String protocolVersion) {
        response.protocol(protocolVersion);
        return this;
    }

    public ResponseBuilder status(Status status) {
        response.status(status);
        return this;
    }

    public ResponseBuilder header(String header, String value) {
        response.header(header, value);
        return this;
    }

    public ResponseBuilder body(byte[] bodyRaw) {
        response.bodyRaw(bodyRaw);
        response.header("content-length", String.valueOf(bodyRaw.length));
        return this;
    }

    public ResponseBuilder body(String body) {
        response.body(body);
        response.header("content-length", String.valueOf(body.getBytes().length));
        return this;
    }

    public Response toResponse() {
        return response;
    }

    public static Response of100() {
        Response response = new Response();
        response.protocol(HTTP_1_1);
        response.status(Status.CONTINUE);
        return response;
    }
}
