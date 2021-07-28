package org.subspark.server.filter;

import org.subspark.server.request.HttpRequest;
import org.subspark.server.response.HttpResponse;

/**
 * A Filter is called by the Web server to process data before or after the
 * Route Handler is called. This is typically used to attach attributes or to
 * call the HaltException, e.g., if the user is not authorized.
 */
@FunctionalInterface
public interface Filter {
    void handle(HttpRequest request, HttpResponse response) throws Exception;
}
