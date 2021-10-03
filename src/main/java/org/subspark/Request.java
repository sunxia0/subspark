package org.subspark;


import org.subspark.http.Method;

import java.util.*;

public class Request {
    private Method method;
    private String path;
    private String queryString;
    private String uri;
    private String protocolVersion;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private final Map<String, Object> attributes;
    private Map<String, String> cookiesHolder;
    private Map<String, String> paramsHolder;
    private List<String> wildcardsHolder;
    private String body;
    private byte[] bodyRaw;
    private Session session;
    private SessionManager sessionManager;

    protected Request() {
        this.queryParams = new HashMap<>();
        this.headers = new HashMap<>();
        this.attributes = new HashMap<>();
    }

    /**
     * ======= Setters only for the class of the same package ======
     */
    protected void method(String method) {
        this.method = Method.fromString(method);
    }

    protected void path(String path) {
        this.path = path;
    }

    protected void queryString(String queryString) {
        this.queryString = queryString;
    }

    protected void uri(String uri) {
        this.uri = uri;
    }

    protected void protocol(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    protected void queryParam(String key, String value) {
        this.queryParams.put(key, value);
    }

    protected void cookiesHolder(Map<String, String> cookiesHolder) {
        this.cookiesHolder = cookiesHolder;
    }

    protected void paramsHolder(Map<String, String> paramsHolder) {
        this.paramsHolder = paramsHolder;
    }

    protected void wildcardsHolder(List<String> wildcardsHolder) {
        this.wildcardsHolder = wildcardsHolder;
    }

    protected void header(String key, String value) {
        this.headers.put(key, value);
    }

    protected void body(byte[] bodyRaw) {
        this.bodyRaw = bodyRaw;
        this.body = new String(this.bodyRaw);
    }

    protected void setSessionManager(SessionManager manager) {
        this.sessionManager = manager;
    }

    /**
     * The request method (GET, POST, ...)
     */
    public Method method() {
        return method;
    }

    /**
     * @return The path (without query params)
     */
    public String path() {
        return path;
    }

    /**
     * @return The URI up to the query string
     */
    public String uri() {
        return uri;
    }

    /**
     * @return Query parameter from the URL
     */
    public String queryParam(String param) {
        return queryParams.get(param);
    }

    public String queryParamOrDefault(String param, String def) {
        String ret = queryParam(param);
        return (ret == null) ? def : ret;
    }

    public Set<String> queryParams() {
        return queryParams.keySet();
    }

    /**
     * @return The raw query string
     */
    public String queryString() {
        return this.queryString;
    }

    /**
     * @return The protocol name and version from the request
     */
    public String protocol() {
        return this.protocolVersion;
    }

    /**
     * @return Get the item from the header
     */
    public String header(String name) {
        return headers.get(name);
    }

    public Set<String> headers() {
        return headers.keySet();
    }

    /**
     * @return The request body sent by the client (encoded with UTF-8)
     */
    public String body() {
        return body;
    }

    /**
     * @return The request body bytes
     */
    public byte[] bodyRaw() {
        return bodyRaw;
    }

    public Map<String, String> cookies() {
        return cookiesHolder;
    }

    public String cookie(String name) {
        if (name == null || cookies() == null) {
            return null;
        } else {
            return cookies().get(name);
        }
    }

    /**
     * @return Gets the session associated with this request
     */
    public Session session() {
        return session(true);
    }

    public Session session(boolean createIfNone) {
        if (this.session == null || !this.session.isValid()) {
            String id = cookie(SessionManager.SESSION_ID_COOKIE_NAME);
            Session session = sessionManager.fromSessionId(id);
            if (session == null && createIfNone) {
                this.session = sessionManager.newSession();
            } else {
                this.session = session;
            }
        }
        return this.session;
    }

    /**
     * Add an attribute to the request (eg in a filter)
     */
    public void attribute(String attrib, Object val) {
        attributes.put(attrib, val);
    }

    /**
     * @return Gets an attribute attached to the request
     */
    public Object attribute(String attrib) {
        return attributes.get(attrib);
    }

    /**
     * @return All attributes attached to the request
     */
    public Set<String> attributes() {
        return attributes.keySet();
    }

    /**
     * @return a map containing the route parameters
     */
    public Map<String, String> namedParams() {
        return paramsHolder;
    }

    /**
     * @return the named parameter Example: parameter 'name' from the following
     *         pattern: (get '/hello/:name')
     */
    public String namedParam(String param) {
        if (param == null)
            return null;

        if (param.startsWith(":")) {
            return namedParams().get(param.toLowerCase());
        } else {
            return namedParams().get(':' + param.toLowerCase());
        }
    }

    /**
     * @return a list containing all wildcards
     */
    public List<String> wildcards() {
        return wildcardsHolder;
    }
}
