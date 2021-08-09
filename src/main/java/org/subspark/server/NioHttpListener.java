package org.subspark.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


//TODO: Finish NIO Listener
public class NioHttpListener {
    private final static Logger logger = LogManager.getLogger(NioHttpListener.class);

    private final static int BACK_LOG = 1024;

    private WebService service;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public NioHttpListener(WebService service) {
        this.service = service;
        this.initListener();
    }

    private void initListener() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            // Bind listening port to server socket and set maximum length of requested queue.
            serverSocketChannel.socket().bind(new InetSocketAddress(service.ipAddress(), service.port()), BACK_LOG);
            serverSocketChannel.configureBlocking(false);
            // Register channel to the selector, interested in socket-accept operations
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            logger.error("An IOException occurred when initializing server", e);
        }

        logger.info("Server starts listening on " + service.port());
    }

    public void listen() {
        int readyChannels;
        while (true) {
            try {
                readyChannels = selector.select();
                if (readyChannels > 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> ite = selectedKeys.iterator();
                    while (ite.hasNext()) {
                        SelectionKey key = ite.next();
                        ite.remove();
                        handleKey(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleKey(SelectionKey key) {
        try {
            if (key.isValid()) {
                if (key.isAcceptable()) { // ServerSocket is acceptable
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();

                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);

                    // Register an accepted socket to the selector
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) { // Socket is readable
                    SocketChannel channel = (SocketChannel) key.channel();
//                    service.getIOHandler().handle(key);
                }
            }
        } catch (IOException e) {
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
