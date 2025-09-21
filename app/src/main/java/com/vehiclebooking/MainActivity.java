package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button bookVehicleButton;
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
}