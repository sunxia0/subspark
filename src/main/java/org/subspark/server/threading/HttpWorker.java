//package org.subspark.server.threading;
//
//
//import org.subspark.server.exceptions.HaltException;
//import org.subspark.server.handling.HttpIoHandler;
//import org.subspark.server.request.Method;
//import org.subspark.server.request.Request;
//import org.subspark.server.response.Response;
//
//import java.util.concurrent.BlockingQueue;
//
///**
// * Stub class for a thread worker that handles Web requests
// */
//public class HttpWorker implements Runnable {
//    private HttpWorkerPool parent;
//
//    private Thread workThread;
//    private BlockingQueue<HttpTask> workQueue;
//
//    public HttpWorker(HttpWorkerPool parent) {
//        this.parent = parent;
//        this.workThread = new Thread(this);
//        this.workQueue = parent.getWorkerQueue();
//    }
//
//    public Thread getWorkThread() {
//        return workThread;
//    }
//
//    public void start() {
//        this.workThread.start();
//    }
//
//    @Override
//    public void run() {
//        HttpTask task;
//        while ((task = getTask()) != null) {
//            runTask(task);
//        }
//    }
//
//    private HttpTask getTask() {
//        try {
//            return workQueue.take();
//        } catch (InterruptedException e) {
//            return null;
//        }
//    }
//
//    private void runTask(HttpTask task) {
//        // parse request message
//        // create Request obj
//        Request request = null;
//
//        // create Response obj based on Request
//        Response response = null;
//        HaltException halt = null;
//
//        try {
//            request = HttpIoHandler.recvRequest(task.getSocket());
//            if (request.requestMethod().equals(Method.GET.toString()) &&
//                request.uri().equals("/shutdown")) {
//                response = HttpIoHandler.createShutdownResponse(task.getStaticFolder(), request);
//                parent.shutdown();
//            }
//            else {
//                response = HttpIoHandler.createFileResponse(task.getStaticFolder(), request);
//            }
//        } catch (HaltException e) {
//            halt = e;
//        }
//
//        // create response message
//        // send Response obj
//        if (halt == null)
//            HttpIoHandler.sendResponse(task.getSocket(), request, response);
//        else
//            HttpIoHandler.sendException(task.getSocket(), request, halt);
//    }
//}
