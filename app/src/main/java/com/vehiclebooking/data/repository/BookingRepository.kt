package com.vehiclebooking.data.repository

import com.vehiclebooking.BookingRequest
import com.vehiclebooking.data.AppDatabase
import com.vehiclebooking.data.dao.BookingDao
import com.vehiclebooking.data.model.BookingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.content.Context

class BookingRepository(context: Context) {
    private val bookingDao: BookingDao = AppDatabase.getDatabase(context).bookingDao()

    // Flow-based reactive queries
    val allBookings: Flow<List<BookingRequest>> = bookingDao.getAllBookings()
        .map { entities -> entities.map { it.toBookingRequest() } }

    fun getBookingByTimestamp(timestamp: Long): Flow<BookingRequest?> =
        bookingDao.getBookingByTimestamp(timestamp)
            .map { it?.toBookingRequest() }

    fun getBookingsByPhoneNumber(phoneNumber: String): Flow<List<BookingRequest>> =
        bookingDao.getBookingsByPhoneNumber(phoneNumber)
            .map { entities -> entities.map { it.toBookingRequest() } }

    val bookingCount: Flow<Int> = bookingDao.getBookingCount()

    // Suspend functions for write operations
    suspend fun insertBooking(booking: BookingRequest) {
        bookingDao.insertBooking(BookingEntity(booking))
    }

    suspend fun insertBookings(bookings: List<BookingRequest>) {
        bookingDao.insertBookings(bookings.map { BookingEntity(it) })
    }

    suspend fun updateBooking(booking: BookingRequest) {
        bookingDao.updateBooking(BookingEntity(booking))
    }

    suspend fun deleteBooking(booking: BookingRequest) {
        bookingDao.deleteBooking(BookingEntity(booking))
    }

    suspend fun deleteAllBookings() {
        bookingDao.deleteAllBookings()
    }

    // Synchronous helper for compatibility (blocks current thread - use sparingly)
    suspend fun getAllBookingsSync(): List<BookingRequest> {
        // This is a workaround for legacy code - prefer using the Flow
        return bookingDao.getAllBookings()
            .map { entities -> entities.map { it.toBookingRequest() } }
            .let { flow ->
                // Collect first emission
                var result: List<BookingRequest> = emptyList()
                flow.collect { result = it }
                result
            }
    }
}
