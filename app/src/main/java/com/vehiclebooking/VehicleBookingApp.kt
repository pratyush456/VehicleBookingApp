package com.vehiclebooking

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors

/**
 * Application class for Vehicle Booking App
 * Handles dynamic colors and dark mode configuration
 */
class VehicleBookingApp : Application() {
    
    companion object {
        const val PREFS_NAME = "app_settings"
        const val KEY_NIGHT_MODE = "night_mode"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Enable dynamic colors (Android 12+)
        // Colors will adapt to user's wallpaper
        DynamicColors.applyToActivitiesIfAvailable(this)
        
        // Set default night mode from preferences
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val nightMode = prefs.getInt(
            KEY_NIGHT_MODE,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // Default: follow system
        )
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
