package org.subspark.server.exceptions;

import org.subspark.server.response.Status;

public class HaltException extends RuntimeException {
    private static final long serialVersionUID = -1781180700340240978L;

    private final Status status;
    private final String message;

    public HaltException(Status status) {
        this.status = status;
        this.message = status.description();
    }

    public HaltException(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
