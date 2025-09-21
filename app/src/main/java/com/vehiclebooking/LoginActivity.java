package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userManager = UserManager.getInstance(this);
        userManager.initializeDefaultUsers(); // Create default admin if needed

        // Check if user is already logged in
        if (userManager.isLoggedIn()) {
            redirectToAppropriateActivity();
            return;
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.et_username);
        passwordEditText = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);
        registerLink = findViewById(R.id.tv_register_link);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
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