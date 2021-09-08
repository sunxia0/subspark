package org.subspark;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO: Finish NIO I/O Handler
public class NioHttpHandler {
    private final static Logger logger = LogManager.getLogger(NioHttpHandler.class);

    private Service service;
    private ExecutorService executor;

    public NioHttpHandler(Service service) {
        this.service = service;
        this.executor = Executors.newFixedThreadPool(service.threadPool());
    }

    public void handle(SelectionKey key) {
        executor.submit(new HttpTask(key));
    }

    private static class HttpTask implements Runnable {
        private SelectionKey key;
        private SocketChannel socketChannel;

        public HttpTask(SelectionKey key) {
            this.key = key;
            this.socketChannel = (SocketChannel) key.channel();
        }

        @Override
        public void run() {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(8192);
                byte[] bytes;

                int readBytes = socketChannel.read(buffer);

                if (readBytes > 0) {
                    buffer.flip();
                    bytes = new byte[buffer.remaining()];
                    // Extract data from the buffer
                    buffer.get(bytes);

                    // Create string
                    String message = new String(bytes);
                    logger.info("Get message: ");
                    logger.info(message);

                    // Write response
                    buffer.clear();
                    buffer.put("OK!".getBytes());
                    buffer.flip();
                    socketChannel.write(buffer);
                }
            } catch (IOException e) {
                logger.error("An IOException occurred during reading socket channel", e);
            } finally {
                key.cancel();
                if (key.channel() != null) {
                    try {
                        key.channel().close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
