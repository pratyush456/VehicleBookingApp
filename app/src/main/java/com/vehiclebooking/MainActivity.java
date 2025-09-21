package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button bookVehicleButton;
    private Button viewMyBookingsButton;
    private Button analyticsButton;
    private Button unifiedAdminButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        bookVehicleButton = findViewById(R.id.btn_book_vehicle);
        viewMyBookingsButton = findViewById(R.id.btn_view_my_bookings);
        analyticsButton = findViewById(R.id.btn_analytics);
        unifiedAdminButton = findViewById(R.id.btn_unified_admin);
    }

    private void setupClickListeners() {
        bookVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookingActivity.class);
                startActivity(intent);
            }
        });

        viewMyBookingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, ask for phone number to view bookings
                showPhoneNumberDialog();
            }
        });

        analyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookingAnalyticsActivitySimple.class);
                startActivity(intent);
            }
        });

        unifiedAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UnifiedAdminDashboardActivity.class);
                startActivity(intent);
            }
        });
    }
    
    private void showPhoneNumberDialog() {
        android.widget.EditText phoneInput = new android.widget.EditText(this);
        phoneInput.setHint("Enter your phone number");
        phoneInput.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("View Your Bookings")
            .setMessage("Please enter your phone number to view your bookings:")
            .setView(phoneInput)
            .setPositiveButton("View Bookings", (dialog, which) -> {
                String phoneNumber = phoneInput.getText().toString().trim();
                if (!phoneNumber.isEmpty()) {
                    CustomerBookingStatusActivity.launch(this, phoneNumber);
                } else {
                    android.widget.Toast.makeText(this, "Please enter a valid phone number", android.widget.Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
