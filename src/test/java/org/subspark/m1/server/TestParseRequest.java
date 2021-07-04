//package org.subspark.m1.server;
//
//
//import org.apache.logging.log4j.Level;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.subspark.TestHelper;
//import org.subspark.server.io.HttpParser;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class TestParseRequest {
//    @Before
//    public void setUp() {
//        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.DEBUG);
//    }
//
//    String sampleGetRequest =
//            "GET /a/b/hello.htm?q=x&v=12%200&q=y HTTP/1.1\r\n" +
//            "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
//            "Host: www.cis.upenn.edu\r\n" +
//            "Accept-Language: en-us\r\n" +
//            "Accept-Encoding: gzip, deflate\r\n" +
//            "Cookie: name1=value1; name2=value2; name3=value3\r\n" +
//            "Connection: Keep-Alive\r\n" +
//            "\r\n" +
//            "This is body";
//
//    @Test
//    public void testDecodeHeader() throws IOException {
//        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        Socket s = TestHelper.getMockSocket(
//                sampleGetRequest,
//                byteArrayOutputStream);
//
//        Map<String, String> pre = new HashMap<>();
//        Map<String, List<String>> params = new HashMap<>();
//        Map<String, String> headers = new HashMap<>();
//        byte[] body = HttpParser.parseRequest(null, s.getInputStream(), pre, headers, params);
//
//        System.out.println("Pre:");
//        for (Map.Entry<String, String> e : pre.entrySet())
//            System.out.println(e.getKey() + ": " + e.getValue());
//        System.out.println();
//
//        System.out.println("Params:");
//        for (Map.Entry<String, List<String>> e : params.entrySet())
//            System.out.println(e.getKey() + ": " + e.getValue());
//        System.out.println();
//
//        System.out.println("Headers:");
//        for (Map.Entry<String, String> e : headers.entrySet())
//            System.out.println(e.getKey() + ": " + e.getValue());
//        System.out.println();
//
//        System.out.println("Body");
//        System.out.println(new String(body, StandardCharsets.UTF_8));
//    }
//
//    @After
//    public void tearDown() {}
//}
