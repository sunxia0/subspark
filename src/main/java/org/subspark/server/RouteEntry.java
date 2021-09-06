package org.subspark.server;

import org.subspark.server.http.Method;
import org.subspark.server.utils.SerializationUtils;

public abstract class RouteEntry {
    private final Method method;
    private final String path;

    RouteEntry(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public boolean matches(Method method, String path) {
        return path.equals(this.path)
                && (this.method == null || method.equals(this.method) || method.equals(Method.HEAD) && this.method == Method.GET);
    }

    public abstract void handle(HttpRequest request, HttpResponse response) throws Exception;

    static RouteEntry createFromFilter(String path, Filter filter) {
        return new RouteEntry(null, path) {
            @Override
            public void handle(HttpRequest request, HttpResponse response) throws Exception {
                filter.handle(request, response);
            }
        };
    }

    static RouteEntry createFromRoute(Method method, String path, Route route) {
        return new RouteEntry(method, path) {
            @Override
            public void handle(HttpRequest request, HttpResponse response) throws Exception {
                // Add method check (e.g. if the request method is HEAD and the entry method is GET, the entry will not be executed.)
                if (request.method() == method) {
                    Object body = route.handle(request, response);
                    if (body != null) {
                        response.bodyRaw(SerializationUtils.serialize(body));
                    }
                }
            }
        };
    }
}
