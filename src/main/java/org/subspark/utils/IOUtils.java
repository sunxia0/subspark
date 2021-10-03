package org.subspark.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class IOUtils {
    public static String getStackTraceString(Exception e) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(buf));
        return buf.toString();
    }
}
