package org.subspark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.filter.Filter;
import org.subspark.route.Route;
import org.subspark.server.WebService;
import org.subspark.server.exceptions.HaltException;
import org.subspark.server.response.Status;


public class Service extends WebService {
    private final static Logger logger = LogManager.getLogger(Service.class);

    protected Service() {}

    /**
     * Triggers a HaltException that terminates the request
     */
    public HaltException halt() {
        throw new HaltException();
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public HaltException halt(Status status) {
        throw new HaltException(status);
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public HaltException halt(Status status, String body) {
        throw new HaltException(status, body);
    }

    /**
     * Handle an HTTP GET request to the path
     */
    public void get(String path, Route route) {}

    /**
     * Handle an HTTP POST request to the path
     */
    public void post(String path, Route route) {}

    /**
     * Handle an HTTP PUT request to the path
     */
    public void put(String path, Route route) {}

    /**
     * Handle an HTTP DELETE request to the path
     */
    public void delete(String path, Route route) {}

    /**
     * Handle an HTTP HEAD request to the path
     */
    public void head(String path, Route route) {}

    /**
     * Handle an HTTP OPTIONS request to the path
     */
    public void options(String path, Route route) {}

    ///////////////////////////////////////////////////
    // HTTP request filtering
    ///////////////////////////////////////////////////

    /**
     * Add filters that get called before a request
     */
    public void before(Filter filter) {}

    /**
     * Add filters that get called after a request
     */
    public void after(Filter filter) {}

    /**
     * Add filters that get called before a request
     */
    public void before(String path, Filter filter) {}

    /**
     * Add filters that get called after a request
     */
    public void after(String path, Filter filter) {}
}
