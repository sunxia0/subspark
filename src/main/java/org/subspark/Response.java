package org.subspark;

import org.subspark.server.response.HttpResponse;

public class Response extends HttpResponse {
    protected Response() {
        super();
    }

    // ============ for stage 2 ============

    public void redirect(String location) {

    }

    public void redirect(String location, int httpStatusCode) {

    }

    public void cookie(String name, String value) {

    }

    public void cookie(String name, String value, int maxAge) {

    }

    public void cookie(String name, String value, int maxAge, boolean secured) {

    }

    public void cookie(String name, String value, int maxAge, boolean secured, boolean httpOnly) {

    }

    public void cookie(String path, String name, String value) {

    }

    public void cookie(String path, String name, String value, int maxAge) {

    }

    public void cookie(String path, String name, String value, int maxAge, boolean secured) {

    }

    public void cookie(String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {

    }

    public void removeCookie(String name) {

    }

    public void removeCookie(String path, String name) {

    }
}
