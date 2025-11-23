package com.vehiclebooking.repository

import android.content.Context
import com.vehiclebooking.BookingRequest
import com.vehiclebooking.BookingStatus
import com.vehiclebooking.data.dao.BookingDao
import com.vehiclebooking.data.model.BookingEntity
import com.vehiclebooking.data.repository.BookingRepository
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.threeten.bp.LocalDate

class BookingRepositoryTest {

    private lateinit var repository: BookingRepository
    private lateinit var mockContext: Context
    private lateinit var mockDao: BookingDao

    @BeforeEach
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockDao = mockk(relaxed = true)
        
        // Mock AppDatabase.getDatabase()
        mockkStatic("com.vehiclebooking.data.AppDatabase")
        val mockDatabase = mockk<com.vehiclebooking.data.AppDatabase>(relaxed = true)
        every { com.vehiclebooking.data.AppDatabase.getDatabase(any()) } returns mockDatabase
        every { mockDatabase.bookingDao() } returns mockDao
        
        repository = BookingRepository(mockContext)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `allBookings flow emits mapped booking requests`() = runTest {
        // Given
        val entity = BookingEntity(
            source = "Mumbai",
            destination = "Pune",
            travelDate = LocalDate.now(),
            timestamp = System.currentTimeMillis(),
            phoneNumber = "1234567890",
            vehicleType = "Sedan",
            status = BookingStatus.PENDING,
            bookingId = "BK123"
        )
        every { mockDao.getAllBookings() } returns flowOf(listOf(entity))

        // When
        val result = repository.allBookings.first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Mumbai", result[0].source)
        assertEquals("Pune", result[0].destination)
    }

    @Test
    fun `insertBooking should call dao insert`() = runTest {
        // Given
        val booking = BookingRequest("Mumbai", "Pune", LocalDate.now())
        coEvery { mockDao.insertBooking(any()) } just Runs

        // When
        repository.insertBooking(booking)

        // Then
        coVerify(exactly = 1) { mockDao.insertBooking(any()) }
    }

    @Test
    fun `updateBooking should call dao update`() = runTest {
        // Given
        val booking = BookingRequest("Mumbai", "Pune", LocalDate.now())
        coEvery { mockDao.updateBooking(any()) } just Runs

        // When
        repository.updateBooking(booking)

        // Then
        coVerify(exactly = 1) { mockDao.updateBooking(any()) }
    }

    @Test
    fun `deleteAllBookings should call dao deleteAll`() = runTest {
        // Given
        coEvery { mockDao.deleteAllBookings() } just Runs

        // When
        repository.deleteAllBookings()

        // Then
        coVerify(exactly = 1) { mockDao.deleteAllBookings() }
    }

    @Test
    fun `getBookingByTimestamp returns correct booking`() = runTest {
        // Given
        val timestamp = System.currentTimeMillis()
        val entity = BookingEntity(
            source = "Mumbai",
            destination = "Pune",
            travelDate = LocalDate.now(),
            timestamp = timestamp,
            phoneNumber = "1234567890",
            vehicleType = "Sedan",
            status = BookingStatus.PENDING,
            bookingId = "BK123"
        )
        every { mockDao.getBookingByTimestamp(timestamp) } returns flowOf(entity)

        // When
        val result = repository.getBookingByTimestamp(timestamp).first()

        // Then
        assertNotNull(result)
        assertEquals("Mumbai", result?.source)
        assertEquals(timestamp, result?.timestamp)
    }

    @Test
    fun `bookingCount flow emits correct count`() = runTest {
        // Given
        every { mockDao.getBookingCount() } returns flowOf(5)

        // When
        val count = repository.bookingCount.first()

        // Then
        assertEquals(5, count)
    }
}
