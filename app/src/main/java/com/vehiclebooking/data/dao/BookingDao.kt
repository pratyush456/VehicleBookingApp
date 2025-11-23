package com.vehiclebooking.data.dao

import androidx.room.*
import com.vehiclebooking.BookingStatus
import com.vehiclebooking.data.model.BookingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE timestamp = :timestamp LIMIT 1")
    fun getBookingByTimestamp(timestamp: Long): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId LIMIT 1")
    fun getBookingById(bookingId: String): BookingEntity?

    @Query("SELECT * FROM bookings WHERE phoneNumber = :phoneNumber ORDER BY timestamp DESC")
    fun getBookingsByPhoneNumber(phoneNumber: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE status = :status ORDER BY timestamp DESC")
    fun getBookingsByStatus(status: BookingStatus): List<BookingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<BookingEntity>)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()

    @Query("SELECT COUNT(*) FROM bookings")
    fun getBookingCount(): Flow<Int>
    
    // Blocking wrappers for Java compatibility (to be deprecated)
    fun getAllBookingsBlocking(): List<BookingEntity> = runBlocking {
        getAllBookings().first()
    }
    
    fun insertBookingBlocking(booking: BookingEntity) = runBlocking {
        insertBooking(booking)
    }
    
    fun updateBookingBlocking(booking: BookingEntity) = runBlocking {
        updateBooking(booking)
    }
    
    fun deleteAllBookingsBlocking() = runBlocking {
        deleteAllBookings()
    }
}
