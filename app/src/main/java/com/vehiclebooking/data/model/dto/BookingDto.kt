package com.vehiclebooking.data.model.dto

import com.google.gson.annotations.SerializedName
import com.vehiclebooking.BookingRequest
import com.vehiclebooking.BookingStatus
import com.vehiclebooking.data.model.BookingEntity
import org.threeten.bp.LocalDate

/**
 * Data Transfer Object for Booking API responses
 * Maps between API JSON and internal models
 */
data class BookingDto(
    @SerializedName("booking_id")
    val bookingId: String,
    
    @SerializedName("source")
    val source: String,
    
    @SerializedName("destination")
    val destination: String,
    
    @SerializedName("travel_date")
    val travelDate: String, // ISO date string
    
    @SerializedName("timestamp")
    val timestamp: Long,
    
    @SerializedName("phone_number")
    val phoneNumber: String,
    
    @SerializedName("vehicle_type")
    val vehicleType: String?,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("status_history")
    val statusHistory: List<String>? = null
) {
    /**
     * Convert DTO to Entity for Room database
     */
    fun toEntity(): BookingEntity {
        return BookingEntity(
            source = source,
            destination = destination,
            travelDate = LocalDate.parse(travelDate),
            timestamp = timestamp,
            phoneNumber = phoneNumber,
            vehicleType = vehicleType,
            status = BookingStatus.valueOf(status),
            bookingId = bookingId
        )
    }

    companion object {
        /**
         * Convert BookingRequest to DTO for API
         */
        fun fromBookingRequest(booking: BookingRequest): BookingDto {
            return BookingDto(
                bookingId = booking.bookingId ?: "",
                source = booking.source,
                destination = booking.destination,
                travelDate = booking.travelDate.toString(),
                timestamp = booking.timestamp,
                phoneNumber = booking.phoneNumber,
                vehicleType = booking.vehicleType,
                status = booking.status.name,
                statusHistory = booking.statusHistory
            )
        }
        
        /**
         * Convert BookingEntity to DTO for API
         */
        fun fromEntity(entity: BookingEntity): BookingDto {
            return BookingDto(
                bookingId = entity.bookingId,
                source = entity.source,
                destination = entity.destination,
                travelDate = entity.travelDate.toString(),
                timestamp = entity.timestamp,
                phoneNumber = entity.phoneNumber,
                vehicleType = entity.vehicleType,
                status = entity.status.name,
                statusHistory = null // Can be enhanced later
            )
        }
    }
}
