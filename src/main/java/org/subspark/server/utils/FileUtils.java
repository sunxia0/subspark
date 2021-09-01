package org.subspark.server.utils;


import org.subspark.server.exceptions.HaltException;
import org.subspark.server.http.Status;

import java.io.*;
import java.util.StringTokenizer;


public class FileUtils {
    /**
     * Check whether filePath is secure (it can't access the file outside basePath)
     */
    private static boolean isFilePathValid(String filePath) {
        StringTokenizer tokenizer = new StringTokenizer(filePath, "/");
        String current;
        int level = 0;

        // Check "-"
        if (tokenizer.hasMoreTokens()) {
            current = tokenizer.nextToken();
            if ("-".equals(current))
                return false;
        }

        while (tokenizer.hasMoreTokens()) {
            current = tokenizer.nextToken();

            // Check ".."
            if ("..".equals(current)) {
                level--;
                if (level < 0)
                    return false;
            } else if (!".".equals(current)) {
                level++;
            }
        }

        return true;
    }

    /**
     * Return full file path based on basePath and filePath to read.
     * If full path is a directory, the full path of "index.html" in
     * the directory will be returned.
     */
    public static String getFullPath(String basePath, String filePath) throws HaltException {
        if (!isFilePathValid(filePath))
            throw new HaltException(Status.FORBIDDEN, "Invalid path.");

        String fullPath = basePath + filePath;
        File file = new File(fullPath);
        if (file.isDirectory()) {
            return fullPath + (filePath.endsWith("/") ? "" : "/") + "index.html";
        } else {
            return fullPath;
        }
    }

    /**
     * Read bytes from given file
     */
    public static byte[] getFileBytes(String filePath) throws HaltException {
        File file = new File(filePath);

        try {
            InputStream in = new FileInputStream(file);
            int available = in.available();
            byte[] bytes = new byte[available];
            in.read(bytes);
            in.close();
            return bytes;
        } catch (FileNotFoundException e) {
            throw new HaltException(Status.NOT_FOUND,
                    "NOT FOUND: File not found!");
        } catch (IOException e) {
            e.printStackTrace();
            throw new HaltException(Status.INTERNAL_SERVER_ERROR,
                    "INTERNAL SERVER ERROR: IOException: " + e.getMessage());
        }
    }

    /**
     * Return length of file (count in byte)
     */
    public static long getFileLength(String filePath) throws HaltException {
        File file = new File(filePath);

        if (!file.exists())
            throw new HaltException(Status.NOT_FOUND,
                    "NOT FOUND: File not found!");
        if (file.isDirectory())
            throw new HaltException(Status.INTERNAL_SERVER_ERROR,
                    "Directory has no length!");
        return file.length();
    }

    /**
     * Return last modified timestamp of given file
     */
    public static long getLastModified(String filePath) throws HaltException {
        File file = new File(filePath);

        if (!file.exists())
            throw new HaltException(Status.NOT_FOUND,
                    "NOT FOUND: File not found!");

        return file.lastModified();
    }

    /**
     * Return whether the given file exists and is readable (not a dir)
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && !file.isDirectory();
    }
}
