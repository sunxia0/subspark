package org.subspark;

import org.subspark.http.Status;

public class SubSpark {

    private SubSpark() {}

    /**
     * Handle an HTTP GET request to the path
     */
    public static void get(String path, Route route) {
        getInstance().get(path, route);
    }

    /**
     * Handle an HTTP POST request to the path
     */
    public static void post(String path, Route route) {
        getInstance().post(path, route);
    }

    /**
     * Handle an HTTP PUT request to the path
     */
    public static void put(String path, Route route) {
        getInstance().put(path, route);
    }

    /**
     * Handle an HTTP DELETE request to the path
     */
    public static void delete(String path, Route route) {
        getInstance().delete(path, route);
    }

    /**
     * Handle an HTTP HEAD request to the path
     */
    public static void head(String path, Route route) {
        getInstance().head(path, route);
    }

    /**
     * Handle an HTTP OPTIONS request to the path
     */
    public static void options(String path, Route route) {
        getInstance().options(path, route);
    }

    /**
     * Handle an HTTP CONNECT request to the path
     */
    public static void connect(String path, Route route) {
        getInstance().connect(path, route);
    }

    /**
     * Handle an HTTP TRACE request to the path
     */
    public static void trace(String path, Route route) {
        getInstance().trace(path, route);
    }

    ///////////////////////////////////////////////////
    // HTTP request filtering
    ///////////////////////////////////////////////////

    /**
     * Add filters that get called before a request
     */
    public static void before(Filter... filters) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add filters that get called after a request
     */
    public static void after(Filter... filters) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add filters that get called before a request
     */
    public static void before(String path, Filter... filters) {
        for (Filter filter : filters) {
            getInstance().before(path, filter);
        }
    }

    /**
     * Add filters that get called after a request
     */
    public static void after(String path, Filter... filters) {
        for (Filter filter : filters) {
            getInstance().after(path, filter);
        }
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt() {
        throw getInstance().halt();
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt(Status status) {
        throw getInstance().halt(status);
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt(Status status, String body) {
        throw getInstance().halt(status, body);
    }

    ////////////////////////////////////////////
    // Server configuration
    ////////////////////////////////////////////

    /**
     * Set the IP address to listen on (default localhost)
     */
    public static void ipAddress(String ipAddress) {
        getInstance().ipAddress(ipAddress);
    }

    /**
     * Set the port to listen on (default 8080)
     */
    public static void port(int port) {
        getInstance().port(port);
    }

    /**
     * Set the size of the thread pool
     */
    public static void threadPool(int threads) {
        getInstance().threadPool(threads);
    }

    /**
     * Set the root directory of the "static web" files
     */
    public static void staticFileLocation(String directory) {
        getInstance().staticFileLocation(directory);
    }

    /**
     * Hold until the server is fully initialized
     */
    public static void awaitInitialization() {
        getInstance().awaitInitialization();
    }

    /**
     * Gracefully shut down the server
     */
    public static void stop() {
        getInstance().stop();
    }

    public static String createSession() {
        throw new UnsupportedOperationException();
    }

    public static Session getSession(String id) {
        throw new UnsupportedOperationException();
    }

    ////////////////////////////////////////////
    // Singleton Service Instance
    ////////////////////////////////////////////

    private static Service getInstance() {
        return ServiceHolder.INSTANCE;
    }

    private static class ServiceHolder {
        private static final Service INSTANCE = new Service();
    }
}
