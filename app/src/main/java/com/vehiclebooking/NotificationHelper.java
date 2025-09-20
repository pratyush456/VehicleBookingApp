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
                .setSmallIcon(R.drawable.ic_notification) // You'll need to add this icon
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

    private int generateNotificationId() {
        return (int) System.currentTimeMillis();
    }
}