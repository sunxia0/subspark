package org.subspark.server.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.server.WebService;
import org.subspark.server.exceptions.ClosedConnectionException;
import org.subspark.server.exceptions.HaltException;
import org.subspark.server.handling.RequestHandler;
import org.subspark.server.request.RequestBuilder;
import org.subspark.server.response.Response;
import org.subspark.server.response.ResponseBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioHttpHandler {
    private final static Logger logger = LogManager.getLogger(BioHttpHandler.class);

    private final WebService service;
    private final ExecutorService executor;

    public BioHttpHandler(WebService service) {
        this.service = service;
        this.executor = Executors.newFixedThreadPool(service.threadPool());
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    public void handle(Socket socket) {
        executor.execute(new HttpTask(socket));
    }

    private final class HttpTask implements Runnable {
        private final Socket socket;

        public HttpTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            RequestHandler requestHandler = service.getRequestHandler();
            RequestBuilder requestBuilder;
            Response response = null;

            while (!socket.isClosed()) {
                try {
                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();

                    try {
                        requestBuilder = HttpParser.parseRequest(in);

                        logger.info(requestBuilder.method() + " " + requestBuilder.uri());

                        // Send 100 Continue
                        if (requestBuilder.protocol().equals(ResponseBuilder.HTTP_1_1)) {
                            HttpParser.sendResponse(out, ResponseBuilder.of100());
                        }

                        response = requestHandler.handleRequest(requestBuilder);
                    } catch (HaltException e) {
                        response = requestHandler.handleException(e);
                    }

                    HttpParser.sendResponse(out, response);

                } catch (ClosedConnectionException | IOException e) {
                    response = null;
                } finally {
                    if (response == null || response.header("connection").equals(ResponseBuilder.CONNECTION_CLOSE)) {
                        try {
                            socket.close();
                            logger.info("Socket connection closed");
                        } catch (IOException e) {
                            logger.error("An error occurred when closing socket", e);
                        }
                    }
                }
            }
        }
    }
}
