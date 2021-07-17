package org.subspark;

import org.subspark.server.io.HttpParser;
import org.subspark.server.request.Request;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

public class Test {
    private static void testParseRequest() {
        String str = "GET /api/blog/get?id=1&mon=2&day=23 HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "Connection: Close\n\n";

        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes());
        Request request = HttpParser.parseRequest(in).toRequest();

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

    private static void connect(String req) throws Exception {
        Socket socket = new Socket("localhost", 8080);

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        out.write(req.getBytes());
        out.flush();

        byte[] bytes = new byte[8192];
        int readBytes;
        while ((readBytes = in.read(bytes)) > 0) {
            System.out.print(new String(bytes, 0, readBytes));
        }
    }

    private static void getTest() throws Exception {
        String header = "GET / HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "Connection: close\n\n";
        connect(header);
    }

    private static void headTest() throws Exception {
        String header = "HEAD / HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "Connection: close\n\n";
        connect(header);
    }

    private static void absoluteURLTest() throws Exception {
        String header = "GET http://localhost:8080/ HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "Connection: close\n\n";
        connect(header);
    }

    private static void ifModifiedSinceTest() throws Exception {
        String header = "GET / HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "If-Modified-Since: Wed, 22 Jan 2021 19:10:41 GMT\n" +
                "Connection: close\n\n";
        connect(header);
    }

    private static void ifUnModifiedSinceTest() throws Exception {
        String header = "GET / HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "If-Unmodified-Since: Wed, 19 Jan 2021 19:10:41 GMT\n" +
                "Connection: close\n\n";
        connect(header);
    }

    private static void chunkedTransferTest() throws Exception {
        // Test with breakpoints
        String req = "POST / HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "Transfer-Encoding: chunked\n" +
                "Connection: close\n" +
                "\n" +
                "1a; ignore-stuff-here\r\n" +
                "abcdefghijklmnopqrstuvwxyz\r\n" +
                "10\r\n" +
                "1234567890abcdef\r\n" +
                "0\r\n" +
                "footer1 : val1\r\n" +
                "footer2: val2\r\n";
        connect(req);
    }

    public static void main(String[] args) throws Exception {
        chunkedTransferTest();
    }
}
