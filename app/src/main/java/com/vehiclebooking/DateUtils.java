package com.vehiclebooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

/**
 * Utility class for date operations using java.time API via ThreeTenABP.
 * ThreeTenABP automatically uses java.time on API 26+ and backport for lower APIs.
 * Efficient date parsing/formatting without string operations.
 * 
 * Note: Call AndroidThreeTen.init(context) in Application.onCreate() or MainActivity.onCreate()
 */
public class DateUtils {
    
    // Common date formatters (thread-safe, can be reused)
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_TIME_12H_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter LONG_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
    private static final DateTimeFormatter LONG_DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm");
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = 
        DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter DAY_NAME_FORMATTER = 
        DateTimeFormatter.ofPattern("EEEE");
    private static final DateTimeFormatter HOUR_FORMATTER = 
        DateTimeFormatter.ofPattern("HH");
    
    /**
     * Get current date
     */
    @NonNull
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * Get current date and time
     */
    @NonNull
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    /**
     * Format LocalDate to string (dd/MM/yyyy)
     */
    @NonNull
    public static String formatDate(@NonNull LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Format LocalDate with custom pattern
     */
    @NonNull
    public static String formatDate(@NonNull LocalDate date, @NonNull String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }
    
    /**
     * Format LocalDateTime to string
     */
    @NonNull
    public static String formatDateTime(@NonNull LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    /**
     * Format LocalDateTime with 12-hour format
     */
    @NonNull
    public static String formatDateTime12Hour(@NonNull LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_12H_FORMATTER);
    }
    
    /**
     * Format timestamp (long) to date string
     */
    @NonNull
    public static String formatTimestamp(long timestamp) {
        LocalDateTime dateTime = timestampToLocalDateTime(timestamp);
        return dateTime.format(TIMESTAMP_FORMATTER);
    }
    
    /**
     * Format timestamp to long date string
     */
    @NonNull
    public static String formatTimestampLong(long timestamp) {
        LocalDateTime dateTime = timestampToLocalDateTime(timestamp);
        return dateTime.format(LONG_DATE_TIME_FORMATTER);
    }
    
    /**
     * Format timestamp to date string (MMM dd, yyyy HH:mm)
     */
    @NonNull
    public static String formatTimestampMedium(long timestamp) {
        LocalDateTime dateTime = timestampToLocalDateTime(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return dateTime.format(formatter);
    }
    
    /**
     * Format timestamp to date string (yyyy-MM-dd HH:mm)
     */
    @NonNull
    public static String formatTimestampShort(long timestamp) {
        LocalDateTime dateTime = timestampToLocalDateTime(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }
    
    /**
     * Parse date string to LocalDate
     */
    @Nullable
    public static LocalDate parseDate(@NonNull String dateString) {
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Parse date string with custom pattern
     */
    @Nullable
    public static LocalDate parseDate(@NonNull String dateString, @NonNull String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Convert timestamp (milliseconds) to LocalDateTime
     */
    @NonNull
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(
            org.threeten.bp.Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        );
    }
    
    /**
     * Convert LocalDate to timestamp (milliseconds)
     */
    public static long localDateToTimestamp(@NonNull LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();
    }
    
    /**
     * Convert LocalDateTime to timestamp (milliseconds)
     */
    public static long localDateTimeToTimestamp(@NonNull LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();
    }
    
    /**
     * Check if date is not in the past
     */
    public static boolean isDateValid(@NonNull LocalDate date) {
        LocalDate today = today();
        return !date.isBefore(today);
    }
    
    /**
     * Get day name from timestamp
     */
    @NonNull
    public static String getDayName(long timestamp) {
        LocalDateTime dateTime = timestampToLocalDateTime(timestamp);
        return dateTime.format(DAY_NAME_FORMATTER);
    }
    
    /**
     * Get month and year from timestamp
     */
    @NonNull
    public static String getMonthYear(long timestamp) {
        LocalDateTime dateTime = timestampToLocalDateTime(timestamp);
        return dateTime.format(MONTH_YEAR_FORMATTER);
    }
    
    /**
     * Get hour from timestamp
     */
    public static int getHour(long timestamp) {
        LocalDateTime dateTime = timestampToLocalDateTime(timestamp);
        return dateTime.getHour();
    }
    
    /**
     * Convert Calendar to LocalDate (for compatibility during migration)
     */
    @NonNull
    public static LocalDate calendarToLocalDate(@NonNull java.util.Calendar calendar) {
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1; // Calendar months are 0-based
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return LocalDate.of(year, month, day);
    }
    
    /**
     * Convert LocalDate to Calendar (for compatibility during migration)
     */
    @NonNull
    public static java.util.Calendar localDateToCalendar(@NonNull LocalDate date) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        return calendar;
    }
    
    /**
     * Convert Date to LocalDate (for compatibility during migration)
     */
    @NonNull
    public static LocalDate dateToLocalDate(@NonNull java.util.Date date) {
        return timestampToLocalDateTime(date.getTime()).toLocalDate();
    }
    
    /**
     * Convert LocalDate to Date (for compatibility during migration)
     */
    @NonNull
    public static java.util.Date localDateToDate(@NonNull LocalDate date) {
        long timestamp = localDateToTimestamp(date);
        return new java.util.Date(timestamp);
    }
}

