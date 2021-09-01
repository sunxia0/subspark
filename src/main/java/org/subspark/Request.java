package org.subspark;

import org.subspark.server.HttpRequest;

import java.util.Map;
import java.util.Set;

public class Request extends HttpRequest {
    protected Request () {
        super();
    }

    // ============ for stage 2 ============

    /**
     * @return The header "host"
     */
    public String host() {
        return header("host");
    }

    /**
     * @return The header "user-agent"
     */
    public String userAgent() {
        return header("user-agent");
    }

    /**
     * @return The header "content-type"
     */
    public String contentType() {
        return header("content-type");
    }

    /**
     * @return The header "content-length"
     */
    public int contentLength() {
        try {
            return Integer.parseInt(header("content-length"));
        } catch (NumberFormatException e) {
            return 0;
        }
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

        if (param.startsWith(":")) {
            return params().get(param.toLowerCase());
        } else {
            return params().get(':' + param.toLowerCase());
        }
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
}
