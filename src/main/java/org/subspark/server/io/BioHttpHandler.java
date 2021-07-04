package org.subspark.server.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.server.WebService;
import org.subspark.server.exceptions.ClosedConnectionException;
import org.subspark.server.exceptions.HaltException;
import org.subspark.server.handling.RequestHandler;
import org.subspark.server.request.Request;
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
            Request request = null;
            Response response = null;

            while (!socket.isClosed()) {
                try {
                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();

                    try {
                        request = HttpParser.parseRequest(in);

                        logger.info(request.method() + " " + request.uri());

                        response = requestHandler.handleRequest(request);
                    } catch (HaltException e) {
                        response = requestHandler.handleException(e);
                    }

                    // ==== For test: write 404 ====
                    response = ResponseBuilder.of404();

                    HttpParser.sendResponse(out, response);

                } catch (ClosedConnectionException e) {
                    logger.error("Socket closed when reading or writing", e);
                } catch (IOException e) {
                    logger.error("An error occurred when handling socket", e);
                } finally {
                    if (response == null || response.header("connection").equals(ResponseBuilder.CONNECTION_CLOSE)) {
                        try {
                            socket.close();
                            logger.info("Socket connection close");
                        } catch (IOException e) {
                            logger.error("An error occurred when closing socket", e);
                        }
                    }
                }
            }
        }
    }
}
