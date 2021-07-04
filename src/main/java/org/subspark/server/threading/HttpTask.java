package org.subspark.server.threading;

import java.net.Socket;

public class HttpTask {
    private String staticFolder;
    private Socket socket;

    public HttpTask(String staticFolder, Socket socket) {
        this.staticFolder = staticFolder;
        this.socket = socket;
    }

    public String getStaticFolder() {
        return staticFolder;
    }

    public Socket getSocket() {
        return socket;
    }

}
