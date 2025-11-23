package com.vehiclebooking.data.api

import com.google.gson.GsonBuilder
import com.vehiclebooking.BuildConfig
import com.vehiclebooking.security.CertificatePinningConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit client singleton with certificate pinning
 * Configures Retrofit with base URL, logging, timeouts, and SSL pinning
 */
object RetrofitClient {
    
    // TODO: Replace with your actual API base URL
    private const val BASE_URL = "https://your-api-server.com/api/v1/"
    private const val API_DOMAIN = "your-api-server.com"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
    
    private val okHttpClient: OkHttpClient by lazy {
        // Use certificate pinning in production, skip in debug for easier testing
        val builder = if (BuildConfig.DEBUG) {
            // Development: No pinning for easier testing
            OkHttpClient.Builder()
        } else {
            // Production: Enable certificate pinning
            CertificatePinningConfig.createPinnedClient(
                API_DOMAIN,
                CertificatePinningConfig.ProductionPins.getPins()
            )
        }
        
        builder
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private val gson = GsonBuilder()
        .setLenient()
        .create()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    val bookingApiService: BookingApiService by lazy {
        retrofit.create(BookingApiService::class.java)
    }
}
