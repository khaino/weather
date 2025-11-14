package com.weather.formatter;

/**
 * Utility class for formatting time strings
 */
public class TimeFormatter {
    
    /**
     * Converts 24-hour time format (HH:MM) to 12-hour format with AM/PM
     */
    public static String format24To12Hour(String time24) {
        String[] parts = time24.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        String period = hour >= 12 ? "PM" : "AM";
        int hour12 = hour % 12;
        if (hour12 == 0) hour12 = 12;
        return String.format("%d:%02d %s", hour12, minute, period);
    }
}

