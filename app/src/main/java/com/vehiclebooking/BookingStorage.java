package com.vehiclebooking;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vehiclebooking.data.AppDatabase;
import com.vehiclebooking.data.dao.BookingDao;
import com.vehiclebooking.data.model.BookingEntity;

import java.util.ArrayList;
import java.util.List;

import org.threeten.bp.LocalDate;

import org.threeten.bp.LocalDate;

public class BookingStorage {
    
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
        BookingDao bookingDao = AppDatabase.getDatabase(context).bookingDao();
        
        // Check for duplicate booking ID to prevent duplicates
        if (booking.getBookingId() != null) {
            BookingEntity existing = bookingDao.getBookingById(booking.getBookingId());
            if (existing != null) {
                // Duplicate booking ID found, don't save
                return;
            }
        }
        
        // Add new booking
        bookingDao.insertBookingBlocking(new BookingEntity(booking));
    }

    @NonNull
    public static List<BookingRequest> getAllBookings(@NonNull Context context) {
        BookingDao bookingDao = AppDatabase.getDatabase(context).bookingDao();
        List<BookingEntity> entities = bookingDao.getAllBookingsBlocking();
        
        List<BookingRequest> bookings = new ArrayList<>();
        for (BookingEntity entity : entities) {
            bookings.add(entity.toBookingRequest());
        }
        return bookings;
    }

    public static void clearAllBookings(@NonNull Context context) {
        BookingDao bookingDao = AppDatabase.getDatabase(context).bookingDao();
        bookingDao.deleteAllBookingsBlocking();
    }

    /**
     * Update an existing booking (useful for status changes and modifications)
     */
    public static void updateBooking(@NonNull Context context, @NonNull BookingRequest updatedBooking) {
        BookingDao bookingDao = AppDatabase.getDatabase(context).bookingDao();
        bookingDao.updateBookingBlocking(new BookingEntity(updatedBooking));
    }
    
    /**
     * Get bookings filtered by status
     */
    @NonNull
    public static List<BookingRequest> getBookingsByStatus(@NonNull Context context, @NonNull BookingStatus status) {
        BookingDao bookingDao = AppDatabase.getDatabase(context).bookingDao();
        List<BookingEntity> entities = bookingDao.getBookingsByStatus(status);
        
        List<BookingRequest> bookings = new ArrayList<>();
        for (BookingEntity entity : entities) {
            bookings.add(entity.toBookingRequest());
        }
        return bookings;
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
        BookingDao bookingDao = AppDatabase.getDatabase(context).bookingDao();
        BookingEntity existing = bookingDao.getBookingById(bookingId);
        
        if (existing != null) {
            // Very rare collision, generate new ID
            bookingId = "BK" + System.currentTimeMillis() + (int)(Math.random() * 10000);
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
