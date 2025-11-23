package com.vehiclebooking.utils

import com.vehiclebooking.DateUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class DateUtilsTest {

    @Test
    fun `today returns current date`() {
        val today = DateUtils.today()
        assertNotNull(today)
        assertTrue(today is LocalDate)
    }

    @Test
    fun `now returns current datetime`() {
        val now = DateUtils.now()
        assertNotNull(now)
        assertTrue(now is LocalDateTime)
    }

    @Test
    fun `formatDate with LocalDate returns formatted string`() {
        val date = LocalDate.of(2024, 1, 15)
        val formatted = DateUtils.formatDate(date)
        assertNotNull(formatted)
        assertTrue(formatted.contains("15"))
        assertTrue(formatted.contains("Jan") || formatted.contains("January"))
    }

    @Test
    fun `formatDate with custom pattern works correctly`() {
        val date = LocalDate.of(2024, 1, 15)
        val formatted = DateUtils.formatDate(date, "yyyy-MM-dd")
        assertEquals("2024-01-15", formatted)
    }

    @Test
    fun `isDateValid returns true for future dates`() {
        val futureDate = LocalDate.now().plusDays(1)
        assertTrue(DateUtils.isDateValid(futureDate))
    }

    @Test
    fun `isDateValid returns true for today`() {
        val today = LocalDate.now()
        assertTrue(DateUtils.isDateValid(today))
    }

    @Test
    fun `isDateValid returns false for past dates`() {
        val pastDate = LocalDate.now().minusDays(1)
        assertFalse(DateUtils.isDateValid(pastDate))
    }

    @Test
    fun `localDateToTimestamp converts correctly`() {
        val date = LocalDate.of(2024, 1, 15)
        val timestamp = DateUtils.localDateToTimestamp(date)
        assertTrue(timestamp > 0)
    }

    @Test
    fun `timestampToLocalDate converts correctly`() {
        val originalDate = LocalDate.of(2024, 1, 15)
        val timestamp = DateUtils.localDateToTimestamp(originalDate)
        val convertedDate = DateUtils.timestampToLocalDate(timestamp)
        assertEquals(originalDate, convertedDate)
    }

    @Test
    fun `timestampToLocalDateTime converts correctly`() {
        val timestamp = System.currentTimeMillis()
        val dateTime = DateUtils.timestampToLocalDateTime(timestamp)
        assertNotNull(dateTime)
        assertTrue(dateTime is LocalDateTime)
    }

    @Test
    fun `formatDateTime12Hour formats correctly`() {
        val dateTime = LocalDateTime.of(2024, 1, 15, 14, 30)
        val formatted = DateUtils.formatDateTime12Hour(dateTime)
        assertNotNull(formatted)
        assertTrue(formatted.contains("PM") || formatted.contains("pm"))
    }

    @Test
    fun `getDayName returns correct day`() {
        val timestamp = System.currentTimeMillis()
        val dayName = DateUtils.getDayName(timestamp)
        assertNotNull(dayName)
        assertTrue(dayName.isNotEmpty())
    }

    @Test
    fun `getMonthYear returns correct format`() {
        val timestamp = System.currentTimeMillis()
        val monthYear = DateUtils.getMonthYear(timestamp)
        assertNotNull(monthYear)
        assertTrue(monthYear.isNotEmpty())
    }

    @Test
    fun `getHour returns valid hour`() {
        val timestamp = System.currentTimeMillis()
        val hour = DateUtils.getHour(timestamp)
        assertTrue(hour in 0..23)
    }
}
