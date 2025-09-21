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

public class AdminMainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private MaterialCardView adminDashboardCard;
    private MaterialCardView manageUsersCard;
    private MaterialCardView analyticsCard;
    private MaterialCardView settingsCard;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        userManager = UserManager.getInstance(this);
        
        // Check authentication and admin role
        if (!userManager.isLoggedIn() || !userManager.getCurrentUser().isAdmin()) {
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
            getSupportActionBar().setTitle("Vehicle Booking - Admin Panel");
        }
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.tv_welcome);
        adminDashboardCard = findViewById(R.id.card_admin_dashboard);
        manageUsersCard = findViewById(R.id.card_manage_users);
        analyticsCard = findViewById(R.id.card_analytics);
        settingsCard = findViewById(R.id.card_settings);
    }

    private void setupClickListeners() {
        adminDashboardCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, UnifiedAdminDashboardActivity.class);
                startActivity(intent);
            }
        });

        manageUsersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Create user management activity
                Toast.makeText(AdminMainActivity.this, "User management feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        analyticsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, BookingAnalyticsActivity.class);
                startActivity(intent);
            }
        });

        settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Create settings activity
                Toast.makeText(AdminMainActivity.this, "Settings feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWelcomeMessage() {
        User currentUser = userManager.getCurrentUser();
        String welcomeMessage = "Welcome, Administrator " + currentUser.getFullName() + "!";
        welcomeText.setText(welcomeMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            handleLogout();
            return true;
        } else if (id == R.id.action_system_info) {
            showSystemInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleLogout() {
        userManager.logout();
        Toast.makeText(this, "Admin logged out successfully", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void showSystemInfo() {
        // TODO: Show system information dialog
        Toast.makeText(this, "System info feature coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}