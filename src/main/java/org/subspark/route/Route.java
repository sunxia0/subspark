package org.subspark.route;

import org.subspark.server.request.HttpRequest;
import org.subspark.server.response.HttpResponse;

/**
 * A Route Handler is called when an HTTP request maps to the assigned route. It
 * is given Request info.
 */
@FunctionalInterface
public interface Route {

    /**
     * A route handler for a given HTTP request.
     */
    Object handle(HttpRequest request, HttpResponse response) throws Exception;
}
