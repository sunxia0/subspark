package org.subspark.server.handling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.subspark.server.WebService;
import org.subspark.server.common.MimeType;
import org.subspark.server.exceptions.HaltException;
import org.subspark.server.request.Method;
import org.subspark.server.request.Request;
import org.subspark.server.response.ResponseBuilder;
import org.subspark.server.response.Status;
import org.subspark.util.DateUtils;
import org.subspark.util.FileUtils;

import java.text.ParseException;

public class StaticFilesHandler {
    private final static Logger logger = LogManager.getLogger(StaticFilesHandler.class);

    private final WebService service;

    private String staticFileLocation;

    public StaticFilesHandler(WebService service) {
        this.service = service;
    }

    public void staticFileLocation(String staticFileLocation) {
        this.staticFileLocation = staticFileLocation;
    }

    public ResponseBuilder consumeFileRequest(Request request) throws HaltException {
        Method method = request.method();
        if (method != Method.GET && method != Method.HEAD)
            throw new HaltException(Status.METHOD_NOT_ALLOWED);

        String fullPath = FileUtils.getFullPath(staticFileLocation, request.path());
        if (!FileUtils.fileExists(fullPath))
            throw new HaltException(Status.NOT_FOUND);

        ResponseBuilder builder = new ResponseBuilder();
        builder.protocol(request.protocol());

        long lastModified = FileUtils.getLastModified(fullPath);

        // Check If-Unmodified-Since
        long ifUnmodifiedSince = DateUtils.fromDateString(request.header("if-unmodified-since"));
        if (ifUnmodifiedSince >= 0 && lastModified > ifUnmodifiedSince) {
            builder.status(Status.PRECONDITION_FAILED);
            return builder;
        }

        if (method == Method.GET) {
            // Check If-Modified-Since
            long ifModifiedSince = DateUtils.fromDateString(request.header("if-modified-since"));
            if (lastModified >= ifModifiedSince) {
                builder.status(Status.OK)
                        .header("last-modified", DateUtils.fromTimestamp(lastModified))
                        .header("content-type", MimeType.getMimeType(fullPath))
                        .body(FileUtils.getFileBytes(fullPath));
            }
            else {
                builder.status(Status.NOT_MODIFIED);
            }
        }
        else { // Method.HEAD
            builder.status(Status.OK)
                    .header("last-modified", DateUtils.fromTimestamp(lastModified))
                    .header("content-length", String.valueOf(FileUtils.getFileLength(fullPath)));
        }
        return builder;
    }
}
