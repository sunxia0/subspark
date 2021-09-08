package org.subspark;


import java.util.HashMap;
import java.util.Map;


public class MimeType {
    public static final String BIN = "application/octet-stream";
    public static final String EXE = BIN;
    public static final String DOC = "application/msword";
    public static final String XLS = "application/vnd.ms-excel";
    public static final String PPT = "application/vnd.ms-powerpoint";
    public static final String PDF = "application/pdf";
    public static final String TEX = "application/x-tex";
    public static final String TAR = "application/x-tar";
    public static final String ZIP = "application/zip";
    public static final String JSON = "application/json";
    public static final String JAR = "application/java-archive";

    public static final String ICO = "image/x-icon";
    public static final String JPG = "image/jpeg";
    public static final String GIF = "image/gif";
    public static final String PNG = "image/png";

    public static final String TXT = "text/plain";
    public static final String HTML = "text/html";
    public static final String JS = "text/javascript";
    public static final String CSS = "text/css";
    public static final String XML = "text/xml";

    private static final Map<String, String> ext2Mime = new HashMap<>();

    static {
        ext2Mime.put("bin", BIN);
        ext2Mime.put("exe", EXE);
        ext2Mime.put("doc", DOC);
        ext2Mime.put("xls", XLS);
        ext2Mime.put("ppt", PPT);
        ext2Mime.put("pdf", PDF);
        ext2Mime.put("tex", TEX);
        ext2Mime.put("tar", TAR);
        ext2Mime.put("zip", ZIP);
        ext2Mime.put("json", JSON);
        ext2Mime.put("jar", JAR);

        ext2Mime.put("ico", ICO);
        ext2Mime.put("jpg", JPG);
        ext2Mime.put("gif", GIF);
        ext2Mime.put("png", PNG);

        ext2Mime.put("txt", TXT);
        ext2Mime.put("html", HTML);
        ext2Mime.put("js", JS);
        ext2Mime.put("css", CSS);
        ext2Mime.put("xml", XML);
    }

    /**
     * Return MIME type of given extension
     */
    public static String extMimeType(String ext) {
        return ext2Mime.getOrDefault(ext.toLowerCase(), TXT);
    }

    /**
     * Return MIME type of given file
     */
    public static String getMimeType(String filePath) {
        int position = filePath.lastIndexOf(".");
        if (position == -1 || position + 1 == filePath.length())
            return BIN;
        String extension = filePath.substring(position + 1);
        return extMimeType(extension);
    }
}
