package org.subspark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.http.Method;
import org.subspark.http.Status;


public class Service {
    private final static Logger logger = LogManager.getLogger(Service.class);

    private final String DEFAULT_STATIC_DIR = System.getProperty("user.dir") + "/static";
    private final String DEFAULT_HOST = "localhost";
    private final int DEFAULT_PORT = 8080;
    private final int DEFAULT_THREADS = 2 * Runtime.getRuntime().availableProcessors();

    private boolean initialized = false;
    private String staticFileLocation = DEFAULT_STATIC_DIR;
    private String ip = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    private int threads = DEFAULT_THREADS;

    private BioHttpListener listener;
    private BioHttpHandler ioHandler;
    private RequestHandler requestHandler;
    private StaticFilesHandler staticFilesHandler;
    private Router router;

    public BioHttpListener getListener() {
        return listener;
    }

    public BioHttpHandler getIOHandler() {
        return ioHandler;
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public StaticFilesHandler getStaticFilesHandler() {
        return staticFilesHandler;
    }

    public Router getRouter() {
        return router;
    }

    Service() {
        this.staticFilesHandler = new StaticFilesHandler();
        this.router = new Router();
    }

    /**
     * Launches the Web server thread pool and the listener
     */
    private void start() {
        this.listener = new BioHttpListener(this);
        this.ioHandler = new BioHttpHandler(this);
        this.requestHandler =  new RequestHandler(this);
        this.staticFilesHandler.staticFileLocation(staticFileLocation);
        this.listener.listen();
        logger.info("SubSpark Service has started");
    }

    /**
     * Gracefully shut down the server
     */
    public void stop() {
        this.listener.close();
        this.ioHandler.close();
        this.requestHandler.close();
        logger.info("SubSpark Service has stopped");
    }

    /**
     * Hold until the server is fully initialized.
     * Should be called after everything else.
     */
    public void init() {
        logger.info("Initializing server");
        start();
        this.initialized = true;
    }

    ////////////////////////////////////////////
    // Server configuration
    ////////////////////////////////////////////

    /**
     * Set the root directory of the "static web" files
     */
    public void staticFileLocation(String directory) {
        if (this.initialized) {
            throwInitializedException();
        }
        this.staticFileLocation = directory;
    }

    /**
     * Return the root directory of the "static web" files
     */
    public String staticFileLocation() {
        return this.staticFileLocation;
    }

    /**
     * Set the IP address to listen on (default 0.0.0.0)
     */
    public void ipAddress(String ipAddress) {
        if (this.initialized) {
            throwInitializedException();
        }
        this.ip = ipAddress;
    }

    /**
     * Return the IP address to listen on
     */
    public String ipAddress() {
        return this.ip;
    }

    /**
     * Set the TCP port to listen on (default 8080)
     */
    public void port(int port) {
        if (this.initialized) {
            throwInitializedException();
        }
        this.port = port;
    }

    /**
     * Return the TCP port to listen to
     */
    public int port() {
        return this.port;
    }

    /**
     * Set the size of the thread pool
     */
    public void threadPool(int threads) {
        if (this.initialized) {
            throwInitializedException();
        }
        this.threads = threads;
    }

    /**
     * Return the size of the thread pool
     */
    public int threadPool() {
        return this.threads;
    }

    ////////////////////////////////////////////
    // Internal Use
    ////////////////////////////////////////////
    private void throwInitializedException() {
        throw new IllegalStateException("This method should be invoked before the initialization of server.");
    }

    ////////////////////////////////////////////
    // Halting
    ////////////////////////////////////////////

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

    ////////////////////////////////////////////
    // Route / Filter
    ////////////////////////////////////////////
    private void addBeforeFilter(String path, Filter filter) {
        this.router.addBeforeFilter(path, filter);
    }

    private void addAfterFilter(String path, Filter filter) {
        this.router.addAfterFilter(path, filter);
    }

    private void addRoute(Method method, String path, Route route) {
        this.router.addRoute(method, path, route);
    }

    /**
     * Handle an HTTP GET request to the path
     */
    public void get(String path, Route route) {
        addRoute(Method.GET, path, route);
    }

    /**
     * Handle an HTTP POST request to the path
     */
    public void post(String path, Route route) {
        addRoute(Method.POST, path, route);
    }

    /**
     * Handle an HTTP PUT request to the path
     */
    public void put(String path, Route route) {
        addRoute(Method.PUT, path, route);
    }

    /**
     * Handle an HTTP DELETE request to the path
     */
    public void delete(String path, Route route) {
        addRoute(Method.DELETE, path, route);
    }

    /**
     * Handle an HTTP HEAD request to the path
     */
    public void head(String path, Route route) {
        addRoute(Method.HEAD, path, route);
    }

    /**
     * Handle an HTTP OPTIONS request to the path
     */
    public void options(String path, Route route) {
        addRoute(Method.OPTIONS, path, route);
    }

    /**
     * Add filters that get called before a request
     */
    public void before(String path, Filter filter) {
        addBeforeFilter(path, filter);
    }

    /**
     * Add filters that get called after a request
     */
    public void after(String path, Filter filter) {
        addAfterFilter(path, filter);
    }
}
