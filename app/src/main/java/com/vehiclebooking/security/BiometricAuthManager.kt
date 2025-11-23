package com.vehiclebooking.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Manager for biometric authentication (fingerprint, face, iris)
 * Handles checking availability and performing authentication
 */
class BiometricAuthManager(private val activity: FragmentActivity) {

    companion object {
        private const val PREF_BIOMETRIC_ENABLED = "biometric_enabled"
    }

    /**
     * Check if biometric authentication is available on this device
     */
    fun isBiometricAvailable(): BiometricStatus {
        val biometricManager = BiometricManager.from(activity)
        
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricStatus.Available
            
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricStatus.NoHardware
            
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricStatus.HardwareUnavailable
            
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricStatus.NoneEnrolled
            
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                BiometricStatus.SecurityUpdateRequired
            
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                BiometricStatus.Unsupported
            
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                BiometricStatus.Unknown
            
            else -> BiometricStatus.Unknown
        }
    }

    /**
     * Check if user has enabled biometric authentication
     */
    fun isBiometricEnabled(): Boolean {
        return SecurePreferences.getBoolean(PREF_BIOMETRIC_ENABLED, false)
    }

    /**
     * Enable or disable biometric authentication
     */
    fun setBiometricEnabled(enabled: Boolean) {
        SecurePreferences.putBoolean(PREF_BIOMETRIC_ENABLED, enabled)
    }

    /**
     * Authenticate user with biometric
     * @param title Title for the biometric prompt
     * @param subtitle Subtitle for the biometric prompt
     * @param description Description for the biometric prompt
     * @param onSuccess Callback when authentication succeeds
     * @param onError Callback when authentication fails
     */
    fun authenticate(
        title: String = "Biometric Authentication",
        subtitle: String = "Verify your identity",
        description: String = "Use your fingerprint or face to authenticate",
        onSuccess: () -> Unit,
        onError: (errorCode: Int, errorMessage: String) -> Unit,
        onFailed: () -> Unit = {}
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Quick login with biometric
     * Authenticates and returns stored username if successful
     */
    fun quickLogin(
        onSuccess: (username: String) -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        if (!isBiometricEnabled()) {
            onError("Biometric authentication is not enabled")
            return
        }

        val savedUsername = SecurePreferences.getString("biometric_username")
        if (savedUsername.isNullOrEmpty()) {
            onError("No saved user for biometric login")
            return
        }

        authenticate(
            title = "Quick Login",
            subtitle = "Login with biometrics",
            description = "Use your fingerprint or face to login",
            onSuccess = {
                onSuccess(savedUsername)
            },
            onError = { _, message ->
                onError(message)
            }
        )
    }

    /**
     * Save username for biometric quick login
     */
    fun saveUsernameForBiometric(username: String) {
        SecurePreferences.putString("biometric_username", username)
    }

    /**
     * Clear saved biometric data
     */
    fun clearBiometricData() {
        SecurePreferences.remove("biometric_username")
        setBiometricEnabled(false)
    }

    /**
     * Get user-friendly message for biometric status
     */
    fun getStatusMessage(status: BiometricStatus): String {
        return when (status) {
            BiometricStatus.Available ->
                "Biometric authentication is available"
            
            BiometricStatus.NoHardware ->
                "This device doesn't have biometric hardware"
            
            BiometricStatus.HardwareUnavailable ->
                "Biometric hardware is currently unavailable"
            
            BiometricStatus.NoneEnrolled ->
                "No biometric credentials enrolled. Please set up fingerprint or face unlock in device settings"
            
            BiometricStatus.SecurityUpdateRequired ->
                "Security update required for biometric authentication"
            
            BiometricStatus.Unsupported ->
                "Biometric authentication is not supported"
            
            BiometricStatus.Unknown ->
                "Biometric status unknown"
        }
    }

    /**
     * Biometric availability status
     */
    sealed class BiometricStatus {
        object Available : BiometricStatus()
        object NoHardware : BiometricStatus()
        object HardwareUnavailable : BiometricStatus()
        object NoneEnrolled : BiometricStatus()
        object SecurityUpdateRequired : BiometricStatus()
        object Unsupported : BiometricStatus()
        object Unknown : BiometricStatus()
    }
}
