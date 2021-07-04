/**
 * CIS 455/555 route-based HTTP framework
 *
 * V. Liu, Z. Ives
 *
 * Portions excerpted from or inspired by Spark Framework,
 *
 *                 http://sparkjava.com,
 *
 * with license notice included below.
 */

/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, softwareƒ
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.subspark.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.subspark.filter.Filter;
import org.subspark.route.Route;
import org.subspark.server.exceptions.HaltException;
import org.subspark.server.handling.RequestHandler;
import org.subspark.server.io.BioHttpHandler;


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

    private final BioHttpListener listener = new BioHttpListener(this);
    private final BioHttpHandler ioHandler = new BioHttpHandler(this);
    private final RequestHandler requestHandler =  new RequestHandler(this);

    public BioHttpListener getListener() {
        return listener;
    }

    public BioHttpHandler getIOHandler() {
        return ioHandler;
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public static void main(String[] args) {
        WebService ws = new WebService();
        ws.start();
    }

    /**
     * Launches the Web server thread pool and the listener
     */
    public void start() {
        this.requestHandler.staticFileLocation(staticFileLocation);
        this.listener.listen();
    }

    /**
     * Gracefully shut down the server
     */
    public void stop() {

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

//    /**
//     * Triggers a HaltException that terminates the request
//     */
//    public HaltException halt() {
//        throw new HaltException();
//    }
//
//    /**
//     * Triggers a HaltException that terminates the request
//     */
//    public HaltException halt(int statusCode) {
//        throw new HaltException(statusCode);
//    }
//
//    /**
//     * Triggers a HaltException that terminates the request
//     */
//    public HaltException halt(String body) {
//        throw new HaltException(body);
//    }
//
//    /**
//     * Triggers a HaltException that terminates the request
//     */
//    public HaltException halt(int statusCode, String body) {
//        throw new HaltException(statusCode, body);
//    }

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

    ///////////////////////////////////////////////////
    // For more advanced capabilities
    ///////////////////////////////////////////////////

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
