package com.vehiclebooking.data.repository

import android.content.Context
import com.vehiclebooking.BookingRequest
import com.vehiclebooking.data.AppDatabase
import com.vehiclebooking.data.api.RetrofitClient
import com.vehiclebooking.data.dao.BookingDao
import com.vehiclebooking.data.firebase.FirestoreManager
import com.vehiclebooking.data.model.BookingEntity
import com.vehiclebooking.data.model.dto.BookingDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Offline-first repository for booking operations
 * Implements single source of truth pattern with Room as cache
 */
class BookingRepository(context: Context) {
    private val bookingDao: BookingDao = AppDatabase.getDatabase(context).bookingDao()
    private val apiService = RetrofitClient.bookingApiService
    private val firestoreManager = FirestoreManager()

    /**
     * Get all bookings with offline-first pattern
     * Loads from DB first, then syncs with network if needed
     */
    fun getAllBookings(): Flow<Resource<List<BookingRequest>>> {
        return object : NetworkBoundResource<List<BookingRequest>, List<BookingDto>>() {
            
            override suspend fun loadFromDb(): Flow<List<BookingRequest>> {
                return bookingDao.getAllBookings()
                    .map { entities -> entities.map { it.toBookingRequest() } }
            }
            
            override suspend fun fetchFromNetwork(): List<BookingDto> {
                // Try API first, fallback to Firestore
                return try {
                    apiService.getAllBookings()
                } catch (e: Exception) {
                    // If API fails, try Firestore
                    firestoreManager.fetchAllBookings()
                }
            }
            
            override suspend fun saveNetworkResult(data: List<BookingDto>) {
                val entities = data.map { it.toEntity() }
                bookingDao.insertBookings(entities)
            }
            
            override fun shouldFetch(data: List<BookingRequest>?): Boolean {
                // Fetch if data is empty or stale (can add timestamp logic)
                return data.isNullOrEmpty()
            }
        }.asFlow()
    }

    /**
     * Get bookings by phone number with offline-first
     */
    fun getBookingsByPhoneNumber(phoneNumber: String): Flow<Resource<List<BookingRequest>>> {
        return object : NetworkBoundResource<List<BookingRequest>, List<BookingDto>>() {
            
            override suspend fun loadFromDb(): Flow<List<BookingRequest>> {
                return bookingDao.getBookingsByPhoneNumber(phoneNumber)
                    .map { entities -> entities.map { it.toBookingRequest() } }
            }
            
            override suspend fun fetchFromNetwork(): List<BookingDto> {
                return try {
                    apiService.getBookingsByPhoneNumber(phoneNumber)
                } catch (e: Exception) {
                    emptyList()
                }
            }
            
            override suspend fun saveNetworkResult(data: List<BookingDto>) {
                val entities = data.map { it.toEntity() }
                bookingDao.insertBookings(entities)
            }
            
            override fun shouldFetch(data: List<BookingRequest>?): Boolean {
                return data.isNullOrEmpty()
            }
        }.asFlow()
    }

    /**
     * Create a new booking (saves locally and syncs to cloud)
     */
    suspend fun createBooking(booking: BookingRequest): Resource<BookingRequest> {
        return try {
            // Save to local DB first (offline-first)
            bookingDao.insertBooking(BookingEntity(booking))
            
            // Try to sync to cloud
            val dto = BookingDto.fromBookingRequest(booking)
            try {
                apiService.createBooking(dto)
                firestoreManager.uploadBooking(dto)
            } catch (e: Exception) {
                // Cloud sync failed, but local save succeeded
                // Can queue for later sync
            }
            
            Resource.Success(booking)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create booking")
        }
    }

    /**
     * Update an existing booking
     */
    suspend fun updateBooking(booking: BookingRequest): Resource<BookingRequest> {
        return try {
            bookingDao.updateBooking(BookingEntity(booking))
            
            // Sync to cloud
            val dto = BookingDto.fromBookingRequest(booking)
            try {
                booking.bookingId?.let { 
                    apiService.updateBooking(it, dto)
                    firestoreManager.uploadBooking(dto)
                }
            } catch (e: Exception) {
                // Cloud sync failed
            }
            
            Resource.Success(booking)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update booking")
        }
    }

    /**
     * Delete a booking
     */
    suspend fun deleteBooking(booking: BookingRequest): Resource<Unit> {
        return try {
            bookingDao.deleteBooking(BookingEntity(booking))
            
            // Sync deletion to cloud
            try {
                booking.bookingId?.let {
                    apiService.deleteBooking(it)
                    firestoreManager.deleteBooking(it)
                }
            } catch (e: Exception) {
                // Cloud sync failed
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete booking")
        }
    }

    /**
     * Listen to real-time Firestore changes
     * Use this to enable real-time sync across devices
     */
    fun syncWithFirestore(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firestoreManager.listenToBookingChanges().collect { dtos ->
                val entities = dtos.map { it.toEntity() }
                bookingDao.insertBookings(entities)
                emit(Resource.Success(Unit))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Firestore sync failed"))
        }
    }

    // Legacy compatibility methods
    val allBookings: Flow<List<BookingRequest>> = bookingDao.getAllBookings()
        .map { entities -> entities.map { it.toBookingRequest() } }

    fun getBookingByTimestamp(timestamp: Long): Flow<BookingRequest?> =
        bookingDao.getBookingByTimestamp(timestamp)
            .map { it?.toBookingRequest() }

    val bookingCount: Flow<Int> = bookingDao.getBookingCount()

    suspend fun insertBooking(booking: BookingRequest) {
        bookingDao.insertBooking(BookingEntity(booking))
    }

    suspend fun insertBookings(bookings: List<BookingRequest>) {
        bookingDao.insertBookings(bookings.map { BookingEntity(it) })
    }

    suspend fun deleteAllBookings() {
        bookingDao.deleteAllBookings()
    }
}
