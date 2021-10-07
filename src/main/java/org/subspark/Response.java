package org.subspark;


import org.subspark.http.Status;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Response {
    private String protocolVersion;
    private Status status;
    private final Map<String, String> headers;
    private byte[] body;
    private CookieManager cookieManager;

    protected Response() {
        this.headers = new HashMap<>();
        this.cookieManager = new CookieManager();
    }

    // Can't be set by user
    protected void protocol(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void header(String header, String value) {
        this.headers.put(header, value);
    }

    public void status(Status status) {
        this.status = status;
    }

    public void bodyRaw(byte[] b) {
        this.body = b;
        if (b != null) {
            header("content-length", String.valueOf(b.length));
        }
    }

    public void body(String body) {
        bodyRaw(body == null ? null : body.getBytes());
    }

    public String protocol() {
        return protocolVersion;
    }

    public Status status() {
        return status;
    }

    public String statusDescription() {
        return status.fullDescription();
    }

    public byte[] bodyRaw() {
        return body;
    }

    public String body() {
        return body == null ? "" : new String(body, StandardCharsets.UTF_8);
    }

    public String header(String header) {
        return headers.get(header);
    }

    public Set<String> headers() {
        return headers.keySet();
    }

    protected String setCookieString() {
        return cookieManager.toSetCookieString();
    }

    public void cookie(String name, String value) {
        cookieManager.cookie(name, value, -1, false);
    }

    public void cookie(String name, String value, int maxAge) {
        cookieManager.cookie(name, value, maxAge, false);
    }

    public void cookie(String name, String value, int maxAge, boolean httpOnly) {
        cookieManager.cookie(name, value, maxAge, httpOnly);
    }

    public void cookie(String path, String name, String value) {
        cookie(path, name, value, -1, false);
    }

    public void cookie(String path, String name, String value, int maxAge) {
        cookie(path, name, value, maxAge, false);
    }

    public void cookie(String path, String name, String value, int maxAge, boolean httpOnly) {
        cookieManager.cookie(path, name, value, maxAge, httpOnly);
    }

    public void removeCookie(String name) {
        cookieManager.removeCookie(name);
    }

    public void removeCookie(String path, String name) {
        cookieManager.removeCookie(path, name);
    }

    public void redirect(String location) {
        redirect(location, Status.FOUND);
    }

    public void redirect(String location, Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Value of status can't be null!");
        }
        int codeStartDigit = status.code() / 100;
        if (codeStartDigit != 3) {
            throw new IllegalArgumentException("Use 3xx status for redirection!");
        }
        status(status);
        header("location", location);
        header("connection", Constant.CONNECTION_CLOSE);
    }

}
