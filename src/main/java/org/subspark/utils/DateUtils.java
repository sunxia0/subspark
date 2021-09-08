package org.subspark.utils;


import java.text.ParseException;
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

    public static long fromDateString(String dateString) {
        if (dateString == null) {
            return -1L;
        }

        try {
            Date date = gmtFormat.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            return -1L;
        }
    }
}
