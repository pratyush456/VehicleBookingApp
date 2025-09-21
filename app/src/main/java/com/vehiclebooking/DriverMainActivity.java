package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;

public class DriverMainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private TextView statusText;
    private Switch availabilitySwitch;
    private MaterialCardView viewBookingsCard;
    private MaterialCardView myTripsCard;
    private MaterialCardView profileCard;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        userManager = UserManager.getInstance(this);
        
        // Check authentication and driver role
        if (!userManager.isLoggedIn() || !userManager.getCurrentUser().isDriver()) {
            redirectToLogin();
            return;
        }

        setupToolbar();
        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
        updateStatusDisplay();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Vehicle Booking - Driver");
        }
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.tv_welcome);
        statusText = findViewById(R.id.tv_status);
        availabilitySwitch = findViewById(R.id.switch_availability);
        viewBookingsCard = findViewById(R.id.card_view_bookings);
        myTripsCard = findViewById(R.id.card_my_trips);
        profileCard = findViewById(R.id.card_profile);
    }

    private void setupClickListeners() {
        availabilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateDriverAvailability(isChecked);
        });

        viewBookingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show available bookings for drivers to accept
                Intent intent = new Intent(DriverMainActivity.this, DriverBookingsActivity.class);
                startActivity(intent);
            }
        });

        myTripsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show driver's accepted/completed trips
                Toast.makeText(DriverMainActivity.this, "My trips feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Create driver profile activity
                Toast.makeText(DriverMainActivity.this, "Profile feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWelcomeMessage() {
        User currentUser = userManager.getCurrentUser();
        String welcomeMessage = "Welcome, Driver " + currentUser.getFullName() + "!";
        welcomeText.setText(welcomeMessage);
    }

    private void updateStatusDisplay() {
        User currentUser = userManager.getCurrentUser();
        boolean isAvailable = currentUser.isAvailable();
        
        availabilitySwitch.setChecked(isAvailable);
        statusText.setText(isAvailable ? "Status: Available for rides" : "Status: Offline");
        statusText.setTextColor(isAvailable ? 
            getResources().getColor(android.R.color.holo_green_dark) : 
            getResources().getColor(android.R.color.holo_red_dark));
    }

    private void updateDriverAvailability(boolean isAvailable) {
        User currentUser = userManager.getCurrentUser();
        currentUser.setAvailable(isAvailable);
        userManager.updateUser(currentUser);
        
        statusText.setText(isAvailable ? "Status: Available for rides" : "Status: Offline");
        statusText.setTextColor(isAvailable ? 
            getResources().getColor(android.R.color.holo_green_dark) : 
            getResources().getColor(android.R.color.holo_red_dark));
            
        Toast.makeText(this, isAvailable ? "You are now available for rides" : "You are now offline", 
                      Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            handleLogout();
            return true;
        } else if (id == R.id.action_earnings) {
            // TODO: Show driver earnings
            Toast.makeText(this, "Earnings feature coming soon!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleLogout() {
        // Set driver offline when logging out
        User currentUser = userManager.getCurrentUser();
        if (currentUser.isAvailable()) {
            currentUser.setAvailable(false);
            userManager.updateUser(currentUser);
        }
        
        userManager.logout();
        Toast.makeText(this, "Driver logged out successfully", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}