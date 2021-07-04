package org.subspark.filter;

import org.subspark.server.request.Request;
import org.subspark.server.response.Response;

/**
 * A Filter is called by the Web server to process data before or after the
 * Route Handler is called. This is typically used to attach attributes or to
 * call the HaltException, e.g., if the user is not authorized.
 */
@FunctionalInterface
public interface Filter {
    void handle(Request request, Response response) throws Exception;
}
