package org.subspark;

import org.subspark.http.Method;
import org.subspark.utils.PathUtils;
import org.subspark.utils.SerializationUtils;

public abstract class RouteEntry {
    private final Method method;
    private final String pathPattern;

    RouteEntry(Method method, String pathPattern) {
        PathUtils.verifyPathPattern(pathPattern);
        this.method = method;
        this.pathPattern = pathPattern;
    }

    public Method getMethod() {
        return method;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public boolean matches(Method method, String path) {
        return PathUtils.isPathMatch(this.pathPattern, path)
                && (this.method == null || method.equals(this.method) || method.equals(Method.HEAD) && this.method == Method.GET);
    }

    public abstract void handle(Request request, Response response) throws Exception;

    static RouteEntry createFromFilter(String path, Filter filter) {
        return new RouteEntry(null, path) {
            @Override
            public void handle(Request request, Response response) throws Exception {
                request.paramsHolder(PathUtils.extractNamedParams(getPathPattern(), request.path()));
                request.wildcardsHolder(PathUtils.extractWildCards(getPathPattern(), request.path()));
                filter.handle(request, response);
            }
        };
    }

    static RouteEntry createFromRoute(Method method, String path, Route route) {
        return new RouteEntry(method, path) {
            @Override
            public void handle(Request request, Response response) throws Exception {
                // Add method check (e.g. if the request method is HEAD and the entry method is GET, the entry will not be executed.)
                if (request.method() == method) {
                    request.paramsHolder(PathUtils.extractNamedParams(getPathPattern(), request.path()));
                    request.wildcardsHolder(PathUtils.extractWildCards(getPathPattern(), request.path()));
                    Object body = route.handle(request, response);
                    if (body != null) {
                        response.bodyRaw(SerializationUtils.serialize(body));
                    }
                }
            }
        };
    }
}
