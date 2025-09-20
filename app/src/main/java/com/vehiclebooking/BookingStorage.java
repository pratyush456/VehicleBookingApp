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
}