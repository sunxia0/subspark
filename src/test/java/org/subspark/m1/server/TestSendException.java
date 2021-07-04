//package org.subspark.m1.server;
//
//import java.io.*;
//import java.net.Socket;
//import java.nio.charset.StandardCharsets;
//
//import static org.junit.Assert.*;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import org.subspark.TestHelper;
//import org.subspark.server.exceptions.HaltException;
//import org.subspark.server.handling.HttpIoHandler;
//
//import org.apache.logging.log4j.Level;
//
//public class TestSendException {
//    @Before
//    public void setUp() {
//        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.DEBUG);
//    }
//
//    String sampleGetRequest =
//        "GET /a/b/hello.htm?q=x&v=12%200 HTTP/1.1\r\n" +
//        "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
//        "Host: www.cis.upenn.edu\r\n" +
//        "Accept-Language: en-us\r\n" +
//        "Accept-Encoding: gzip, deflate\r\n" +
//        "Cookie: name1=value1; name2=value2; name3=value3\r\n" +
//        "Connection: Keep-Alive\r\n\r\n";
//
//    @Ignore
//    @Test
//    public void testSendException() throws IOException {
//        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        Socket s = TestHelper.getMockSocket(
//            sampleGetRequest,
//            byteArrayOutputStream);
//
//        HaltException halt = new HaltException(404, "Not found");
//
//        HttpIoHandler.sendException(s, null, halt);
//        String result = byteArrayOutputStream.toString(StandardCharsets.UTF_8).replace("\r", "");
//        System.out.println(result);
//
//        assertTrue(result.startsWith("HTTP/1.1 404"));
//    }
//
//    @After
//    public void tearDown() {}
//}
