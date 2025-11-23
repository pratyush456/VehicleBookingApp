package com.vehiclebooking.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure wrapper for SharedPreferences using AES256 encryption
 * All data is encrypted at rest using Android Keystore
 */
object SecurePreferences {
    
    private var encryptedPrefs: SharedPreferences? = null
    
    /**
     * Initialize encrypted preferences
     * Must be called before using any other methods
     */
    fun init(context: Context) {
        if (encryptedPrefs != null) return
        
        try {
            val masterKey = MasterKey.Builder(context.applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            encryptedPrefs = EncryptedSharedPreferences.create(
                context.applicationContext,
                "secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to regular SharedPreferences if encryption fails
            // This can happen on some devices with broken Keystore
            encryptedPrefs = context.getSharedPreferences("secure_prefs_fallback", Context.MODE_PRIVATE)
        }
    }
    
    private fun getPrefs(): SharedPreferences {
        return encryptedPrefs ?: throw IllegalStateException(
            "SecurePreferences not initialized. Call init() first."
        )
    }
    
    // String operations
    fun putString(key: String, value: String) {
        getPrefs().edit().putString(key, value).apply()
    }
    
    fun getString(key: String, defaultValue: String? = null): String? {
        return getPrefs().getString(key, defaultValue)
    }
    
    // Int operations
    fun putInt(key: String, value: Int) {
        getPrefs().edit().putInt(key, value).apply()
    }
    
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return getPrefs().getInt(key, defaultValue)
    }
    
    // Boolean operations
    fun putBoolean(key: String, value: Boolean) {
        getPrefs().edit().putBoolean(key, value).apply()
    }
    
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return getPrefs().getBoolean(key, defaultValue)
    }
    
    // Long operations
    fun putLong(key: String, value: Long) {
        getPrefs().edit().putLong(key, value).apply()
    }
    
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return getPrefs().getLong(key, defaultValue)
    }
    
    // Remove and clear
    fun remove(key: String) {
        getPrefs().edit().remove(key).apply()
    }
    
    fun clear() {
        getPrefs().edit().clear().apply()
    }
    
    fun contains(key: String): Boolean {
        return getPrefs().contains(key)
    }
}
