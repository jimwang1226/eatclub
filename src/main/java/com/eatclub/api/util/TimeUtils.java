package com.eatclub.api.util;

import io.micrometer.common.util.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.regex.Pattern;

public class TimeUtils {

    /**
     * Regular expression for 24-HOUR FORMAT
     * hour: from 0 to 23, allow 1 digit or 2 digits
     * minute: from 00 to 59, only allow 2 digits
     */
    private static final Pattern REG_FORMAT_24H = Pattern.compile("^([01]?\\d|2[0-3]):[0-5]\\d$");
    /**
     * DateTimeFormat for 24-hour
     */
    private static final DateTimeFormatter DT_FORMAT_24H = DateTimeFormatter.ofPattern("H:mm");

    /**
     * Regular expression for 12-HOUR FORMAT
     * hour: from 0 to 12, allow 1 digit or 2 digits
     * minute: from 00 to 59, only allow 2 digits
     * must have 'am' or 'pm' at the end of the string
     */
    private static final Pattern REG_FORMAT_12H = Pattern.compile("^(0?[1-9]|1[0-2]):[0-5]\\d(?i)(am|pm)$");

    /**
     * DateTimeFormat for 12-hour
     */
//    private static final DateTimeFormatter DT_FORMAT_12H = DateTimeFormatter.ofPattern("h:mma");
    private static final DateTimeFormatter DT_FORMAT_12H =
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("h:mma")
                    .toFormatter(Locale.ENGLISH);

    /**
     * Check the input string is a valid 24-HOUR format or a valid 12-HOUR format
     * @param time time in String
     * @return return a valid time string if pass the validation.
     */
    public static String isValidTime(String time) {
        if (StringUtils.isBlank(time)) {
            throw new IllegalArgumentException("Time is null or empty.");
        }

        //trim; if string has AM PM, change to lowercase; replace space.
        time = time.trim().toLowerCase().replace(" ", "");

        boolean is24H = REG_FORMAT_24H.matcher(time).matches();
        boolean is12H = REG_FORMAT_12H.matcher(time).matches();

        if (!is24H && !is12H) {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }
        return time;
    }

    /**
     * Check the time validation, and parse time to LocalTime
     * @param time in String
     * @return LocalTime object
     */
    public static LocalTime parseTime(String time) {
        //Check the time format.
        String validTime = isValidTime(time);

        if (validTime.endsWith("am") || validTime.endsWith("pm")) {
            return LocalTime.parse(validTime, DT_FORMAT_12H);
        } else {
            return LocalTime.parse(validTime, DT_FORMAT_24H);
        }
    }

    /**
     * Parse an integer to LocalTime
     * @param time in integer, should between 0~1439
     * @return String, like 12:30pm
     */
    public static String parseTime(int time) {
        if (time < 0 || time > 1439) {
            throw new IllegalArgumentException("Invalid integer, must be between 0 and 1439");
        }
        int hour = time / 60;
        int minute = time % 60;
        return LocalTime.of(hour, minute).format(DT_FORMAT_12H).toLowerCase();
    }
}
