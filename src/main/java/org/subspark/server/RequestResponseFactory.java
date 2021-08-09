package org.subspark.server;


import org.subspark.server.exceptions.HaltException;
import org.subspark.server.http.Status;
import org.subspark.server.utils.DateUtils;

public final class RequestResponseFactory {
    private RequestResponseFactory() {}

    public static HttpRequest createHttpRequest() {
        return new HttpRequest();
    }

    public static HttpResponse createHttpResponse() {
        HttpResponse response = new HttpResponse();

        //Set HTTP version
        response.protocol(Constant.HTTP_1_1);

        String now = DateUtils.now();

        //Set essential headers
        response.header("server", Constant.HEADER_SERVER);
        response.header("date", now);
        response.header("connection", Constant.CONNECTION_KEEP_ALIVE);
        return response;
    }

    public static HttpResponse createHttpResponse(HaltException e) {
        HttpResponse response = createHttpResponse();
        response.status(e.getStatus());
        response.header("content-type", MimeType.TXT);
        response.header("connection", Constant.CONNECTION_CLOSE);
        response.body(e.getMessage());
        return response;
    }

    public static HttpResponse of100() {
        HttpResponse response = createHttpResponse();
        response.status(Status.CONTINUE);
        return response;
    }
}
