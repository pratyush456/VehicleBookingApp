package com.vehiclebooking;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;

import java.util.List;

public class ModifyBookingActivity extends AppCompatActivity {

    private EditText sourceEditText;
    private EditText destinationEditText;
    private EditText vehicleTypeEditText;
    private TextView selectedDateText;
    private Button selectDateButton;
    private Button saveChangesButton;
    private Button cancelButton;
    
    private LocalDate selectedDate;
    private BookingRequest currentBooking;
    private String bookingId;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize ThreeTenABP for java.time backport (API < 26)
        AndroidThreeTen.init(this);
        
        try {
            setContentView(R.layout.activity_modify_booking);

            // Get booking details from intent
            bookingId = getIntent().getStringExtra("booking_id");
            phoneNumber = getIntent().getStringExtra("phone_number");
            
            Toast.makeText(this, "Modify booking: " + bookingId + " for " + phoneNumber, Toast.LENGTH_SHORT).show();

            initializeViews();
            setupClickListeners();
            selectedDate = DateUtils.today(); // Initialize date before loading data
            loadBookingData();
        } catch (Exception e) {
            Toast.makeText(this, "Error in modify activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    private void initializeViews() {
        sourceEditText = findViewById(R.id.et_source);
        destinationEditText = findViewById(R.id.et_destination);
        vehicleTypeEditText = findViewById(R.id.et_vehicle_type);
        selectedDateText = findViewById(R.id.tv_selected_date);
        selectDateButton = findViewById(R.id.btn_select_date);
        saveChangesButton = findViewById(R.id.btn_save_changes);
        cancelButton = findViewById(R.id.btn_cancel);
    }

    private void setupClickListeners() {
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveModifications();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadBookingData() {
        // Find the booking by ID and phone number
        List<BookingRequest> allBookings = BookingStorage.getAllBookings(this);
        
        for (BookingRequest booking : allBookings) {
            if (booking.getBookingId() != null && 
                booking.getBookingId().equals(bookingId) &&
                booking.getPhoneNumber() != null &&
                booking.getPhoneNumber().equals(phoneNumber)) {
                currentBooking = booking;
                break;
            }
        }

        if (currentBooking == null) {
            Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate fields with current booking data
        sourceEditText.setText(currentBooking.getSource());
        destinationEditText.setText(currentBooking.getDestination());
        vehicleTypeEditText.setText(currentBooking.getVehicleType() != null ? currentBooking.getVehicleType() : "");
        
        // Set the date to the current booking date
        selectedDate = currentBooking.getTravelDate();
        updateSelectedDateDisplay();
    }

    private void showDatePicker() {
        LocalDate today = DateUtils.today();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    // DatePicker months are 0-based, LocalDate months are 1-based
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    updateSelectedDateDisplay();
                }
            },
            selectedDate.getYear(),
            selectedDate.getMonthValue() - 1, // DatePicker uses 0-based months
            selectedDate.getDayOfMonth()
        );
        
        // Set minimum date to today
        long todayTimestamp = DateUtils.localDateToTimestamp(today);
        datePickerDialog.getDatePicker().setMinDate(todayTimestamp);
        datePickerDialog.show();
    }

    private void updateSelectedDateDisplay() {
        String formattedDate = DateUtils.formatDate(selectedDate);
        selectedDateText.setText("Selected Date: " + formattedDate);
    }

    private void saveModifications() {
        String source = sourceEditText.getText().toString().trim();
        String destination = destinationEditText.getText().toString().trim();
        String vehicleType = vehicleTypeEditText.getText().toString().trim();

        // Validation
        if (source.isEmpty() || destination.isEmpty() || vehicleType.isEmpty() || 
            selectedDate == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if any changes were made
        boolean hasChanges = !source.equals(currentBooking.getSource()) ||
                           !destination.equals(currentBooking.getDestination()) ||
                           !vehicleType.equals(currentBooking.getVehicleType() != null ? currentBooking.getVehicleType() : "") ||
                           !selectedDate.equals(currentBooking.getTravelDate());

        if (!hasChanges) {
            Toast.makeText(this, "No changes detected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create modified copy (preserves booking ID, status, history)
        BookingRequest updatedBooking = currentBooking.createModifiedCopy(source, destination, selectedDate);
        updatedBooking.setVehicleType(vehicleType);
        
        // Update booking in storage
        BookingStorage.updateBooking(this, updatedBooking);
        
        // Update current booking reference
        currentBooking = updatedBooking;
        
        // If booking was confirmed, change it back to pending after modification
        BookingStatus originalStatus = currentBooking.getStatus();
        if (originalStatus == BookingStatus.CONFIRMED) {
            currentBooking.changeStatus(BookingStatus.PENDING, "Booking modified by customer - moved to pending for re-confirmation");
        } else {
            // Add status change to indicate modification
            currentBooking.changeStatus(currentBooking.getStatus(), "Booking modified by customer");
        }
        
        // Save the updated booking
        BookingStorage.updateBooking(this, currentBooking);

        String statusMessage = "";
        if (originalStatus == BookingStatus.CONFIRMED) {
            statusMessage = "\nStatus changed to Pending for re-confirmation";
        }
        
        Toast.makeText(this, "\u2705 Booking updated successfully!\nBooking ID: " + currentBooking.getBookingId() + statusMessage, 
                      Toast.LENGTH_LONG).show();

        // Return to the previous activity with success result
        setResult(RESULT_OK);
        finish();
    }
}