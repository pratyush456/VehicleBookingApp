package com.vehiclebooking.security

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
 * Certificate pinning configuration for secure API communication
 * Prevents man-in-the-middle attacks by validating SSL certificates
 */
object CertificatePinningConfig {

    /**
     * Create OkHttpClient with certificate pinning
     * @param baseUrl The API base URL (e.g., "your-api-server.com")
     * @param pins List of SHA-256 certificate pins
     * @return Configured OkHttpClient with pinning
     */
    fun createPinnedClient(baseUrl: String, pins: List<String>): OkHttpClient.Builder {
        val certificatePinner = CertificatePinner.Builder()
            .apply {
                pins.forEach { pin ->
                    add(baseUrl, "sha256/$pin")
                }
            }
            .build()

        return OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
    }

    /**
     * Production certificate pins
     * TODO: Replace with actual certificate pins from your API server
     * 
     * To get certificate pins:
     * 1. Using OpenSSL:
     *    openssl s_client -connect your-api-server.com:443 | \
     *    openssl x509 -pubkey -noout | \
     *    openssl pkey -pubin -outform der | \
     *    openssl dgst -sha256 -binary | \
     *    openssl enc -base64
     * 
     * 2. Using OkHttp (will fail first time and show pins in error):
     *    Just run the app and check logcat for the actual pins
     */
    object ProductionPins {
        const val API_DOMAIN = "your-api-server.com"
        
        // Primary certificate pin
        const val PRIMARY_PIN = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
        
        // Backup certificate pin (for certificate rotation)
        const val BACKUP_PIN = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="
        
        fun getPins(): List<String> = listOf(PRIMARY_PIN, BACKUP_PIN)
    }

    /**
     * Development/Testing pins
     * For local development or staging servers
     */
    object DevelopmentPins {
        const val API_DOMAIN = "staging-api-server.com"
        
        const val STAGING_PIN = "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC="
        
        fun getPins(): List<String> = listOf(STAGING_PIN)
    }

    /**
     * Trust manager for debugging (ONLY FOR DEVELOPMENT)
     * WARNING: Never use in production!
     */
    @Suppress("CustomX509TrustManager")
    class UnsafeTrustManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }

    /**
     * Create client without pinning (for development only)
     * WARNING: Never use in production builds!
     */
    fun createUnsafeClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            // No certificate pinning - for development only
    }
}
