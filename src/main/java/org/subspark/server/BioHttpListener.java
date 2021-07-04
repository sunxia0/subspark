package org.subspark.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Stub for your HTTP server, which listens on a ServerSocket and handles
 * requests
 */
public class BioHttpListener implements Runnable {
    private final static Logger logger = LogManager.getLogger(BioHttpListener.class);

    private final static int BACK_LOG = 1024;

    private final WebService service;

    private ServerSocket serverSocket;
    private Thread listenThread;

    public BioHttpListener(WebService service) {
        this.service = service;
        this.initListener();
    }

    private void initListener() {
        try {
            this.serverSocket = new ServerSocket();
            this.serverSocket.bind(new InetSocketAddress(service.ipAddress(), service.port()), BACK_LOG);
            this.listenThread = new Thread(this);
        } catch (IOException e) {
            logger.error("An error occurred when initializing server socket", e);
        }
    }

    public void listen() {
        listenThread.start();
    }

    public void stop() {
        listenThread.interrupt();
    }

    @Override
    public void run() {
        try {
            logger.info(String.format("Server starts listening on %s:%d",
                    service.ipAddress(), service.port()));

            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();

                logger.info(String.format("Open socket connection - %s:%d",
                        socket.getInetAddress().getHostName(), socket.getPort()));

                service.getIOHandler().handle(socket);
            }
        } catch (IOException e) {
            logger.error("An error occurred when accepting socket", e);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.error("An error occurred when closing server socket", e);
            }
        }
    }
}
