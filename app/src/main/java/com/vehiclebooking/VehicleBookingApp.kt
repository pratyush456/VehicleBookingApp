package com.vehiclebooking

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.vehiclebooking.security.SecurePreferences

/**
 * Application class for Vehicle Booking App
 * Handles dynamic colors, dark mode configuration, and security initialization
 */
class VehicleBookingApp : Application() {
    
    companion object {
        const val PREFS_NAME = "app_settings"
        const val KEY_NIGHT_MODE = "night_mode"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize secure encrypted preferences
        SecurePreferences.init(this)
        
        // Enable dynamic colors (Android 12+)
        // Colors will adapt to user's wallpaper
        DynamicColors.applyToActivitiesIfAvailable(this)
        
        // Set default night mode from secure preferences
        val nightMode = SecurePreferences.getInt(
            KEY_NIGHT_MODE,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // Default: follow system
        )
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
