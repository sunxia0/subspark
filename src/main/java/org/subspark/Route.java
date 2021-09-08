package org.subspark;


/**
 * A Route Handler is called when an HTTP request maps to the assigned route. It
 * is given Request info.
 */
@FunctionalInterface
public interface Route {
    Object handle(Request request, Response response) throws Exception;
}
