package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btnBookVehicle;
    private Button btnViewMyBookings;
    private Button btnAnalytics;
    private Button btnUnifiedAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize ThreeTenABP for java.time backport (API < 26)
        AndroidThreeTen.init(this);
        
        try {
            Log.d(TAG, "MainActivity started");
            
            // Set content view first to prevent layout issues
            setContentView(R.layout.activity_main);
            
            Log.d(TAG, "Content view set successfully");
            
            // Initialize views and setup click listeners
            initializeViews();
            setupClickListeners();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in MainActivity onCreate", e);
            Toast.makeText(this, "Error starting app: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Fallback: redirect to login if there's an error
            redirectToLogin();
        }
    }
    
    private void initializeViews() {
        btnBookVehicle = findViewById(R.id.btn_book_vehicle);
        btnViewMyBookings = findViewById(R.id.btn_view_my_bookings);
        btnAnalytics = findViewById(R.id.btn_analytics);
        btnUnifiedAdmin = findViewById(R.id.btn_unified_admin);
    }
    
    private void setupClickListeners() {
        btnBookVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user is logged in, if not redirect to login
                UserManager userManager = UserManager.getInstance(MainActivity.this);
                if (userManager.isLoggedIn()) {
                    Intent intent = new Intent(MainActivity.this, BookingActivity.class);
                    startActivity(intent);
                } else {
                    redirectToLogin();
                }
            }
        });
        
        btnViewMyBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user is logged in, if not redirect to login
                UserManager userManager = UserManager.getInstance(MainActivity.this);
                if (userManager.isLoggedIn()) {
                    Intent intent = new Intent(MainActivity.this, ViewBookingsActivity.class);
                    startActivity(intent);
                } else {
                    redirectToLogin();
                }
            }
        });
        
        btnAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user is logged in, if not redirect to login
                UserManager userManager = UserManager.getInstance(MainActivity.this);
                if (userManager.isLoggedIn()) {
                    Intent intent = new Intent(MainActivity.this, BookingAnalyticsActivity.class);
                    startActivity(intent);
                } else {
                    redirectToLogin();
                }
            }
        });
        
        btnUnifiedAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user is logged in and is admin
                UserManager userManager = UserManager.getInstance(MainActivity.this);
                if (userManager.isLoggedIn() && userManager.getCurrentUser().isAdmin()) {
                    Intent intent = new Intent(MainActivity.this, UnifiedAdminDashboardActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please login as admin to access this feature", Toast.LENGTH_SHORT).show();
                    redirectToLogin();
                }
            }
        });
    }
    
    private void redirectToLogin() {
        try {
            Log.d(TAG, "Attempting to start LoginActivity");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity so user can't go back to it
            Log.d(TAG, "LoginActivity started successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error starting LoginActivity", e);
            Toast.makeText(this, "Error starting login: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
