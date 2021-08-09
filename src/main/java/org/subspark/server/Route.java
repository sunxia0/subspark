package org.subspark.server;

import org.subspark.server.HttpRequest;
import org.subspark.server.HttpResponse;

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
