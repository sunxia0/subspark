package org.subspark.server.request;

public class RequestBuilder {
    private final Request request;

    public RequestBuilder() {
        this.request = new Request();
    }

    public Method method() {
        return request.method();
    }

    public String path() {
        return request.path();
    }

    public String queryString() {
        return request.queryString();
    }

    public String uri() {
        return request.uri();
    }

    public String protocol() {
        return request.protocol();
    }

    public String queryParam(String key) {
        return request.queryParam(key);
    }

    public String header(String key) {
        return request.header(key);
    }

    public byte[] body() {
        return request.bodyRaw();
    }

    public RequestBuilder method(String method) {
        request.method(method);
        return this;
    }

    public RequestBuilder path(String path) {
        request.path(path);
        return this;
    }

    public RequestBuilder queryString(String queryString) {
        request.queryString(queryString);
        return this;
    }

    public RequestBuilder uri(String uri) {
        request.uri(uri);
        return this;
    }

    public RequestBuilder protocol(String protocolVersion) {
        request.protocol(protocolVersion);
        return this;
    }

    public RequestBuilder queryParam(String key, String value) {
        request.queryParam(key, value);
        return this;
    }

    public RequestBuilder header(String key, String value) {
        request.header(key, value);
        return this;
    }

    public RequestBuilder body(byte[] bodyRaw) {
        request.body(bodyRaw);
        return this;
    }

    public Request toRequest() {
        return request;
    }
}
