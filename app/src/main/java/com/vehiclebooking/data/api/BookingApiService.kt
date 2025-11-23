package com.vehiclebooking.data.api

import com.vehiclebooking.data.model.dto.BookingDto
import retrofit2.http.*

/**
 * Retrofit API service for booking operations
 * Define your API endpoints here
 */
interface BookingApiService {
    
    @GET("bookings")
    suspend fun getAllBookings(): List<BookingDto>
    
    @GET("bookings/{id}")
    suspend fun getBookingById(@Path("id") bookingId: String): BookingDto
    
    @POST("bookings")
    suspend fun createBooking(@Body booking: BookingDto): BookingDto
    
    @PUT("bookings/{id}")
    suspend fun updateBooking(
        @Path("id") bookingId: String,
        @Body booking: BookingDto
    ): BookingDto
    
    @DELETE("bookings/{id}")
    suspend fun deleteBooking(@Path("id") bookingId: String)
    
    @GET("bookings/user/{phoneNumber}")
    suspend fun getBookingsByPhoneNumber(@Path("phoneNumber") phoneNumber: String): List<BookingDto>
}
