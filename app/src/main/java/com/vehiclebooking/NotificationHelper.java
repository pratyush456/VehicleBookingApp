package com.vehiclebooking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {
    private static final String CHANNEL_ID = "BOOKING_NOTIFICATIONS";
    private static final String CHANNEL_NAME = "Vehicle Booking Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for new vehicle booking requests";
    
    private Context context;
    private NotificationManagerCompat notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void sendBookingNotification(BookingRequest bookingRequest) {
        String title = "New Vehicle Booking Request";
        String content = bookingRequest.getBookingSummary();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_car_24) // Using car icon for vehicle booking notifications
                .setContentTitle(title)
                .setContentText("New booking from " + bookingRequest.getSource() + " to " + bookingRequest.getDestination())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        try {
            notificationManager.notify(generateNotificationId(), builder.build());
        } catch (SecurityException e) {
            // Handle the case where notification permission is not granted
            e.printStackTrace();
        }

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
        String shortText = "Status changed to " + status.getDisplayName();
        String detailedText = createStatusUpdateMessage(booking, status);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(shortText)
                .setStyle(new NotificationCompat.BigTextStyle()
                         .bigText(detailedText)
                         .setBigContentTitle(title))
                .setPriority(getNotificationPriority(status))
                .setAutoCancel(true)
                .setColor(status.getColor());

        // Add action button for completed bookings
        if (status == BookingStatus.COMPLETED) {
            builder.addAction(android.R.drawable.ic_dialog_email, "Rate Trip", null);
        }

        try {
            notificationManager.notify(generateNotificationId(), builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
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

    private int getNotificationPriority(BookingStatus status) {
        switch (status) {
            case CONFIRMED:
            case IN_PROGRESS:
                return NotificationCompat.PRIORITY_HIGH;
            case COMPLETED:
                return NotificationCompat.PRIORITY_DEFAULT;
            case CANCELLED:
                return NotificationCompat.PRIORITY_LOW;
            default:
                return NotificationCompat.PRIORITY_DEFAULT;
        }
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
        String content = "Reminder set for booking #" + bookingId + ": " + reminderType;
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                    "Booking: " + booking.getSource() + " → " + booking.getDestination() + "\n" +
                    "Travel Date: " + booking.getFormattedTravelDate() + "\n" +
                    "Reminder: " + reminderType
                ))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        try {
            notificationManager.notify(generateNotificationId(), builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private int generateNotificationId() {
        return (int) System.currentTimeMillis();
    }
}
