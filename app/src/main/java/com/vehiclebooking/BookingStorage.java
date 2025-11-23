package com.vehiclebooking;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.threeten.bp.LocalDate;

public class BookingStorage {
    private static final String PREFERENCES_NAME = "vehicle_bookings";
    private static final String BOOKINGS_KEY = "bookings_list";
    
    private Context context;
    
    // Instance constructor for analytics activity
    public BookingStorage(Context context) {
        this.context = context;
    }
    
    // Instance method to get bookings
    public List<BookingRequest> getBookings() {
        return getAllBookings(context);
    }

    public static void saveBooking(@NonNull Context context, @NonNull BookingRequest booking) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = createGson();
        
        // Get existing bookings
        List<BookingRequest> bookings = getAllBookings(context);
        
        // Check for duplicate booking ID to prevent duplicates
        if (booking.getBookingId() != null) {
            for (BookingRequest existingBooking : bookings) {
                if (booking.getBookingId().equals(existingBooking.getBookingId())) {
                    // Duplicate booking ID found, don't save
                    return;
                }
            }
        }
        
        // Add new booking
        bookings.add(booking);
        
        // Save back to preferences
        String bookingsJson = gson.toJson(bookings);
        prefs.edit().putString(BOOKINGS_KEY, bookingsJson).apply();
    }

    @NonNull
    public static List<BookingRequest> getAllBookings(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String bookingsJson = prefs.getString(BOOKINGS_KEY, "[]");
        
        Gson gson = createGson();
        Type listType = new TypeToken<List<BookingRequest>>(){}.getType();
        
        List<BookingRequest> bookings = gson.fromJson(bookingsJson, listType);
        return bookings != null ? bookings : new ArrayList<>();
    }

    public static void clearAllBookings(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(BOOKINGS_KEY).apply();
    }

    /**
     * Update an existing booking (useful for status changes and modifications)
     */
    public static void updateBooking(@NonNull Context context, @NonNull BookingRequest updatedBooking) {
        List<BookingRequest> bookings = getAllBookings(context);
        
        // Find and update the booking by booking ID (preferred) or timestamp
        boolean found = false;
        for (int i = 0; i < bookings.size(); i++) {
            BookingRequest existing = bookings.get(i);
            // Try booking ID first (more reliable)
            if (updatedBooking.getBookingId() != null && existing.getBookingId() != null &&
                updatedBooking.getBookingId().equals(existing.getBookingId())) {
                bookings.set(i, updatedBooking);
                found = true;
                break;
            }
            // Fallback to timestamp if booking ID not available
            if (!found && existing.getTimestamp() == updatedBooking.getTimestamp()) {
                bookings.set(i, updatedBooking);
                found = true;
                break;
            }
        }
        
        // If not found, add as new booking
        if (!found) {
            bookings.add(updatedBooking);
        }
        
        // Save updated list
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = createGson();
        String bookingsJson = gson.toJson(bookings);
        prefs.edit().putString(BOOKINGS_KEY, bookingsJson).apply();
    }
    
    /**
     * Create Gson instance with LocalDate adapter for efficient serialization
     */
    @NonNull
    private static Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    }

    /**
     * Get bookings filtered by status
     */
    @NonNull
    public static List<BookingRequest> getBookingsByStatus(@NonNull Context context, @NonNull BookingStatus status) {
        List<BookingRequest> allBookings = getAllBookings(context);
        List<BookingRequest> filteredBookings = new ArrayList<>();
        
        for (BookingRequest booking : allBookings) {
            BookingStatus bookingStatus = booking.getStatus();
            if (bookingStatus == null) {
                bookingStatus = BookingStatus.PENDING; // Default fallback
            }
            if (bookingStatus == status) {
                filteredBookings.add(booking);
            }
        }
        
        return filteredBookings;
    }

    /**
     * Get booking statistics by status
     */
    @NonNull
    public static BookingStats getBookingStats(@NonNull Context context) {
        List<BookingRequest> allBookings = getAllBookings(context);
        BookingStats stats = new BookingStats();
        
        for (BookingRequest booking : allBookings) {
            BookingStatus status = booking.getStatus();
            if (status == null) {
                status = BookingStatus.PENDING;
            }
            stats.incrementCount(status);
        }
        
        return stats;
    }

    /**
     * Generate a unique booking ID
     */
    @NonNull
    public static String generateUniqueBookingId(@NonNull Context context) {
        long timestamp = System.currentTimeMillis();
        int randomComponent = (int)(Math.random() * 1000);
        String bookingId = "BK" + timestamp + randomComponent;
        
        // Ensure this ID doesn't already exist
        List<BookingRequest> existingBookings = getAllBookings(context);
        for (BookingRequest existing : existingBookings) {
            if (bookingId.equals(existing.getBookingId())) {
                // Very rare collision, generate new ID
                bookingId = "BK" + System.currentTimeMillis() + (int)(Math.random() * 10000);
                break;
            }
        }
        
        return bookingId;
    }
    
    /**
     * Validate phone number format
     */
    public static boolean isValidPhoneNumber(@NonNull String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Basic phone number validation - accepts numbers with optional formatting
        return phone.trim().matches("^[+]?[0-9\\s\\-\\(\\)]{10,}$");
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(@NonNull String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.trim().matches(emailPattern);
    }
    
    /**
     * Format date for display (using LocalDate)
     */
    @NonNull
    public static String formatDate(@NonNull LocalDate date) {
        return DateUtils.formatDate(date);
    }
    
    /**
     * Format date for display with custom format (using LocalDate)
     */
    @NonNull
    public static String formatDate(@NonNull LocalDate date, @NonNull String pattern) {
        return DateUtils.formatDate(date, pattern);
    }
    
    /**
     * Format Calendar to LocalDate and format (for compatibility during migration)
     */
    @NonNull
    public static String formatDate(@NonNull java.util.Calendar calendar) {
        LocalDate date = DateUtils.calendarToLocalDate(calendar);
        return DateUtils.formatDate(date);
    }
    
    /**
     * Validate that date is not in the past (using LocalDate)
     */
    public static boolean isDateValid(@NonNull LocalDate date) {
        return DateUtils.isDateValid(date);
    }
    
    /**
     * Validate Calendar date (for compatibility during migration)
     */
    public static boolean isDateValid(@NonNull java.util.Calendar calendar) {
        LocalDate date = DateUtils.calendarToLocalDate(calendar);
        return DateUtils.isDateValid(date);
    }
    
    /**
     * Trim and validate non-empty string
     */
    @Nullable
    public static String trimAndValidate(@Nullable String input, @NonNull String fieldName) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
    
    /**
     * Validate required field
     */
    public static boolean isFieldValid(@Nullable String field, @NonNull String fieldName) {
        return field != null && !field.trim().isEmpty();
    }

    /**
     * Helper class for booking statistics
     */
    public static class BookingStats {
        private int pendingCount = 0;
        private int confirmedCount = 0;
        private int inProgressCount = 0;
        private int completedCount = 0;
        private int cancelledCount = 0;
        
        public void incrementCount(BookingStatus status) {
            switch (status) {
                case PENDING: pendingCount++; break;
                case CONFIRMED: confirmedCount++; break;
                case IN_PROGRESS: inProgressCount++; break;
                case COMPLETED: completedCount++; break;
                case CANCELLED: cancelledCount++; break;
            }
        }
        
        public int getTotalCount() {
            return pendingCount + confirmedCount + inProgressCount + completedCount + cancelledCount;
        }
        
        public int getActiveCount() {
            return pendingCount + confirmedCount + inProgressCount;
        }
        
        // Getters for each status count
        public int getPendingCount() { return pendingCount; }
        public int getConfirmedCount() { return confirmedCount; }
        public int getInProgressCount() { return inProgressCount; }
        public int getCompletedCount() { return completedCount; }
        public int getCancelledCount() { return cancelledCount; }
    }
}
