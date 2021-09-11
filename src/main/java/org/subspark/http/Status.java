package org.subspark.http;

public enum Status {
    CONTINUE(100, "Continue"),

    OK(200, "OK"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),

    MOVED_PERMANENTLY(302, "Moved Permanently"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),

    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    GONE(410, "Gone"),
    PRECONDITION_FAILED(412, "Precondition Failed"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented");

    private final int statusCode;
    private final String description;

    Status(int statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public static Status fromStatusCode(int statusCode) {
        for (Status status : values())
            if (status.code() == statusCode)
                return status;
        return null;
    }

    public int code() {
        return statusCode;
    }

    public String description() {
        return description;
    }

    public String fullDescription() {
        return statusCode + " " + description;
    }

}
