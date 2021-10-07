## What is SubSpark

SubSpark is a light-weighted microservice framework motivated by [cis 455 hw1](https://www.cis.upenn.edu/~cis455/assignments.html) and [Spark](http://sparkjava.com/). 

It provides a built-in small HTTP server which supports HTTP/1.1 and a framework to build RESTful microservice applications.

(p.s. **DO NOT** use it in production environment! > _ < )

(p.p.s. If you want a robuster framework, check [Spark](http://sparkjava.com/) please as SubSpark only implemented part of its features.)

## Example

```java
import static org.subspark.SubSpark.*;

public class WebServer {
    public static void main(String[] args) {
        // Create a route for handling HTTP requests
        get("/hello", (req, res) -> "Hello, World!");
        
        // Start the service
        init();
    }
}
```

Send an GET request to `http://localhost:8080/hello` and see what will happen.

## Core API

`org.subspark.SubSpark` provides many static methods for building a RESTful microservice application.

### Configuration

```java
void ipAddress(String ipAddress);
```

Set ip address of the host. 

**Defaut: `localhost`**

```java
void port(int port);
```

Set listening port of the server.

**Default: `8080`**

```java
void threadPool(int threads);
```

Set the number of listening threads of the HTTP server.

**Default: `2 * Runtime.getRuntime().availableProcessors()`**

```java
void staticFileLocation(String directory);
```

Set the root directory of static resources. If you want SubSpark to serve static resources, remember to check this configuration.

**Default: `/{your project directory}/static`**

### Route

[Example](#example) shows a simple route. A route consists of 3 parts:

* An HTTP verb ( `GET`, `POST`, `PUT` and so on)

* A path (like `/blog`, `/comment` )

  SubSpark supports 2 special path: named parameter and wildcard.

  * *Named paramter*

    A path like `/blog/:id` contains a named parameter `:id` which can be accessed in `Request`.

  * *Wildcard*

    A path like `/say/*/to/*` contains 2 wildcards which can be accessed in `Request` as well.

* A `Route` for processing the matching request

  ```java
  @FunctionalInterface
  public interface Route {
      Object handle(Request request, Response response) throws Exception;
  }
  ```

  The definition of interface `Route` shows above. You need to override `handle(Request request, Response response)` to process requests. Returned object (usually a `String` object) will be serialized and become body of the HTTP response.

SubSpark provides 6 HTTP verbs (GET, POST, PUT, DELETE, HEAD, OPTIONS) to build a RESTful microservice application.

```java
get(String path, (req, res) -> {
  // ...
});

post(String path, (req, res) -> {
  // ...
});

put(String path, (req, res) -> {
  // ...
});

delete(String path, (req, res) -> {
  // ...
});

head(String path, (req, res) -> {
  // ...
});

options(String path, (req, res) -> {
  // ...
});
```

Each HTTP verb has its semantics in RESTful style:

* **GET**

  GET is usually used for RETRIVING resources. If the location of a resource has changed, consider using 302 or 307 response for redirection based on the actual situation. SubSpark also provides api for redirection, see [Request](#request).

* **POST**

  POST is usually used for CREATING resources. If the resource to create exists, consider using 303 (See Other) response. Otherwise, 201 (Created) response can be used.

* **PUT**

  PUT is usually used for UPDATING resources.

* **DELETE**

  DELETE is usually used for DELETING resources. Consider using 204 (No Content) response to identify that the resource has been deleted.

* **HEAD**

  HEAD is usually used to get meta information of resources. If you want to create a route to get only meta information of a resource, use `head(String path, Route route)` rather than `get(String path, Route route)`.

* **OPTIONS**

  OPTIONS is usually used for returning configuration information of the application.

The matching order of routes coresponds to their definition order.

### Filter

As the name shows, Filters are used for filtering requests. There are two kinds of filters in SubSpark, which are before filter and after filter.  The running order of before filter, route and after filter is:

**Before filters** ➔ **Routes** ➔ **After filters**

Just like route, a filter also consists of 3 parts:

* A filter type ( `before` / `after` )

* A path (the same as that in [Route](#route))

* A `Filter` for processing the matching requests

  ```java
  @FunctionalInterface
  public interface Filter {
      void handle(Request request, Response response) throws Exception;
  }
  ```

  Like `Route`, you need to override `handle(Request request, Response response)` to implement your business process. Different from `Route`, the `handle` method in `Filter` has no return value.

SubSpark provides 2 api to create filters.

```java
before(String path, (req, res) -> {
  // filter 1 for path
}, (req, res) -> {
  // filter 2 for path
}, ...);

after(String path, (req, res) -> {
  // filter 1 for path
}, (req, res) -> {
  // filter 2 for path
}, ...);
```

The matching order of filters also coresponds to their definition order, just like the rule in route.

### Exception

`halt` is used to trigger exception in a route / filter. It will ignore latter business process by throwing a `HaltException` and a corresponding HTTP response will be sent back.

SubSpark provides 3 api to `halt` routes / filters.

```java
void halt();
void halt(Status status);
void halt(Status status, String body);
```

You can custom the status and body of the HTTP response triggered by `halt`.

### Start and stop

```java
void init();
```

By calling `init()`, SubSpark will load all configurations / routes / filters and start an HTTP server. 

Any configuration after `init()` will trigger an `IllegalStateException`.

```java
void stop();
```

The calling of `stop()` will terminate the HTTP server. You may use this method in a `Route` under certain circumstances.

## Request

`Request` provides convenient api to access all information in an HTTP request.

```java
Method method();
```

Return the request method.

```java
String path();
```

Return the path (without query parameters).

```java
String uri();
```

Return the URI up to the query string.

```java
String queryParam(String param);
```

 Return value of the query parameter.

```java
String queryParamOrDefault(String param, String def);
```

Specify default value if the parameter does not exist.

```java
Set<String> queryParams();
```

Return names of all query parameters.

```java
String queryString();
```

Return the raw query string.

```java
String protocol();
```

Return the protocol name and version from the request.

```java
String header(String name);
```

Return the item from the header.

```java
Set<String> headers();
```

Return names of all headers.

```java
String body();
```

Return the request body sent by the client (encoded with UTF-8).

```java
byte[] bodyRaw();
```

Return the request body bytes.

```java
String cookie(String name);
```

Return the value of cookie with specified name.

```java
Map<String, String> cookies();
```

Return all cookie pairs.

```java
Session session();
```

Return the session associated with this request. If no valid session found, a new `Session` object will be created and returned.

```java
Session session(boolean createIfNone);
```

Return the session associated with this request. If `createIfNone` is `true`, the method equals `session()` . If `createIfNone` is `false`, `null` will be returned if no valid session found.

```java
void attribute(String attrib, Object val);
```

Add an attribute to the request (eg in a filter) so that latter route entry can use it.

```java
Object attribute(String attrib);
```

Return the attribute attached to the request.

```java
Set<String> attributes();
```

Return names of all attributes attached to the request.

```java
String namedParam(String param);
```

Return the specified named parameter.

```java
Map<String, String> namedParams();
```

Return name-value pairs of all named parameter.

```java
List<String> wildcards();
```

Return a list containing all wildcards.

### Session

The `Session` object associated with a `Request` can be accessed with 2 api in `Request` :

```java
Session session();
Session session(boolean createIfNone);
```

`Seesion` provides a stateful mechanism in stateless HTTP protocol. You can put some objects in `Session` so that they can be accessed by latter requests.

Api can be used in `Session` :

```java
/**
* Id of this Session object
*/
String id();

/**
 * Time the session was created
 */
long creationTime();

/**
 * Time the session was last accessed
 */
long lastAccessedTime();

/**
 * Test if the session is valid
 */
boolean isValid();

/**
 * Invalidate the session
 */
void invalidate();

/**
 * Get the inactivity timeout
 */
int maxInactiveInterval();

/**
 * Set the inactivity timeout
 */
void maxInactiveInterval(int interval);

/**
 * Notify the session that it was just accessed
 */
void access();

/**
 * Store an object under the name
 */
void attribute(String name, Object value);

/**
 * Get an object associated with the name
 */
Object attribute(String name);

/**
 * Get all objects bound to the session
 */
Set<String> attributes();

/**
 * Delete an object from the session
 */
void removeAttribute(String name);
```

## Response

Just like`Request` , `Response` encapsules details of the HTTP response. It provides the following api :

```java
void header(String header, String value);
```

Add a header to response.

```java
void status(Status status);
```

Set status of response.

```java
void bodyRaw(byte[] b);
```

Set response body with bytes.

```java
void body(String body);
```

Set response body (will be encoded with UTF-8).

```java
String protocol();
```

Return the using protocol version.

```java
Status status();
```

Return the status of response.

```java
String statusDescription();
```

Return the status description of current status.

```java
byte[] bodyRaw();
```

Return the current response body in bytes.

```java
String body();
```

Return the current response body.

```java
String header(String header);
```

Return the value of specified header.

```java
Set<String> headers();
```

Return names of all headers.

```java
cookie(String name, String value);
cookie(String name, String value, int maxAge);
cookie(String name, String value, int maxAge, boolean httpOnly);
cookie(String path, String name, String value);
cookie(String path, String name, String value, int maxAge);
cookie(String path, String name, String value, int maxAge, boolean httpOnly);
```

Set cookie with optional attributes.

```java
removeCookie(String name);
removeCookie(String path, String name);
```

Remove cookie used by `Request`.

```java
redirect(String location);
redirect(String location, Status status);
```

Redirect the request to specified location.

## Reference

- [MDN Web Docs - HTTP response status codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
- [MDN Web Docs - HTTP cookies](https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies)
- [MDN Web Docs - Set-Cookie](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie)
- [MDN Web Docs - Redirections](https://developer.mozilla.org/en-US/docs/Web/HTTP/Redirections)
- [RFC 7231 - Request Methods](https://greenbytes.de/tech/webdav/rfc7231.html#methods)
- [RFC 2616](https://datatracker.ietf.org/doc/html/rfc2616)
- [HTTP Made Really Easy](https://jmarshall.com/easy/http/)
- [Documentation - Spark Framework](https://sparkjava.com/documentation)
- [Spark](http://sparkjava.com/)
- [cis 455 hw1](https://www.cis.upenn.edu/~cis455/assignments.html)