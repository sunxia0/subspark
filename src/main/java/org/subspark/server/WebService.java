package org.subspark.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WebService {
    private final static Logger logger = LogManager.getLogger(WebService.class);

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

    protected WebService() {}

    public static void main(String[] args) {
        WebService ws = new WebService();
        ws.start();
    }

    /**
     * Launches the Web server thread pool and the listener
     */
    public void start() {
        this.listener = new BioHttpListener(this);
        this.ioHandler = new BioHttpHandler(this);
        this.requestHandler =  new RequestHandler(this);
        this.staticFilesHandler = new StaticFilesHandler(this);

        this.staticFilesHandler.staticFileLocation(staticFileLocation);
        this.listener.listen();
        logger.info("WebService has started");
    }

    /**
     * Gracefully shut down the server
     */
    public void stop() {
        this.listener.stop();
        this.ioHandler.shutdown();
        logger.info("WebService has stopped");
    }

    /**
     * Hold until the server is fully initialized.
     * Should be called after everything else.
     */
    public void awaitInitialization() {
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
}
