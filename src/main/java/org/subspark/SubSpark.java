package org.subspark;

import org.subspark.route.Route;
import org.subspark.filter.Filter;
import org.subspark.server.Session;

import org.subspark.server.WebService;
import org.subspark.server.exceptions.HaltException;

public class SubSpark {

    // We don't want people to use the constructor
    protected SubSpark() {}

    /**
     * Handle an HTTP GET request to the path
     */
    public static void get(String path, Route route) {
        throw new UnsupportedOperationException();
    }

    /**
     * Handle an HTTP POST request to the path
     */
    public static void post(String path, Route route) {
        throw new UnsupportedOperationException();
    }

    /**
     * Handle an HTTP PUT request to the path
     */
    public static void put(String path, Route route) {
        throw new UnsupportedOperationException();
    }

    /**
     * Handle an HTTP DELETE request to the path
     */
    public static void delete(String path, Route route) {
        throw new UnsupportedOperationException();
    }

    /**
     * Handle an HTTP HEAD request to the path
     */
    public static void head(String path, Route route) {
        throw new UnsupportedOperationException();
    }

    /**
     * Handle an HTTP OPTIONS request to the path
     */
    public static void options(String path, Route route) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    /**
     * Add filters that get called after a request
     */
    public static void after(String path, Filter... filters) {
        throw new UnsupportedOperationException();
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt() {
        throw new UnsupportedOperationException();
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt(int statusCode, String body) {
        throw new UnsupportedOperationException();
    }

    ////////////////////////////////////////////
    // Server configuration
    ////////////////////////////////////////////

    /**
     * Set the IP address to listen on (default 0.0.0.0)
     */
    public static void ipAddress(String ipAddress) {
        getInstance().ipAddress(ipAddress);
    }

    /**
     * Set the port to listen on (default 45555)
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

    private static WebService getInstance() {
        return WebServiceHolder.INSTANCE;
    }

    private static class WebServiceHolder {
        private static final WebService INSTANCE = new WebService();
    }
}
