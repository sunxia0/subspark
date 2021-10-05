package org.subspark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.http.Method;
import org.subspark.http.Status;
import org.subspark.utils.IOUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router {
    private final static Logger logger = LogManager.getLogger(Router.class);

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

    private List<RouteEntry> findEntriesFromEntryList(List<RouteEntry> source, Method method, String path) {
        List<RouteEntry> target = new ArrayList<>();
        for (RouteEntry entry : source) {
            if (entry.matches(method, path)) {
                target.add(entry);
            }
        }
        return target;
    }

    private List<RouteEntry> findBeforeEntries(String path) {
        return findEntriesFromEntryList(beforeFilters,null, path);
    }

    private RouteEntry findRouteEntry(Method method, String path) {
        List<RouteEntry> source = routes.get(method);

        for (RouteEntry entry : source) {
            if (entry.matches(method, path)) {
                return entry;
            }
        }

        // If there is no candidate HEAD entry, try to find matching GET entries for HEAD requests.
        if (method == Method.HEAD) {
            source = routes.get(Method.GET);
            for (RouteEntry entry : source) {
                if (entry.matches(method, path)) {
                    return entry;
                }
            }
        }

        return null;
    }

    private List<RouteEntry> findAfterEntries(String path) {
        return findEntriesFromEntryList(afterFilters, null, path);
    }

    public void consume(Request request, Response response) {
        List<RouteEntry> beforeEntries = findBeforeEntries(request.path());
        RouteEntry routeEntry = findRouteEntry(request.method(), request.path());
        List<RouteEntry> afterEntries = findAfterEntries(request.path());

        try {
            // Before filters
            for (RouteEntry entry : beforeEntries) {
                entry.handle(request, response);
            }

            // Route
            if (routeEntry != null) {
                routeEntry.handle(request, response);
            } else {
                response.status(Status.NOT_FOUND);
                response.header("content-type", MimeType.TXT);
                response.body(Status.NOT_FOUND.description());
            }

            // After filters
            for (RouteEntry entry : afterEntries) {
                entry.handle(request, response);
            }
        } catch (Exception e) {
            logger.warn(IOUtils.getStackTraceString(e));
            HaltException halt = e instanceof HaltException ?
                    (HaltException) e : new HaltException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
            response.status(halt.getStatus());
            response.header("content-type", MimeType.TXT);
            response.body(e.getMessage());
        }
    }
}