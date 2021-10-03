package org.subspark;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SessionImpl extends Session {
    private static final int DEFAULT_INACTIVATE_INTERVAL = 30 * 60 * 1000;

    private final String id;
    private final long creationTime;
    private volatile long lastAccessTime;
    private volatile int maxInactiveInterval;
    private volatile boolean isValid;
    private final Map<String, Object> attrs;

    SessionImpl(String id) {
        this.id = id;
        this.isValid = true;
        this.attrs = new ConcurrentHashMap<>();
        this.creationTime = System.currentTimeMillis();
        this.lastAccessTime = creationTime;
        this.maxInactiveInterval = DEFAULT_INACTIVATE_INTERVAL;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public long creationTime() {
        return this.creationTime;
    }

    @Override
    public long lastAccessedTime() {
        return this.lastAccessTime;
    }

    @Override
    public boolean isValid() {
        if (!isValid) {
            return false;
        }
        boolean isExpired = System.currentTimeMillis() > creationTime + maxInactiveInterval * 1000;
        return !isExpired;
    }

    @Override
    public void invalidate() {
        if (!isValid()) {
            throw new IllegalStateException("Session: {" + id + "} is invalid");
        }
        isValid = false;
    }

    @Override
    public int maxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    @Override
    public void maxInactiveInterval(int interval) {
        if (!isValid()) {
            throw new IllegalStateException("Session: {" + id + "} is invalid");
        }
        this.maxInactiveInterval = interval;
    }

    @Override
    public void access() {
        this.lastAccessTime = System.currentTimeMillis();
    }

    @Override
    public void attribute(String name, Object value) {
        if (!isValid()) {
            throw new IllegalStateException("Session: {" + id + "} is invalid");
        }
        if (name == null || value == null) {
            throw new NullPointerException("null key or null value is not supported in Session!");
        }
        access();
        attrs.put(name, value);
    }

    @Override
    public Object attribute(String name) {
        if (!isValid()) {
            throw new IllegalStateException("Session: {" + id + "} is invalid");
        }
        if (name == null) {
            throw new NullPointerException("null key or null value is not supported in Session!");
        }
        access();
        return attrs.get(name);
    }

    @Override
    public Set<String> attributes() {
        if (!isValid()) {
            throw new IllegalStateException("Session: {" + id + "} is invalid");
        }
        access();
        return attrs.keySet();
    }

    @Override
    public void removeAttribute(String name) {
        if (!isValid()) {
            throw new IllegalStateException("Session: {" + id + "} is invalid");
        }
        if (name == null) {
            return;
        }
        access();
        attrs.remove(name);
    }
}
