//package org.subspark.server.threading;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
///**
// * Server thread pool
// */
//public class HttpWorkerPool {
//    final static Logger logger = LogManager.getLogger(HttpWorkerPool.class);
//
//    private final int DEFAULT_POOL_SIZE = 2 * Runtime.getRuntime().availableProcessors();
//    private int poolSize = DEFAULT_POOL_SIZE;
//    private final List<HttpWorker> workers = new ArrayList<>();
//    private final BlockingQueue<HttpTask> workerQueue = new LinkedBlockingQueue<>();
//
//    public HttpWorkerPool() {}
//
//    public HttpWorkerPool(int threads) {
//        this.poolSize = threads;
//        initWorkers();
//    }
//
//    BlockingQueue<HttpTask> getWorkerQueue() {
//        return workerQueue;
//    }
//
//    synchronized void shutdown() {
//        Thread workerThread;
//        for (HttpWorker worker : workers) {
//            workerThread = worker.getWorkThread();
//            logger.info(workerThread.getName() + ": "  + workerThread.getState());
//
//            if (workerThread.getState() == Thread.State.WAITING) {
//                workerThread.interrupt();
//            }
//            if (workerThread.getState() == Thread.State.RUNNABLE) {
//                try {
//                    workerThread.join(1000);
//                } catch (InterruptedException e) {
//                    workerThread.interrupt();
//                }
//            }
//        }
//
////        System.out.println();
////
////        for (HttpWorker worker : workers) {
////            workerThread = worker.getWorkThread();
////            logger.info(workerThread.getName() + ": "  + workerThread.getState());
////        }
//    }
//
//    private void initWorkers() {
//        HttpWorker worker;
//        for (int i = 0; i < poolSize; i++) {
//            worker = new HttpWorker(this);
//            workers.add(worker);
//            worker.start();
//        }
//    }
//
//    public void execute(HttpTask task) {
//        try {
//            workerQueue.put(task);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
