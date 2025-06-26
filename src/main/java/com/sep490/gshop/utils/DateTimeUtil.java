package com.sep490.gshop.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private DateTimeUtil() {}
    public static String getTimeStringInTimeZone(String timeZone, long time) {
        ZonedDateTime dateTime = Instant.ofEpochSecond(time)
                .atZone(ZoneId.of(timeZone));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return dateTime.format(formatter);
    }

    public static long getCurrentEpochMilli() {
        return Instant.now().toEpochMilli();
    }
    public static long getCurrentEpochSecond() {
        return Instant.now().getEpochSecond();
    }

    public static String secondToTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        if (hours > 0) {
            return String.format("%02d giờ %02d phút %02d giây", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%02d phút %02d giây", minutes, secs);
        } else {
            return String.format("%02d giây", secs);
        }
    }

}
