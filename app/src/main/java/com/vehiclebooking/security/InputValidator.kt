package com.vehiclebooking.security

import android.util.Patterns

/**
 * Input validation and sanitization utilities
 * Prevents injection attacks and validates user input
 */
object InputValidator {
    
    /**
     * Sanitize string input by removing potentially dangerous characters
     * @param input Raw user input
     * @return Sanitized string
     */
    fun sanitizeString(input: String): String {
        return input.trim()
            .replace(Regex("[<>\"'`]"), "") // Remove XSS characters
            .replace(Regex("\\s+"), " ") // Normalize whitespace
            .take(255) // Limit length
    }
    
    /**
     * Validate email address format
     * @param email Email to validate
     * @return true if valid email format
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && 
               Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
               email.length <= 254 // RFC 5321
    }
    
    /**
     * Validate phone number (10 digits)
     * @param phone Phone number to validate
     * @return true if valid phone format
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        return cleaned.matches(Regex("^[0-9]{10}$"))
    }
    
    /**
     * Check if password meets strength requirements
     * - At least 8 characters
     * - Contains uppercase letter
     * - Contains lowercase letter
     * - Contains digit
     * 
     * @param password Password to check
     * @return true if password is strong enough
     */
    fun isStrongPassword(password: String): Boolean {
        return password.length >= 8 &&
               password.any { it.isUpperCase() } &&
               password.any { it.isLowerCase() } &&
               password.any { it.isDigit() }
    }
    
    /**
     * Validate username format
     * - 3-20 characters
     * - Alphanumeric and underscore only
     * 
     * @param username Username to validate
     * @return true if valid username
     */
    fun isValidUsername(username: String): Boolean {
        return username.matches(Regex("^[a-zA-Z0-9_]{3,20}$"))
    }
    
    /**
     * Sanitize and validate booking ID
     * @param bookingId Booking ID to validate
     * @return Sanitized booking ID or null if invalid
     */
    fun sanitizeBookingId(bookingId: String): String? {
        val cleaned = bookingId.trim().uppercase()
        return if (cleaned.matches(Regex("^BK[0-9]{10,}$"))) {
            cleaned
        } else {
            null
        }
    }
}
