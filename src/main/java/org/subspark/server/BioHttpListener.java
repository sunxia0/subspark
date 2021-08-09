package org.subspark.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


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
        try {
            serverSocket.close();
            listenThread.join();
        } catch (InterruptedException | IOException e) {
            logger.error("An error occurred when terminating the listener");
        }
    }

    @Override
    public void run() {
        logger.info(String.format("Server starts listening on %s:%d",
                service.ipAddress(), service.port()));

        while (true) {
            Socket socket;

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                break;
            }

            logger.info(String.format("Open socket connection - %s:%d",
                    socket.getInetAddress().getHostName(), socket.getPort()));

            service.getIOHandler().handle(socket);
        }

        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.error("An error occurred when closing server socket", e);
            }
        }
    }
}
