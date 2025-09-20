package com.vehiclebooking;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookingStorage {
    private static final String PREFERENCES_NAME = "vehicle_bookings";
    private static final String BOOKINGS_KEY = "bookings_list";

    public static void saveBooking(Context context, BookingRequest booking) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        
        // Get existing bookings
        List<BookingRequest> bookings = getAllBookings(context);
        
        // Add new booking
        bookings.add(booking);
        
        // Save back to preferences
        String bookingsJson = gson.toJson(bookings);
        prefs.edit().putString(BOOKINGS_KEY, bookingsJson).apply();
    }

    public static List<BookingRequest> getAllBookings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String bookingsJson = prefs.getString(BOOKINGS_KEY, "[]");
        
        Gson gson = new Gson();
        Type listType = new TypeToken<List<BookingRequest>>(){}.getType();
        
        List<BookingRequest> bookings = gson.fromJson(bookingsJson, listType);
        return bookings != null ? bookings : new ArrayList<>();
    }

    public static void clearAllBookings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(BOOKINGS_KEY).apply();
    }

    /**
     * Update an existing booking (useful for status changes)
     */
    public static void updateBooking(Context context, BookingRequest updatedBooking) {
        List<BookingRequest> bookings = getAllBookings(context);
        
        // Find and update the booking with matching timestamp (unique identifier)
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getTimestamp() == updatedBooking.getTimestamp()) {
                bookings.set(i, updatedBooking);
                break;
            }
        }
        
        // Save updated list
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String bookingsJson = gson.toJson(bookings);
        prefs.edit().putString(BOOKINGS_KEY, bookingsJson).apply();
    }

    /**
     * Get bookings filtered by status
     */
    public static List<BookingRequest> getBookingsByStatus(Context context, BookingStatus status) {
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
    public static BookingStats getBookingStats(Context context) {
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
