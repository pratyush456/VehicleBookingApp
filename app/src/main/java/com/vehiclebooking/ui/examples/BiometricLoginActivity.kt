package com.vehiclebooking.ui.examples

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.vehiclebooking.R
import com.vehiclebooking.UserManager
import com.vehiclebooking.security.BiometricAuthManager

/**
 * Example Activity showing biometric authentication implementation
 * Demonstrates login with fingerprint/face and biometric settings
 */
class BiometricLoginActivity : AppCompatActivity() {

    private lateinit var biometricManager: BiometricAuthManager
    private lateinit var btnBiometricLogin: MaterialButton
    private lateinit var switchBiometric: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric_login_example)

        biometricManager = BiometricAuthManager(this)
        
        btnBiometricLogin = findViewById(R.id.btn_biometric_login)
        switchBiometric = findViewById(R.id.switch_biometric)

        setupBiometricAvailability()
        setupBiometricLogin()
        setupBiometricToggle()
    }

    private fun setupBiometricAvailability() {
        val status = biometricManager.isBiometricAvailable()
        
        when (status) {
            is BiometricAuthManager.BiometricStatus.Available -> {
                // Biometric is available
                btnBiometricLogin.isEnabled = biometricManager.isBiometricEnabled()
                switchBiometric.isEnabled = true
                switchBiometric.isChecked = biometricManager.isBiometricEnabled()
            }
            
            is BiometricAuthManager.BiometricStatus.NoneEnrolled -> {
                // Show message to enroll biometric
                Toast.makeText(
                    this,
                    biometricManager.getStatusMessage(status),
                    Toast.LENGTH_LONG
                ).show()
                btnBiometricLogin.isEnabled = false
                switchBiometric.isEnabled = false
            }
            
            else -> {
                // Biometric not available
                Toast.makeText(
                    this,
                    biometricManager.getStatusMessage(status),
                    Toast.LENGTH_SHORT
                ).show()
                btnBiometricLogin.isEnabled = false
                switchBiometric.isEnabled = false
            }
        }
    }

    private fun setupBiometricLogin() {
        btnBiometricLogin.setOnClickListener {
            performBiometricLogin()
        }
    }

    private fun performBiometricLogin() {
        biometricManager.quickLogin(
            onSuccess = { username ->
                // Login successful
                Toast.makeText(
                    this,
                    "Welcome back, $username!",
                    Toast.LENGTH_SHORT
                ).show()
                
                // Proceed to main app
                // startActivity(Intent(this, MainActivity::class.java))
                // finish()
            },
            onError = { errorMessage ->
                Toast.makeText(
                    this,
                    "Login failed: $errorMessage",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun setupBiometricToggle() {
        switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Enable biometric - verify first
                biometricManager.authenticate(
                    title = "Enable Biometric Login",
                    subtitle = "Verify your identity",
                    description = "Authenticate to enable biometric login",
                    onSuccess = {
                        // Save current user for biometric login
                        val currentUser = UserManager.getInstance(this).currentUser
                        if (currentUser != null) {
                            biometricManager.saveUsernameForBiometric(currentUser.username)
                            biometricManager.setBiometricEnabled(true)
                            btnBiometricLogin.isEnabled = true
                            Toast.makeText(
                                this,
                                "Biometric login enabled",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            switchBiometric.isChecked = false
                            Toast.makeText(
                                this,
                                "Please login first",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onError = { _, errorMessage ->
                        switchBiometric.isChecked = false
                        Toast.makeText(
                            this,
                            "Failed to enable: $errorMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            } else {
                // Disable biometric
                biometricManager.clearBiometricData()
                btnBiometricLogin.isEnabled = false
                Toast.makeText(
                    this,
                    "Biometric login disabled",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
