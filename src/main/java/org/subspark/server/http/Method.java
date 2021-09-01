package org.subspark.server.http;

public enum Method {
    HEAD, GET, POST, PUT, DELETE, OPTIONS, TRACE;

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
