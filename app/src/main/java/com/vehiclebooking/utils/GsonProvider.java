package com.vehiclebooking.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vehiclebooking.LocalDateAdapter;
import org.threeten.bp.LocalDate;

/**
 * Singleton provider for Gson instance to avoid redundant object creation.
 */
public class GsonProvider {
    private static Gson gson;

    private GsonProvider() {
        // Private constructor to prevent instantiation
    }

    public static synchronized Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
        }
        return gson;
    }
}
