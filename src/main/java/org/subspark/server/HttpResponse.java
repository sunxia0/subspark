package org.subspark.server;


import org.subspark.server.http.Status;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private String protocolVersion;
    private Status status;
    private final Map<String, String> headers;
    private byte[] body;

    protected HttpResponse() {
        this.headers = new HashMap<>();
    }

    // Can't be set by user
    protected void protocol(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void header(String header, String value) {
        this.headers.put(header, value);
    }

    public void status(Status status) {
        this.status = status;
    }

    public void bodyRaw(byte[] b) {
        this.body = b;
        if (b != null) {
            header("content-length", String.valueOf(b.length));
        }
    }

    public void body(String body) {
        this.body = body == null ? null : body.getBytes();
    }

    public String protocol() {
        return protocolVersion;
    }

    public Status status() {
        return status;
    }

    public String statusDescription() {
        return status.fullDescription();
    }

    public byte[] bodyRaw() {
        return body;
    }

    public String body() {
        return body == null ? "" : new String(body, StandardCharsets.UTF_8);
    }

    public String header(String header) {
        return headers.get(header);
    }

    public Set<String> headers() {
        return headers.keySet();
    }

    public String headerString() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocolVersion).append(' ').append(statusDescription()).append("\r\n");
        for (Map.Entry<String, String> e : headers.entrySet())
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("\r\n");
        return sb.toString();
    }
}
