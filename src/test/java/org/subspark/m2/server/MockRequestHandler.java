//package org.subspark.m2.server;
//
//import org.subspark.route.Route;
//import org.subspark.server.exceptions.HaltException;
//import org.subspark.server.request.Request;
//import org.subspark.server.response.Response;
//
//public class MockRequestHandler implements Route {
//
//    @Override
//    public Object handle(Request request, Response response) throws HaltException {
//        response.status(200);
//        response.type("text/html");
//
//        return "<html><head><title>Response</title></head><body><h1>Response</h1><p>Test</p></body></html>";
//    }
//}
