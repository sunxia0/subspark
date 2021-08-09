package org.subspark.server.http;

public enum Method {
    GET, HEAD, POST;

    public static Method fromString(String method) {
        if (method == null)
            return null;
        try {
            return valueOf(method);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
