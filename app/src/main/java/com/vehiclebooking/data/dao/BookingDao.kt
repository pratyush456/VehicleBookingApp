package com.vehiclebooking.data.dao

import androidx.room.*
import com.vehiclebooking.data.model.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE timestamp = :timestamp LIMIT 1")
    fun getBookingByTimestamp(timestamp: Long): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE phoneNumber = :phoneNumber ORDER BY timestamp DESC")
    fun getBookingsByPhoneNumber(phoneNumber: String): Flow<List<BookingEntity>>

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
}
