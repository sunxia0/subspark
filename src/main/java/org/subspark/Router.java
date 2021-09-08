package org.subspark;

import org.subspark.http.Method;
import org.subspark.http.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router {
    private final List<RouteEntry> beforeFilters;
    private final List<RouteEntry> afterFilters;
    private final Map<Method, List<RouteEntry>> routes;

    Router() {
        beforeFilters = new ArrayList<>();
        afterFilters = new ArrayList<>();

        routes = new HashMap<>();
        routes.put(Method.HEAD, new ArrayList<>());
        routes.put(Method.GET, new ArrayList<>());
        routes.put(Method.POST, new ArrayList<>());
        routes.put(Method.PUT, new ArrayList<>());
        routes.put(Method.DELETE, new ArrayList<>());
        routes.put(Method.OPTIONS, new ArrayList<>());
        routes.put(Method.CONNECT, new ArrayList<>());
        routes.put(Method.TRACE, new ArrayList<>());
    }

    public void addBeforeFilter(String path, Filter filter) {
        RouteEntry entry = RouteEntry.createFromFilter(path, filter);
        beforeFilters.add(entry);
    }

    public void addAfterFilter(String path, Filter filter) {
        RouteEntry entry = RouteEntry.createFromFilter(path, filter);
        afterFilters.add(entry);
    }

    public void addRoute(Method method, String path, Route route) {
        RouteEntry entry = RouteEntry.createFromRoute(method, path, route);
        routes.get(method).add(entry);
    }

    private void findEntriesFromEntryList(List<RouteEntry> source, Method method, String path, List<RouteEntry> target) {
        for (RouteEntry entry : source) {
            if (entry.matches(method, path)) {
                target.add(entry);
            }
        }
    }

    private List<RouteEntry> findBeforeEntries(String path) {
        List<RouteEntry> beforeEntries = new ArrayList<>();
        findEntriesFromEntryList(beforeFilters, null, path, beforeEntries);
        return beforeEntries;
    }

    private List<RouteEntry> findRouteEntries(Method method, String path) {
        List<RouteEntry> routeEntries = new ArrayList<>();
        findEntriesFromEntryList(routes.get(method), method, path, routeEntries);

        // If there is no candidate HEAD entry, try to find matching GET entries for HEAD requests.
        if (method == Method.HEAD && routeEntries.isEmpty()) {
            findEntriesFromEntryList(routes.get(Method.GET), method, path, routeEntries);
        }

        return routeEntries;
    }

    private List<RouteEntry> findAfterEntries(String path) {
        List<RouteEntry> afterEntries = new ArrayList<>();
        findEntriesFromEntryList(afterFilters, null, path, afterEntries);
        return afterEntries;
    }

    public void consume(Request request, Response response) {
        List<RouteEntry> beforeEntries = findBeforeEntries(request.path());
        List<RouteEntry> routeEntries = findRouteEntries(request.method(), request.path());
        List<RouteEntry> afterEntries = findAfterEntries(request.path());

        try {
            // Before filters
            for (RouteEntry entry : beforeEntries) {
                entry.handle(request, response);
            }

            // Routes
            for (RouteEntry entry : routeEntries) {
                entry.handle(request, response);
            }

            // After filters
            for (RouteEntry entry : afterEntries) {
                entry.handle(request, response);
            }
        } catch (Exception e) {
            HaltException halt = e instanceof HaltException ?
                    (HaltException) e : new HaltException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
            response.status(halt.getStatus());
            response.header("content-type", MimeType.TXT);
            response.body(e.getMessage());
            return;
        }

        if (routeEntries.isEmpty()) {
            response.status(Status.NOT_FOUND);
            response.header("content-type", MimeType.TXT);
            response.body(Status.NOT_FOUND.description());
        }
    }
}