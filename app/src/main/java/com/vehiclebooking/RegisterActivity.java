package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Spinner roleSpinner;
    private EditText licenseNumberEditText;
    private EditText vehicleDetailsEditText;
    private Button registerButton;
    private TextView loginLink;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userManager = UserManager.getInstance(this);
        initializeViews();
        setupRoleSpinner();
        setupClickListeners();
    }

    private void initializeViews() {
        fullNameEditText = findViewById(R.id.et_full_name);
        usernameEditText = findViewById(R.id.et_username);
        emailEditText = findViewById(R.id.et_email);
        phoneNumberEditText = findViewById(R.id.et_phone_number);
        passwordEditText = findViewById(R.id.et_password);
        confirmPasswordEditText = findViewById(R.id.et_confirm_password);
        roleSpinner = findViewById(R.id.spinner_role);
        licenseNumberEditText = findViewById(R.id.et_license_number);
        vehicleDetailsEditText = findViewById(R.id.et_vehicle_details);
        registerButton = findViewById(R.id.btn_register);
        loginLink = findViewById(R.id.tv_login_link);
    }

    private void setupRoleSpinner() {
        // Only allow Passenger and Driver registration (Admin is system-only)
        UserRole[] roles = {UserRole.PASSENGER, UserRole.DRIVER};
        ArrayAdapter<UserRole> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_dropdown_item, roles);
        roleSpinner.setAdapter(adapter);
        
        // Show/hide driver fields based on role selection
        roleSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                UserRole selectedRole = (UserRole) parent.getSelectedItem();
                boolean isDriver = selectedRole == UserRole.DRIVER;
                licenseNumberEditText.setVisibility(isDriver ? View.VISIBLE : View.GONE);
                vehicleDetailsEditText.setVisibility(isDriver ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupClickListeners() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegistration();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to login
            }
        });
    }

    private void handleRegistration() {
        String fullName = fullNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        UserRole selectedRole = (UserRole) roleSpinner.getSelectedItem();

        // Validation
        if (fullName.isEmpty()) {
            fullNameEditText.setError("Full name is required");
            return;
        }

        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return;
        }

        if (phoneNumber.isEmpty()) {
            phoneNumberEditText.setError("Phone number is required");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        // Driver-specific validation
        if (selectedRole == UserRole.DRIVER) {
            String licenseNumber = licenseNumberEditText.getText().toString().trim();
            String vehicleDetails = vehicleDetailsEditText.getText().toString().trim();

            if (licenseNumber.isEmpty()) {
                licenseNumberEditText.setError("License number is required for drivers");
                return;
            }

            if (vehicleDetails.isEmpty()) {
                vehicleDetailsEditText.setError("Vehicle details are required for drivers");
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