package com.vehiclebooking.ui.components

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.vehiclebooking.R
import com.vehiclebooking.security.InputValidator

/**
 * Custom view for displaying password strength
 * Shows visual indicator and text feedback
 */
class PasswordStrengthIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val progressBar: ProgressBar
    private val strengthText: TextView
    
    enum class PasswordStrength(val score: Int, val label: String, val color: Int) {
        WEAK(25, "Weak", Color.parseColor("#F44336")),           // Red
        FAIR(50, "Fair", Color.parseColor("#FF9800")),           // Orange
        GOOD(75, "Good", Color.parseColor("#FFC107")),           // Amber
        STRONG(100, "Strong", Color.parseColor("#4CAF50"))       // Green
    }

    init {
        orientation = VERTICAL
        
        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.view_password_strength, this, true)
        
        progressBar = findViewById(R.id.password_strength_progress)
        strengthText = findViewById(R.id.password_strength_text)
        
        // Initialize with empty state
        updateStrength("")
    }

    /**
     * Update the strength indicator based on password
     * @param password The password to evaluate
     */
    fun updateStrength(password: String) {
        val strength = calculatePasswordStrength(password)
        
        progressBar.progress = strength.score
        progressBar.progressTintList = android.content.res.ColorStateList.valueOf(strength.color)
        strengthText.text = strength.label
        strengthText.setTextColor(strength.color)
        
        // Add accessibility
        contentDescription = "Password strength: ${strength.label}"
    }

    /**
     * Calculate password strength based on various criteria
     */
    private fun calculatePasswordStrength(password: String): PasswordStrength {
        if (password.isEmpty()) {
            return PasswordStrength.WEAK
        }

        var score = 0
        
        // Length check (max 40 points)
        score += when {
            password.length >= 12 -> 40
            password.length >= 8 -> 30
            password.length >= 6 -> 20
            else -> 10
        }
        
        // Uppercase letters (15 points)
        if (password.any { it.isUpperCase() }) score += 15
        
        // Lowercase letters (15 points)
        if (password.any { it.isLowerCase() }) score += 15
        
        // Numbers (15 points)
        if (password.any { it.isDigit() }) score += 15
        
        // Special characters (15 points)
        if (password.any { !it.isLetterOrDigit() }) score += 15
        
        // Determine strength level
        return when {
            score >= 85 -> PasswordStrength.STRONG
            score >= 60 -> PasswordStrength.GOOD
            score >= 40 -> PasswordStrength.FAIR
            else -> PasswordStrength.WEAK
        }
    }

    /**
     * Check if current password meets minimum requirements
     */
    fun meetsRequirements(password: String): Boolean {
        return InputValidator.isStrongPassword(password)
    }

    /**
     * Get list of requirements that are not met
     */
    fun getUnmetRequirements(password: String): List<String> {
        val unmet = mutableListOf<String>()
        
        if (password.length < 8) {
            unmet.add("At least 8 characters")
        }
        if (!password.any { it.isUpperCase() }) {
            unmet.add("One uppercase letter")
        }
        if (!password.any { it.isLowerCase() }) {
            unmet.add("One lowercase letter")
        }
        if (!password.any { it.isDigit() }) {
            unmet.add("One number")
        }
        
        return unmet
    }
}
