package com.vehiclebooking.security

import android.content.Context
import android.util.Log
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.FileWriter

/**
 * Security event logger for tracking authentication attempts and security events
 * Logs failed logins, suspicious activities, and security-related events
 */
object SecurityLogger {

    private const val TAG = "SecurityLogger"
    private const val LOG_FILE_NAME = "security_events.log"
    private const val MAX_LOG_SIZE = 1024 * 1024 // 1 MB
    private const val MAX_FAILED_ATTEMPTS = 5
    private const val LOCKOUT_DURATION_MINUTES = 15

    enum class SecurityEvent {
        LOGIN_SUCCESS,
        LOGIN_FAILED,
        LOGIN_LOCKED,
        LOGOUT,
        PASSWORD_CHANGED,
        BIOMETRIC_ENABLED,
        BIOMETRIC_DISABLED,
        BIOMETRIC_FAILED,
        ACCOUNT_CREATED,
        ACCOUNT_DELETED,
        SUSPICIOUS_ACTIVITY,
        CERTIFICATE_PINNING_FAILED,
        UNAUTHORIZED_ACCESS
    }

    /**
     * Log a security event
     */
    fun logEvent(
        context: Context,
        event: SecurityEvent,
        username: String? = null,
        details: String? = null
    ) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val logEntry = buildLogEntry(timestamp, event, username, details)
        
        // Log to Android logcat (only in debug builds)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.i(TAG, logEntry)
        }
        
        // Write to file
        writeToFile(context, logEntry)
        
        // Check for account lockout
        if (event == SecurityEvent.LOGIN_FAILED && username != null) {
            checkAndHandleLockout(context, username)
        }
    }

    /**
     * Log failed login attempt
     */
    fun logFailedLogin(context: Context, username: String, reason: String = "Invalid credentials") {
        logEvent(context, SecurityEvent.LOGIN_FAILED, username, reason)
    }

    /**
     * Log successful login
     */
    fun logSuccessfulLogin(context: Context, username: String) {
        // Clear failed attempts on successful login
        clearFailedAttempts(context, username)
        logEvent(context, SecurityEvent.LOGIN_SUCCESS, username)
    }

    /**
     * Check if account is locked due to too many failed attempts
     */
    fun isAccountLocked(context: Context, username: String): Boolean {
        val attempts = getFailedAttempts(context, username)
        val lockoutTime = getLockoutTime(context, username)
        
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            val now = System.currentTimeMillis()
            val lockoutExpiry = lockoutTime + (LOCKOUT_DURATION_MINUTES * 60 * 1000)
            
            if (now < lockoutExpiry) {
                return true
            } else {
                // Lockout expired, clear attempts
                clearFailedAttempts(context, username)
            }
        }
        
        return false
    }

    /**
     * Get remaining lockout time in minutes
     */
    fun getRemainingLockoutTime(context: Context, username: String): Int {
        val lockoutTime = getLockoutTime(context, username)
        val now = System.currentTimeMillis()
        val lockoutExpiry = lockoutTime + (LOCKOUT_DURATION_MINUTES * 60 * 1000)
        
        if (now < lockoutExpiry) {
            return ((lockoutExpiry - now) / (60 * 1000)).toInt()
        }
        
        return 0
    }

    /**
     * Get security logs (for admin viewing)
     */
    fun getSecurityLogs(context: Context, maxLines: Int = 100): List<String> {
        val logFile = File(context.filesDir, LOG_FILE_NAME)
        
        if (!logFile.exists()) {
            return emptyList()
        }
        
        return try {
            logFile.readLines().takeLast(maxLines)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading security logs", e)
            emptyList()
        }
    }

    /**
     * Clear security logs
     */
    fun clearLogs(context: Context) {
        val logFile = File(context.filesDir, LOG_FILE_NAME)
        logFile.delete()
        Log.i(TAG, "Security logs cleared")
    }

    // Private helper methods

    private fun buildLogEntry(
        timestamp: String,
        event: SecurityEvent,
        username: String?,
        details: String?
    ): String {
        val user = username ?: "UNKNOWN"
        val info = details ?: ""
        return "[$timestamp] $event | User: $user | $info"
    }

    private fun writeToFile(context: Context, logEntry: String) {
        try {
            val logFile = File(context.filesDir, LOG_FILE_NAME)
            
            // Rotate log if too large
            if (logFile.exists() && logFile.length() > MAX_LOG_SIZE) {
                rotateLogs(context)
            }
            
            FileWriter(logFile, true).use { writer ->
                writer.appendLine(logEntry)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error writing to security log", e)
        }
    }

    private fun rotateLogs(context: Context) {
        val logFile = File(context.filesDir, LOG_FILE_NAME)
        val backupFile = File(context.filesDir, "$LOG_FILE_NAME.old")
        
        // Delete old backup
        backupFile.delete()
        
        // Rename current log to backup
        logFile.renameTo(backupFile)
    }

    private fun checkAndHandleLockout(context: Context, username: String) {
        val attempts = incrementFailedAttempts(context, username)
        
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            setLockoutTime(context, username, System.currentTimeMillis())
            logEvent(
                context,
                SecurityEvent.LOGIN_LOCKED,
                username,
                "Account locked for $LOCKOUT_DURATION_MINUTES minutes due to $attempts failed attempts"
            )
        }
    }

    private fun getFailedAttempts(context: Context, username: String): Int {
        return SecurePreferences.getInt("failed_attempts_$username", 0)
    }

    private fun incrementFailedAttempts(context: Context, username: String): Int {
        val attempts = getFailedAttempts(context, username) + 1
        SecurePreferences.putInt("failed_attempts_$username", attempts)
        return attempts
    }

    private fun clearFailedAttempts(context: Context, username: String) {
        SecurePreferences.remove("failed_attempts_$username")
        SecurePreferences.remove("lockout_time_$username")
    }

    private fun getLockoutTime(context: Context, username: String): Long {
        return SecurePreferences.getLong("lockout_time_$username", 0L)
    }

    private fun setLockoutTime(context: Context, username: String, time: Long) {
        SecurePreferences.putLong("lockout_time_$username", time)
    }
}
