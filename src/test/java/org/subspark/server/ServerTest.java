package org.subspark.server;

import org.junit.*;
import org.subspark.server.io.HttpParser;
import org.subspark.server.request.Method;
import org.subspark.server.request.Request;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerTest {
    private WebService ws;

    @Before
    public void setUp() {
        ws = new WebService();
        ws.start();
    }

    @After
    public void tearDown() {
        ws.stop();
    }

    @Ignore
    @Test
    public void requestParsingTest() {
        String str = "GET /api/blog/get?id=1&mon=2&day=23 HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "Connection: close\n\n";

        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes());
        Request request = HttpParser.parseRequest(in).toRequest();

        Assert.assertEquals(Method.GET, request.method());
        Assert.assertEquals("/api/blog/get", request.path());
        Assert.assertEquals("id=1&mon=2&day=23", request.queryString());
        Assert.assertEquals("/api/blog/get?id=1&mon=2&day=23", request.uri());
        Assert.assertEquals("HTTP/1.1", request.protocol());

        Map<String, String> queryMap = new HashMap<>(){{
            put("id", "1");
            put("mon", "2");
            put("day", "23");
        }};
        Assert.assertEquals(queryMap.keySet(), request.queryParams());
        for (String k : request.queryParams())
            Assert.assertEquals(queryMap.get(k), request.queryParam(k));

        Map<String, String> headerMap = new HashMap<>(){{
           put("user-agent", "WebSniffer/1.0 (+http://websniffer.cc/)");
           put("host", "localhost");
           put("accept", "*/*");
           put("referer", "https://websniffer.cc/");
           put("connection", "close");
        }};
        Assert.assertEquals(headerMap.keySet(), request.headers());
        for (String k : request.headers())
            Assert.assertEquals(headerMap.get(k), request.header(k));
    }

    private void connect(String req) throws Exception {
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
        System.out.println();
    }

    @Ignore
    @Test
    public void getTest() throws Exception {
        String header = "GET / HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "Connection: close\n\n";
        System.out.println("===== Get Test =====");
        connect(header);
        System.out.println("===== Get Test =====");
        System.out.println();
    }

    @Ignore
    @Test
    public void headTest() throws Exception {
        String header = "HEAD / HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "Connection: close\n\n";
        System.out.println("===== Head Test =====");
        connect(header);
        System.out.println("===== Head Test =====");
        System.out.println();
    }

    @Ignore
    @Test
    public void absoluteURLTest() throws Exception {
        String header = "GET http://localhost:8080/ HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "Connection: close\n\n";
        System.out.println("===== Absolute URL Test =====");
        connect(header);
        System.out.println("===== Absolute URL Test =====");
        System.out.println();
    }

    @Ignore
    @Test
    public void ifModifiedSinceTest() throws Exception {
        String header = "GET / HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "If-Modified-Since: Wed, 22 Jan 2021 19:10:41 GMT\n" +
                "Connection: close\n\n";
        System.out.println("===== If Modified Since Test =====");
        connect(header);
        System.out.println("===== If Modified Since Test =====");
        System.out.println();
    }

    @Ignore
    @Test
    public void ifUnModifiedSinceTest() throws Exception {
        String header = "GET / HTTP/1.1\n" +
                "User-Agent: WebSniffer/1.0 (+http://websniffer.cc/)\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "Referer: https://websniffer.cc/\n" +
                "If-Unmodified-Since: Wed, 19 Jan 2021 19:10:41 GMT\n" +
                "Connection: close\n\n";
        System.out.println("===== If UnmodifiedSince Test =====");
        connect(header);
        System.out.println("===== If UnmodifiedSince Test =====");
        System.out.println();
    }

    @Ignore
    @Test
    public void chunkedTransferTest() throws Exception {
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
        System.out.println("===== Absolute URL Test =====");
        connect(req);
        System.out.println("===== Absolute URL Test =====");
        System.out.println();
    }
}
