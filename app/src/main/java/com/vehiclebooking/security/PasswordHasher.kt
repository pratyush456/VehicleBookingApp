package com.vehiclebooking.security

import org.mindrot.jbcrypt.BCrypt

/**
 * Password hashing utility using bcrypt
 * Provides secure password hashing and verification
 */
object PasswordHasher {
    
    private const val BCRYPT_ROUNDS = 12
    
    /**
     * Hash a password using bcrypt
     * @param password Plain text password
     * @return Hashed password (safe to store)
     */
    fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS))
    }
    
    /**
     * Verify a password against a hash
     * @param password Plain text password to verify
     * @param hash Stored password hash
     * @return true if password matches, false otherwise
     */
    fun verify(password: String, hash: String): Boolean {
        return try {
            BCrypt.checkpw(password, hash)
        } catch (e: Exception) {
            // Invalid hash format or other error
            false
        }
    }
    
    /**
     * Check if a string is a valid bcrypt hash
     * @param hash String to check
     * @return true if valid bcrypt hash format
     */
    fun isValidHash(hash: String): Boolean {
        return hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$")
    }
}
