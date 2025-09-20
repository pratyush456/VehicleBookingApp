package com.vehiclebooking;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class NotificationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // This service can be extended to handle background notifications
        // For example, checking for new bookings from a server
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}