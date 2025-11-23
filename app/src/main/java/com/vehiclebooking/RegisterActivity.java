package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.vehiclebooking.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = UserManager.getInstance(this);
        setupRoleSpinner();
        setupClickListeners();
    }

    private void setupRoleSpinner() {
        // Only allow Passenger and Driver registration (Admin is system-only)
        UserRole[] roles = {UserRole.PASSENGER, UserRole.DRIVER};
        ArrayAdapter<UserRole> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_dropdown_item, roles);
        binding.spinnerRole.setAdapter(adapter);
        
        // Show/hide driver fields based on role selection
        binding.spinnerRole.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                UserRole selectedRole = (UserRole) parent.getSelectedItem();
                boolean isDriver = selectedRole == UserRole.DRIVER;
                binding.etLicenseNumber.setVisibility(isDriver ? View.VISIBLE : View.GONE);
                binding.etVehicleDetails.setVisibility(isDriver ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupClickListeners() {
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegistration();
            }
        });

        binding.tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to login
            }
        });
    }

    private void handleRegistration() {
        // Trim all inputs using utility methods
        String fullName = BookingStorage.trimAndValidate(
            binding.etFullName.getText().toString(), "Full Name");
        String username = BookingStorage.trimAndValidate(
            binding.etUsername.getText().toString(), "Username");
        String email = BookingStorage.trimAndValidate(
            binding.etEmail.getText().toString(), "Email");
        String phoneNumber = BookingStorage.trimAndValidate(
            binding.etPhoneNumber.getText().toString(), "Phone Number");
        String password = BookingStorage.trimAndValidate(
            binding.etPassword.getText().toString(), "Password");
        String confirmPassword = BookingStorage.trimAndValidate(
            binding.etConfirmPassword.getText().toString(), "Confirm Password");
        UserRole selectedRole = (UserRole) binding.spinnerRole.getSelectedItem();

        // Validation using utility methods
        if (!BookingStorage.isFieldValid(fullName, "Full Name")) {
            binding.etFullName.setError("Full name is required");
            return;
        }

        if (!BookingStorage.isFieldValid(username, "Username")) {
            binding.etUsername.setError("Username is required");
            return;
        }

        if (!BookingStorage.isFieldValid(email, "Email")) {
            binding.etEmail.setError("Email is required");
            return;
        }
        
        // Validate email format
        if (!BookingStorage.isValidEmail(email)) {
            binding.etEmail.setError("Please enter a valid email address");
            return;
        }

        if (!BookingStorage.isFieldValid(phoneNumber, "Phone Number")) {
            binding.etPhoneNumber.setError("Phone number is required");
            return;
        }
        
        // Validate phone number format
        if (!BookingStorage.isValidPhoneNumber(phoneNumber)) {
            binding.etPhoneNumber.setError("Please enter a valid phone number");
            return;
        }

        if (!BookingStorage.isFieldValid(password, "Password")) {
            binding.etPassword.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Driver-specific validation
        if (selectedRole == UserRole.DRIVER) {
            String licenseNumber = BookingStorage.trimAndValidate(
                binding.etLicenseNumber.getText().toString(), "License Number");
            String vehicleDetails = BookingStorage.trimAndValidate(
                binding.etVehicleDetails.getText().toString(), "Vehicle Details");

            if (!BookingStorage.isFieldValid(licenseNumber, "License Number")) {
                binding.etLicenseNumber.setError("License number is required for drivers");
                return;
            }

            if (!BookingStorage.isFieldValid(vehicleDetails, "Vehicle Details")) {
                binding.etVehicleDetails.setError("Vehicle details are required for drivers");
                return;
            }
        }

        // Attempt registration
        if (userManager.registerUser(username, email, phoneNumber, password, selectedRole, fullName)) {
            // If driver, update additional fields
            if (selectedRole == UserRole.DRIVER) {
                // We'll need to update the user with driver-specific details
                // For now, we'll handle this in a future update
            }

            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
            finish(); // Go back to login
        } else {
            Toast.makeText(this, "Registration failed. Username or email may already exist.", 
                         Toast.LENGTH_LONG).show();
        }
    }
}