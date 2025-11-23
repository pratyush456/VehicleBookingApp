package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.vehiclebooking.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            android.util.Log.d("LoginActivity", "LoginActivity started");
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            android.util.Log.d("LoginActivity", "Layout set successfully");

            userManager = UserManager.getInstance(this);
            android.util.Log.d("LoginActivity", "UserManager initialized");
            
            userManager.initializeDefaultUsers(); // Create default admin if needed
            android.util.Log.d("LoginActivity", "Default users initialized");

            // Check if user is already logged in
            if (userManager.isLoggedIn()) {
                android.util.Log.d("LoginActivity", "User already logged in, redirecting");
                redirectToAppropriateActivity();
                return;
            }

            android.util.Log.d("LoginActivity", "Views initialized");
            
            setupClickListeners();
            android.util.Log.d("LoginActivity", "Click listeners set up");
            
        } catch (Exception e) {
            android.util.Log.e("LoginActivity", "Error in LoginActivity onCreate", e);
            android.widget.Toast.makeText(this, "Error initializing login: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        binding.tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        
        // Add real-time validation
        setupTextWatchers();
    }
    
    private void setupTextWatchers() {
        binding.etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = s.toString().trim();
                if (username.isEmpty()) {
                    binding.etUsername.setError("Username is required");
                } else {
                    binding.etUsername.setError(null);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString().trim();
                if (password.isEmpty()) {
                    binding.etPassword.setError("Password is required");
                } else {
                    binding.etPassword.setError(null);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleLogin() {
        String username = BookingStorage.trimAndValidate(
            binding.etUsername.getText().toString(), "Username");
        String password = BookingStorage.trimAndValidate(
            binding.etPassword.getText().toString(), "Password");

        // Validation using utility methods
        if (!BookingStorage.isFieldValid(username, "Username")) {
            binding.etUsername.setError("Username is required");
            return;
        }

        if (!BookingStorage.isFieldValid(password, "Password")) {
            binding.etPassword.setError("Password is required");
            return;
        }

        // Attempt login
        if (userManager.login(username, password)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            redirectToAppropriateActivity();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToAppropriateActivity() {
        User currentUser = userManager.getCurrentUser();
        Intent intent;

        switch (currentUser.getRole()) {
            case ADMIN:
                intent = new Intent(this, AdminMainActivity.class);
                break;
            case DRIVER:
                intent = new Intent(this, DriverMainActivity.class);
                break;
            case PASSENGER:
            default:
                intent = new Intent(this, PassengerMainActivity.class);
                break;
        }

        startActivity(intent);
        finish(); // Prevent going back to login
    }
}