package com.vehiclebooking;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.vehiclebooking.databinding.ActivityBookingBinding;

import org.threeten.bp.LocalDate;

public class BookingActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;
    
    private LocalDate selectedDate;
    private NotificationHelper notificationHelper;
    private boolean isSubmitting = false; // Prevent double submission
    
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize ThreeTenABP for java.time backport (API < 26)
        AndroidThreeTen.init(this);
        
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
        selectedDate = DateUtils.today();
        notificationHelper = new NotificationHelper(this);
        
        // Request location permission if needed (for future location features)
        requestLocationPermissionIfNeeded();
    }
    
    /**
     * Request location permission dynamically if not already granted
     */
    private void requestLocationPermissionIfNeeded() {
        // Check if we already have location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, nothing to do
            return;
        }
        
        // Check if we should show rationale
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, 
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show explanation to user
            Toast.makeText(this, 
                "Location permission helps us provide better booking services", 
                Toast.LENGTH_LONG).show();
        }
        
        // Request the permission
        ActivityCompat.requestPermissions(this,
                new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
    }
    
    /**
     * Handle permission request results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && 
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                // You can now use location services if needed
                // For now, we just acknowledge the permission
            } else {
                // Location permission denied
                // App will continue to work without location features
                Toast.makeText(this, 
                    "Location permission denied. App will work without location features.", 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupClickListeners() {
        binding.btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        binding.btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBookingSubmission();
            }
        });
        
        // Add real-time validation with TextWatcher
        setupTextWatchers();
    }
    
    private void setupTextWatchers() {
        // Source validation
        binding.etSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateSourceField();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // Destination validation
        binding.etDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateDestinationField();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // Phone number validation
        binding.etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhoneField();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // Vehicle type validation
        binding.etVehicleType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateVehicleTypeField();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void validateSourceField() {
        String source = binding.etSource.getText().toString().trim();
        if (source.isEmpty()) {
            binding.etSource.setError("Source location is required");
        } else {
            binding.etSource.setError(null);
        }
    }
    
    private void validateDestinationField() {
        String destination = binding.etDestination.getText().toString().trim();
        if (destination.isEmpty()) {
            binding.etDestination.setError("Destination location is required");
        } else {
            binding.etDestination.setError(null);
        }
    }
    
    private void validatePhoneField() {
        String phone = binding.etPhoneNumber.getText().toString().trim();
        if (phone.isEmpty()) {
            binding.etPhoneNumber.setError("Phone number is required");
        } else if (!BookingStorage.isValidPhoneNumber(phone)) {
            binding.etPhoneNumber.setError("Please enter a valid phone number");
        } else {
            binding.etPhoneNumber.setError(null);
        }
    }
    
    private void validateVehicleTypeField() {
        String vehicleType = binding.etVehicleType.getText().toString().trim();
        if (vehicleType.isEmpty()) {
            binding.etVehicleType.setError("Vehicle type is required");
        } else {
            binding.etVehicleType.setError(null);
        }
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
            today.getYear(),
            today.getMonthValue() - 1, // DatePicker uses 0-based months
            today.getDayOfMonth()
        );
        
        // Set minimum date to today
        long todayTimestamp = DateUtils.localDateToTimestamp(today);
        datePickerDialog.getDatePicker().setMinDate(todayTimestamp);
        datePickerDialog.show();
    }

    private void updateSelectedDateDisplay() {
        String formattedDate = DateUtils.formatDate(selectedDate);
        binding.tvSelectedDate.setText("Selected Date: " + formattedDate);
    }

    private void handleBookingSubmission() {
        // Prevent double submission
        if (isSubmitting) {
            Toast.makeText(this, "Booking already in progress...", Toast.LENGTH_SHORT).show();
            return;
        }
        
        isSubmitting = true;
        binding.btnBookNow.setEnabled(false); // Disable button during submission
        
        // Trim all inputs using utility method
        String source = BookingStorage.trimAndValidate(
            binding.etSource.getText().toString(), "Source");
        String destination = BookingStorage.trimAndValidate(
            binding.etDestination.getText().toString(), "Destination");
        String phoneNumber = BookingStorage.trimAndValidate(
            binding.etPhoneNumber.getText().toString(), "Phone Number");
        String vehicleType = BookingStorage.trimAndValidate(
            binding.etVehicleType.getText().toString(), "Vehicle Type");
        String selectedDate = binding.tvSelectedDate.getText().toString();

        // Validation using utility methods
        if (!BookingStorage.isFieldValid(source, "Source") || 
            !BookingStorage.isFieldValid(destination, "Destination") || 
            selectedDate.equals("No date selected") ||
            !BookingStorage.isFieldValid(phoneNumber, "Phone Number") || 
            !BookingStorage.isFieldValid(vehicleType, "Vehicle Type")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            resetSubmissionState();
            return;
        }
        
        // Validate date
        if (!DateUtils.isDateValid(selectedDate)) {
            Toast.makeText(this, "Please select a valid date (not in the past)", Toast.LENGTH_SHORT).show();
            resetSubmissionState();
            return;
        }
        
        // Validate phone number using utility method
        if (!BookingStorage.isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            resetSubmissionState();
            return;
        }

        // Create enhanced booking request with phone and vehicle type
        BookingRequest bookingRequest = new BookingRequest(source, destination, selectedDate);
        bookingRequest.setPhoneNumber(phoneNumber);
        bookingRequest.setVehicleType(vehicleType);
        
        // Generate unique booking ID using utility method
        String bookingId = BookingStorage.generateUniqueBookingId(this);
        bookingRequest.setBookingId(bookingId);
        
        // Save booking to storage
        BookingStorage.saveBooking(this, bookingRequest);
        
        // Send notification to vehicle owner (you)
        notificationHelper.sendBookingNotification(bookingRequest);
        
        // Show confirmation to user with booking ID
        Toast.makeText(this, "\u2705 Booking submitted successfully!\nBooking ID: " + bookingId + 
            "\nRedirecting to your booking status...", Toast.LENGTH_SHORT).show();
        
        // Clear form and reset submission state
        clearForm();
        resetSubmissionState();
        
        // Redirect to customer booking status
        CustomerBookingStatusActivity.launch(this, phoneNumber);
        finish();
    }

    private void clearForm() {
        binding.etSource.setText("");
        binding.etDestination.setText("");
        binding.etPhoneNumber.setText("");
        binding.etVehicleType.setText("");
        binding.tvSelectedDate.setText("No date selected");
        selectedDate = DateUtils.today();
    }
    
    private void resetSubmissionState() {
        isSubmitting = false;
        binding.btnBookNow.setEnabled(true);
    }
}
