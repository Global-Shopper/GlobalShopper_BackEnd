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
}
