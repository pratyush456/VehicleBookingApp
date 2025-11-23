package com.vehiclebooking.ui.examples

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.vehiclebooking.R
import com.vehiclebooking.security.InputValidator
import com.vehiclebooking.ui.components.PasswordStrengthIndicator

/**
 * Example Activity showing how to use PasswordStrengthIndicator
 * This can be adapted for actual registration screens
 */
class ExampleRegistrationActivity : AppCompatActivity() {

    private lateinit var passwordInput: TextInputEditText
    private lateinit var strengthIndicator: PasswordStrengthIndicator
    private lateinit var registerButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_registration_with_strength)

        // Initialize views
        passwordInput = findViewById(R.id.et_password)
        strengthIndicator = findViewById(R.id.password_strength_indicator)
        registerButton = findViewById(R.id.btn_register)

        setupPasswordStrengthListener()
        setupRegisterButton()
    }

    private fun setupPasswordStrengthListener() {
        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val password = s?.toString() ?: ""
                
                // Update strength indicator
                strengthIndicator.updateStrength(password)
                
                // Enable/disable register button based on password strength
                val meetsRequirements = strengthIndicator.meetsRequirements(password)
                registerButton.isEnabled = meetsRequirements
                
                // Optional: Show unmet requirements
                if (!meetsRequirements && password.isNotEmpty()) {
                    val unmet = strengthIndicator.getUnmetRequirements(password)
                    // You could show these in a tooltip or helper text
                }
            }
        })
    }

    private fun setupRegisterButton() {
        registerButton.setOnClickListener {
            val password = passwordInput.text?.toString() ?: ""
            
            if (strengthIndicator.meetsRequirements(password)) {
                // Proceed with registration
                Toast.makeText(this, "Password is strong enough!", Toast.LENGTH_SHORT).show()
                // Call your registration logic here
            } else {
                Toast.makeText(this, "Password does not meet requirements", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
