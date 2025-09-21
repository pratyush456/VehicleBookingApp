package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;

public class PassengerMainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private MaterialCardView newBookingCard;
    private MaterialCardView myBookingsCard;
    private MaterialCardView profileCard;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_main);

        userManager = UserManager.getInstance(this);
        
        // Check authentication
        if (!userManager.isLoggedIn() || !userManager.getCurrentUser().isPassenger()) {
            redirectToLogin();
            return;
        }

        setupToolbar();
        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Vehicle Booking - Passenger");
        }
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.tv_welcome);
        newBookingCard = findViewById(R.id.card_new_booking);
        myBookingsCard = findViewById(R.id.card_my_bookings);
        profileCard = findViewById(R.id.card_profile);
    }

    private void setupClickListeners() {
        newBookingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PassengerMainActivity.this, BookingActivity.class);
                startActivity(intent);
            }
        });

        myBookingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User currentUser = userManager.getCurrentUser();
                Intent intent = new Intent(PassengerMainActivity.this, CustomerBookingStatusActivity.class);
                intent.putExtra("phone_number", currentUser.getPhoneNumber());
                startActivity(intent);
            }
        });

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Create profile activity
                Toast.makeText(PassengerMainActivity.this, "Profile feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWelcomeMessage() {
        User currentUser = userManager.getCurrentUser();
        String welcomeMessage = "Welcome back, " + currentUser.getFullName() + "!";
        welcomeText.setText(welcomeMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.passenger_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            handleLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleLogout() {
        userManager.logout();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}