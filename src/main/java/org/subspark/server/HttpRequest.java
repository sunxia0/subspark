package org.subspark.server;


import org.subspark.server.http.Method;

import java.util.*;

public class HttpRequest {
    private Method method;
    private String path;
    private String queryString;
    private String uri;
    private String protocolVersion;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private Map<String, String> cookiesHolder;
    private String body;
    private byte[] bodyRaw;

    protected HttpRequest() {
        this.queryParams = new HashMap<>();
        this.headers = new HashMap<>();
    }

    /**
     * ======= Setters only for the class of the same package ======
     */
    protected void method(String method) {
        this.method = Method.fromString(method);
    }

    protected void path(String path) {
        this.path = path;
    }

    protected void queryString(String queryString) {
        this.queryString = queryString;
    }

    protected void uri(String uri) {
        this.uri = uri;
    }

    protected void protocol(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    protected void queryParam(String key, String value) {
        this.queryParams.put(key, value);
    }

    protected void cookiesHolder(Map<String, String> cookiesHolder) {
        this.cookiesHolder =cookiesHolder;
    }

    protected void header(String key, String value) {
        this.headers.put(key, value);
    }

    protected void body(byte[] bodyRaw) {
        this.bodyRaw = bodyRaw;
        this.body = new String(this.bodyRaw);
    }

    /**
     * The request method (GET, POST, ...)
     */
    public Method method() {
        return method;
    }

    /**
     * @return The path (without query params)
     */
    public String path() {
        return path;
    }

    /**
     * @return The URI up to the query string
     */
    public String uri() {
        return uri;
    }

    /**
     * @return Query parameter from the URL
     */
    public String queryParam(String param) {
        return queryParams.get(param);
    }

    public String queryParamOrDefault(String param, String def) {
        String ret = queryParam(param);
        return (ret == null) ? def : ret;
    }

    public Set<String> queryParams() {
        return queryParams.keySet();
    }

    /**
     * @return The raw query string
     */
    public String queryString() {
        return this.queryString;
    }

    /**
     * @return The protocol name and version from the request
     */
    public String protocol() {
        return this.protocolVersion;
    }

    /**
     * @return Get the item from the header
     */
    public String header(String name) {
        return headers.get(name);
    }

    public Set<String> headers() {
        return headers.keySet();
    }

    /**
     * @return The request body sent by the client (encoded with UTF-8)
     */
    public String body() {
        return body;
    }

    /**
     * @return The request body bytes
     */
    public byte[] bodyRaw() {
        return bodyRaw;
    }

    /**
     * @return Gets the session associated with this request
     */
    public Session session() {
        return null;
    }

    public Map<String, String> cookies() {
        return cookiesHolder;
    }

    public String cookie(String name) {
        if (name == null || cookies() == null)
            return null;
        else
            return cookies().get(name);
    }
}
