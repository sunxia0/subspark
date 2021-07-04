package org.subspark.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
    private static final SimpleDateFormat gmtFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'");

    static {
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String now() {
        Date date = new Date();
        return gmtFormat.format(date);
    }

    public static String fromTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        return gmtFormat.format(date);
    }
}
