package org.subspark.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.server.http.Method;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioHttpHandler {
    private final static Logger logger = LogManager.getLogger(BioHttpHandler.class);

    private final WebService service;
    private final ExecutorService executor;
    private final List<HttpTask> tasks;

    public BioHttpHandler(WebService service) {
        this.service = service;
        this.executor = Executors.newFixedThreadPool(service.threadPool());
        this.tasks = new ArrayList<>();
    }

    public void handle(Socket socket) {
        HttpTask task = new HttpTask(socket);
        executor.execute(task);
        tasks.add(task);
    }

    public void removeTask(HttpTask task) {
        tasks.remove(task);
    }

    public void close() {
        executor.shutdownNow();
        for (HttpTask task : tasks) {
            task.closeTask();
        }
    }

    private final class HttpTask implements Runnable {
        private final Socket socket;

        public HttpTask(Socket socket) {
            this.socket = socket;
        }

        public void closeTask() {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                    logger.info(String.format("Close socket connection - %s:%d",
                            socket.getInetAddress().getHostName(), socket.getPort()));
                } catch (IOException e) {
                    logger.error("An error occurred when closing socket", e);
                }
            }
        }

        @Override
        public void run() {
            RequestHandler requestHandler = service.getRequestHandler();
            HttpRequest request = null;
            HttpResponse response = null;

            while (!socket.isClosed()) {
                try {
                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();

                    try {
                        request = HttpParser.parseRequest(in);

                        // Send 100 Continue
                        if (request.protocol().equals(Constant.HTTP_1_1)) {
                            HttpParser.sendResponse(out, RequestResponseFactory.of100(), false);
                        }

                        response = requestHandler.handleRequest(request);
                    } catch (HaltException e) {
                        response = requestHandler.handleException(e);
                    }

                    boolean withBody = (request == null || !request.method().equals(Method.HEAD));
                    HttpParser.sendResponse(out, response, withBody);
                } catch (IOException e) {
                    response = null;
                } finally {
                    if (response == null || response.header("connection").equals(Constant.CONNECTION_CLOSE)) {
                        closeTask();
                    }
                }
            }
            removeTask(this);
        }
    }
}
