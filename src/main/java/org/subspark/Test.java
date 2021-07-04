package org.subspark;

import org.subspark.server.io.HttpParser;
import org.subspark.server.request.Request;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Test {
    private static void testParseRequest() {
        String str = "GET /api/blog/get?id=1&mon=2&day=23 HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "Connection: Close\n\n";

        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes());
        Request request = HttpParser.parseRequest(in);

        System.out.println(request.method());
        System.out.println(request.path());
        System.out.println(request.queryString());
        System.out.println(request.uri());
        System.out.println(request.protocol());

        System.out.println(request.queryParams());
        for (String k : request.queryParams())
            System.out.println(k + ": " + request.queryParam(k));

        System.out.println(request.headers());
        for (String k : request.headers())
            System.out.println(k + ": " + request.header(k));
    }

    private static void connectTest() throws Exception {
        Socket socket = new Socket("localhost", 8080);

        String header = "GET / HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "Connection: Close\n\n";

        byte[] bodyBytes = new byte[1000];
        Arrays.fill(bodyBytes, (byte) 97);
        String body = new String(bodyBytes);

        String req = header + body;

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        out.write(req.getBytes());
        out.flush();

        byte[] bytes = new byte[8192];
        int readBytes;
        while ((readBytes = in.read(bytes)) > 0) {
            System.out.println(readBytes);
            System.out.println(new String(bytes, 0, readBytes));
        }
    }

    public static void main(String[] args) throws Exception {
        connectTest();
    }
}
