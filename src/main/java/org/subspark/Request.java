package org.subspark;

import java.util.Map;
import java.util.Set;

public class Request extends org.subspark.server.request.Request {
    protected Request () {
        super();
    }

    // ============ for stage 2 ============

    /**
     * @return Gets the session associated with this request
     */
    public Session session() {
        return null;
    }

    /**
     * @return a map containing the route parameters
     */
    public Map<String, String> params() {
        return null;
    }

    /**
     * @return the named parameter Example: parameter 'name' from the following
     *         pattern: (get '/hello/:name')
     */
    public String params(String param) {
        if (param == null)
            return null;

        if (param.startsWith(":"))
            return params().get(param.toLowerCase());
        else
            return params().get(':' + param.toLowerCase());
    }

    /**
     * Add an attribute to the request (eg in a filter)
     */
    public void attribute(String attrib, Object val) {
    }

    /**
     * @return Gets an attribute attached to the request
     */
    public Object attribute(String attrib) {
        return null;
    }

    /**
     * @return All attributes attached to the request
     */
    public Set<String> attributes() {
        return null;
    }

    public Map<String, String> cookies() {
        return null;
    }

    public String cookie(String name) {
        if (name == null || cookies() == null)
            return null;
        else
            return cookies().get(name);
    }
}
