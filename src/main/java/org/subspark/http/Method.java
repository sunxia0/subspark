package org.subspark.http;

public enum Method {
    HEAD, GET, POST, PUT, DELETE, OPTIONS;

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
