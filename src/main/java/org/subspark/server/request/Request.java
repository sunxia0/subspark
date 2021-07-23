package org.subspark.server.request;


import java.util.*;

public class Request {
    private Method method;
    private String path;
    private String queryString;
    private String uri;
    private String protocolVersion;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private String body;
    private byte[] bodyRaw;

    protected Request () {
        this.queryParams = new HashMap<>();
        this.headers = new HashMap<>();
    }

    /**
     * ======= Setters only for RequestBuilder (package visible) ======
     */

    void method(String method) {
        this.method = Method.fromString(method);
    }

    void path(String path) {
        this.path = path;
    }

    void queryString(String queryString) {
        this.queryString = queryString;
    }

    void uri(String uri) {
        this.uri = uri;
    }

    void protocol(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    void queryParam(String key, String value) {
        this.queryParams.put(key, value);
    }

    void header(String key, String value) {
        this.headers.put(key, value);
    }

    void body(byte[] bodyRaw) {
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
     * @return The header "host"
     */
    public String host() {
        return headers.get("host");
    }

    /**
     * @return The header "user-agent"
     */
    public String userAgent() {
        return headers.get("user-agent");
    }

    /**
     * @return The header "content-type"
     */
    public String contentType() {
        return headers.get("content-type");
    }

    /**
     * @return The header "content-length"
     */
    public int contentLength() {
        try {
            return Integer.parseInt(headers.get("content-length"));
        } catch (NumberFormatException e) {
            return 0;
        }
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
}
