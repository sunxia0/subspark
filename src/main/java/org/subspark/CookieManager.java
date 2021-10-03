package org.subspark;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

public class CookieManager {
    // Key: cookie identifier (`name`@`path`)
    private Map<String, Cookie> cookieHolder = new HashMap<>();

    CookieManager() {}

    private static String toSetCookieValue(Cookie cookie) {
        StringBuilder builder = new StringBuilder();
        builder.append(cookie.getName()).append("=").append(cookie.getValue());

        String path = cookie.getPath();
        if (path != null) {
            builder.append("; Path=").append(path);
        }

        int maxAge = cookie.getMaxAge();
        if (maxAge >= 0) {
            builder.append("; Max-Age=").append(maxAge);
        }

        boolean httpOnly = cookie.isHttpOnly();
        if (httpOnly) {
            builder.append("; HttpOnly");
        }

        return builder.toString();
    }

    void cookie(String name, String value, int maxAge, boolean httpOnly) {
        cookie(null, name, value, maxAge, httpOnly);
    }

    void cookie(String path, String name, String value, int maxAge, boolean httpOnly) {
        // Secure flag is unsupported in current HTTP/1.1 server
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        String cookieIdentifier = String.format("%s@%s", (path != null ? path : "/"), name);
        cookieHolder.put(cookieIdentifier, cookie);
    }

    void removeCookie(String name) {
        removeCookie(null, name);
    }

    void removeCookie(String path, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath(path);
        cookie.setMaxAge(0);
        String cookieIdentifier = String.format("%s@%s", (path != null ? path : "/"), name);
        cookieHolder.put(cookieIdentifier, cookie);
    }

    String toSetCookieString() {
        StringBuilder builder = new StringBuilder();
        for (Cookie cookie : cookieHolder.values()) {
            builder.append("set-cookie: ").append(toSetCookieValue(cookie)).append("\r\n");
        }
        return builder.toString();
    }
}
