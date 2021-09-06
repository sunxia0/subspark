package org.subspark.server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.server.http.Method;
import org.subspark.server.http.Status;
import org.subspark.server.utils.DateUtils;
import org.subspark.server.utils.FileUtils;

public class StaticFilesHandler {
    private final static Logger logger = LogManager.getLogger(StaticFilesHandler.class);

    private String staticFileLocation;

    public StaticFilesHandler() {}

    public void staticFileLocation(String staticFileLocation) {
        this.staticFileLocation = staticFileLocation;
    }

    private void consumeFileRequest(HttpRequest request, HttpResponse response) throws HaltException {
        String fullPath = FileUtils.getFullPath(staticFileLocation, request.path());
        if (!FileUtils.fileExists(fullPath)) {
            throw new HaltException(Status.NOT_FOUND);
        }

        Method method = request.method();
        if (method != Method.GET && method != Method.HEAD) {
            throw new HaltException(request.protocol().equals(Constant.HTTP_1_1) ?
                    Status.METHOD_NOT_ALLOWED : Status.BAD_REQUEST, "Invalid HTTP method for the resource");
        }

        long lastModified = FileUtils.getLastModified(fullPath);

        // Check If-Unmodified-Since
        long ifUnmodifiedSince = DateUtils.fromDateString(request.header("if-unmodified-since"));
        if (ifUnmodifiedSince >= 0 && lastModified > ifUnmodifiedSince) {
            response.status(Status.PRECONDITION_FAILED);
        }

        // Check If-Modified-Since
        long ifModifiedSince = DateUtils.fromDateString(request.header("if-modified-since"));
        if (lastModified >= ifModifiedSince) {
            response.status(Status.OK);
            response.header("last-modified", DateUtils.fromTimestamp(lastModified));
            response.header("content-type", MimeType.getMimeType(fullPath));
            response.bodyRaw(FileUtils.getFileBytes(fullPath));
        } else {
            response.status(Status.NOT_MODIFIED);
        }
    }

    public boolean consume(HttpRequest request, HttpResponse response) {
        try {
            consumeFileRequest(request, response);
            return true;
        } catch (HaltException e) {
            boolean isConsumed = e.getStatus() != Status.NOT_FOUND;
            if (isConsumed) {
                response.status(e.getStatus());
                response.header("content-type", MimeType.TXT);
                response.body(e.getMessage());
            }
            return isConsumed;
        }
    }
}
