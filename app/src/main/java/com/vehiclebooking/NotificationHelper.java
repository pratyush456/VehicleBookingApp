package com.vehiclebooking;

import android.content.Context;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class NotificationHelper {
    
    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void sendBookingNotification(BookingRequest bookingRequest) {
        String title = "New Vehicle Booking Request";
        String content = "New booking from " + bookingRequest.getSource() + " to " + bookingRequest.getDestination() + "\n" + bookingRequest.getBookingSummary();

        scheduleNotification(title, content);

        // Also save booking to local storage for future reference
        BookingStorage.saveBooking(context, bookingRequest);
    }

    /**
     * Send notification for booking status changes
     */
    public static void sendStatusChangeNotification(Context context, BookingRequest booking, BookingStatus newStatus) {
        NotificationHelper helper = new NotificationHelper(context);
        helper.sendStatusUpdateNotification(booking, newStatus);
    }

    private void sendStatusUpdateNotification(BookingRequest booking, BookingStatus status) {
        String title = "Booking Status Update " + status.getIcon();
        String detailedText = createStatusUpdateMessage(booking, status);

        scheduleNotification(title, detailedText);
    }

    private String createStatusUpdateMessage(BookingRequest booking, BookingStatus status) {
        StringBuilder message = new StringBuilder();
        message.append("Booking ID: BK").append(String.valueOf(booking.getTimestamp()).substring(8)).append("\n");
        message.append("Route: ").append(booking.getSource()).append(" → ").append(booking.getDestination()).append("\n");
        message.append("Travel Date: ").append(booking.getFormattedTravelDate()).append("\n\n");
        message.append("Status: ").append(status.getIcon()).append(" ").append(status.getDisplayName()).append("\n");
        message.append(status.getDescription());
        
        return message.toString();
    }

    /**
     * Send confirmation notification for reminder setup
     */
    public static void sendReminderConfirmation(Context context, BookingRequest booking, String reminderType) {
        NotificationHelper helper = new NotificationHelper(context);
        helper.sendReminderSetNotification(booking, reminderType);
    }

    private void sendReminderSetNotification(BookingRequest booking, String reminderType) {
        String bookingId = "BK" + String.valueOf(booking.getTimestamp()).substring(8);
        String title = "⏰ Reminder Set";
        String content = "Reminder set for booking #" + bookingId + ": " + reminderType + "\n" +
                "Booking: " + booking.getSource() + " → " + booking.getDestination() + "\n" +
                "Travel Date: " + booking.getFormattedTravelDate();
        
        scheduleNotification(title, content);
    }

    private void scheduleNotification(String title, String message) {
        Data data = new Data.Builder()
                .putString(NotificationWorker.KEY_TITLE, title)
                .putString(NotificationWorker.KEY_MESSAGE, message)
                .build();

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueue(notificationWork);
    }
}

