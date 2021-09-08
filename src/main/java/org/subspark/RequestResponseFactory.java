package org.subspark;


import org.subspark.http.Status;
import org.subspark.utils.DateUtils;

public final class RequestResponseFactory {
    private RequestResponseFactory() {}

    public static Request createHttpRequest() {
        return new Request();
    }

    public static Response createHttpResponse() {
        Response response = new Response();

        //Set HTTP version
        response.protocol(Constant.HTTP_1_1);

        //Set default status
        response.status(Status.OK);

        String now = DateUtils.now();

        //Set essential headers
        response.header("server", Constant.HEADER_SERVER);
        response.header("date", now);
        response.header("connection", Constant.CONNECTION_KEEP_ALIVE);
        response.header("content-type", MimeType.TXT);
        response.header("content-length", "0");
        return response;
    }

    public static Response createHttpResponse(HaltException e) {
        Response response = createHttpResponse();
        response.status(e.getStatus());
        response.header("content-type", MimeType.TXT);
        response.header("connection", Constant.CONNECTION_CLOSE);
        response.body(e.getMessage());
        return response;
    }

    public static Response of100() {
        Response response = createHttpResponse();
        response.status(Status.CONTINUE);
        return response;
    }
}
