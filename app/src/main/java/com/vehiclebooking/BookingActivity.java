package com.vehiclebooking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private EditText sourceEditText;
    private EditText destinationEditText;
    private TextView selectedDateText;
    private Button selectDateButton;
    private Button bookNowButton;
    private EditText phoneNumberEditText;
    private EditText vehicleTypeEditText;
    
    private Calendar selectedCalendar;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        initializeViews();
        setupClickListeners();
        selectedCalendar = Calendar.getInstance();
        notificationHelper = new NotificationHelper(this);
    }

    private void initializeViews() {
        sourceEditText = findViewById(R.id.et_source);
        destinationEditText = findViewById(R.id.et_destination);
        selectedDateText = findViewById(R.id.tv_selected_date);
        selectDateButton = findViewById(R.id.btn_select_date);
        bookNowButton = findViewById(R.id.btn_book_now);
        phoneNumberEditText = findViewById(R.id.et_phone_number);
        vehicleTypeEditText = findViewById(R.id.et_vehicle_type);
    }

    private void setupClickListeners() {
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        bookNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBookingSubmission();
            }
        });
    }

    private void showDatePicker() {
        Calendar currentCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    selectedCalendar.set(year, month, dayOfMonth);
                    updateSelectedDateDisplay();
                }
            },
            currentCalendar.get(Calendar.YEAR),
            currentCalendar.get(Calendar.MONTH),
            currentCalendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateSelectedDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedCalendar.getTime());
        selectedDateText.setText("Selected Date: " + formattedDate);
    }

    private void handleBookingSubmission() {
        String source = sourceEditText.getText().toString().trim();
        String destination = destinationEditText.getText().toString().trim();
        String selectedDate = selectedDateText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String vehicleType = vehicleTypeEditText.getText().toString().trim();

        // Validation
        if (source.isEmpty() || destination.isEmpty() || selectedDate.equals("No date selected") || 
            phoneNumber.isEmpty() || vehicleType.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Validate phone number
        if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create enhanced booking request with phone and vehicle type
        BookingRequest bookingRequest = new BookingRequest(source, destination, selectedCalendar.getTime());
        bookingRequest.setPhoneNumber(phoneNumber);
        bookingRequest.setVehicleType(vehicleType);
        
        // Generate unique booking ID
        String bookingId = "BK" + System.currentTimeMillis();
        bookingRequest.setBookingId(bookingId);
        
        // Save booking to storage
        BookingStorage storage = new BookingStorage(this);
        storage.addBooking(bookingRequest);
        
        // Send notification to vehicle owner (you)
        notificationHelper.sendBookingNotification(bookingRequest);
        
        // Show confirmation to user with booking ID
        Toast.makeText(this, "✅ Booking submitted successfully!\nBooking ID: " + bookingId + 
            "\nWe will contact you at " + phoneNumber, Toast.LENGTH_LONG).show();
        
        // Clear form
        clearForm();
    }

    private void clearForm() {
        sourceEditText.setText("");
        destinationEditText.setText("");
        phoneNumberEditText.setText("");
        vehicleTypeEditText.setText("");
        selectedDateText.setText("No date selected");
        selectedCalendar = Calendar.getInstance();
    }
    
    private boolean isValidPhoneNumber(String phone) {
        // Basic phone number validation - accepts numbers with optional formatting
        return phone.matches("^[+]?[0-9\\s\\-\\(\\)]{10,}$");
    }
}
