## SubSpark

SubSpark is a light-weighted microservice framework motivated by [cis 455 hw1](https://www.cis.upenn.edu/~cis455/assignments.html) and [Spark](http://sparkjava.com/). 

It provides a built-in small HTTP server which supports HTTP/1.1 and a framework to create RESTful microservice applications.

### Development

* SparkController
  * WebService
    * ContextHandler
      * Context
    * HttpWorkerPool
      * WorkerQueue
      * HttpWorker
        * HttpIoHandler
          * Request, Response
          * Session (session token security)
          * Route (with wildcards) (RouteWrapper?), Filter
          * Process HaltException
          * Process redirect (HttpRedirector?)
          * RequestHandler
    * HttpListener
      * HttpTask
* Special URL /shutdown /control (with err log)

### Test

- Using Mockito
- Stress test: Apachebench