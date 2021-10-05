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

### Reference

- [MDN Web Docs - HTTP response status codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
- [MDN Web Docs - HTTP cookies](https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies)
- [MDN Web Docs - Set-Cookie](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie)
- [MDN Web Docs - Redirections](https://developer.mozilla.org/en-US/docs/Web/HTTP/Redirections)
- [RFC 7231 - Request Methods](https://greenbytes.de/tech/webdav/rfc7231.html#methods)
- [RFC 2616](https://datatracker.ietf.org/doc/html/rfc2616)
- [HTTP Made Really Easy](https://jmarshall.com/easy/http/)
- [Documentation - Spark Framework](https://sparkjava.com/documentation)