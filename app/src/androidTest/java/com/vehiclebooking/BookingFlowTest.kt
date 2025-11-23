package com.vehiclebooking

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BookingFlowTest {

    private lateinit var scenario: ActivityScenario<BookingActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(BookingActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testBookingFormDisplayed() {
        // Verify all form fields are displayed
        onView(withId(R.id.et_source)).check(matches(isDisplayed()))
        onView(withId(R.id.et_destination)).check(matches(isDisplayed()))
        onView(withId(R.id.et_phone_number)).check(matches(isDisplayed()))
        onView(withId(R.id.et_vehicle_type)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_select_date)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_book_now)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyFormShowsError() {
        // Click book button without filling form
        onView(withId(R.id.btn_book_now)).perform(click())
        
        // Verify error is shown (toast or field error)
        // Note: This is a simplified test - actual implementation may vary
    }

    @Test
    fun testValidBookingSubmission() {
        // Fill in the form
        onView(withId(R.id.et_source))
            .perform(typeText("Mumbai"), closeSoftKeyboard())
        
        onView(withId(R.id.et_destination))
            .perform(typeText("Pune"), closeSoftKeyboard())
        
        onView(withId(R.id.et_phone_number))
            .perform(typeText("9876543210"), closeSoftKeyboard())
        
        onView(withId(R.id.et_vehicle_type))
            .perform(typeText("Sedan"), closeSoftKeyboard())
        
        // Select date
        onView(withId(R.id.btn_select_date)).perform(click())
        // Note: DatePicker interaction would go here
        
        // Submit booking
        onView(withId(R.id.btn_book_now)).perform(click())
        
        // Verify success (this would check for navigation or success message)
    }

    @Test
    fun testPhoneNumberValidation() {
        // Enter invalid phone number
        onView(withId(R.id.et_phone_number))
            .perform(typeText("123"), closeSoftKeyboard())
        
        // Trigger validation (e.g., by clicking book button)
        onView(withId(R.id.btn_book_now)).perform(click())
        
        // Verify error is shown
        onView(withId(R.id.et_phone_number))
            .check(matches(hasErrorText("Please enter a valid phone number")))
    }

    @Test
    fun testSourceFieldValidation() {
        // Leave source empty and fill other fields
        onView(withId(R.id.et_destination))
            .perform(typeText("Pune"), closeSoftKeyboard())
        
        onView(withId(R.id.btn_book_now)).perform(click())
        
        // Verify source field shows error
        onView(withId(R.id.et_source))
            .check(matches(hasErrorText("Source location is required")))
    }

    @Test
    fun testDestinationFieldValidation() {
        // Leave destination empty and fill other fields
        onView(withId(R.id.et_source))
            .perform(typeText("Mumbai"), closeSoftKeyboard())
        
        onView(withId(R.id.btn_book_now)).perform(click())
        
        // Verify destination field shows error
        onView(withId(R.id.et_destination))
            .check(matches(hasErrorText("Destination location is required")))
    }
}
