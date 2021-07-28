package org.subspark.server.request;

import java.util.Map;

public class HttpRequestBuilder {
    private final HttpRequest request;

    public HttpRequestBuilder() {
        this.request = new HttpRequest();
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

    public String body() {
        return request.body();
    }

    public byte[] bodyRaw() {
        return request.bodyRaw();
    }

    public HttpRequestBuilder method(String method) {
        request.method(method);
        return this;
    }

    public HttpRequestBuilder path(String path) {
        request.path(path);
        return this;
    }

    public HttpRequestBuilder queryString(String queryString) {
        request.queryString(queryString);
        return this;
    }

    public HttpRequestBuilder uri(String uri) {
        request.uri(uri);
        return this;
    }

    public HttpRequestBuilder protocol(String protocolVersion) {
        request.protocol(protocolVersion);
        return this;
    }

    public HttpRequestBuilder queryParam(String key, String value) {
        request.queryParam(key, value);
        return this;
    }

    public HttpRequestBuilder header(String key, String value) {
        request.header(key, value);
        return this;
    }

    public HttpRequestBuilder cookiesHolder(Map<String, String> cookiesHolder) {
        request.cookiesHolder(cookiesHolder);
        return this;
    }

    public HttpRequestBuilder body(byte[] bodyRaw) {
        request.body(bodyRaw);
        return this;
    }

    public HttpRequest toRequest() {
        return request;
    }
}
