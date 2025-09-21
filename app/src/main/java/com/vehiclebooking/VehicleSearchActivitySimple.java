package com.vehiclebooking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VehicleSearchActivitySimple extends AppCompatActivity {
    private EditText searchQuery;
    private EditText phoneNumber;
    private EditText customerName;
    private Button searchButton;
    private LinearLayout searchResultsContainer;
    
    // Sample vehicle data
    private String[] availableVehicles = {
        "Toyota Camry - Sedan",
        "Honda CR-V - SUV", 
        "Ford Transit - Van",
        "BMW 3 Series - Luxury",
        "Nissan Altima - Sedan",
        "Chevrolet Tahoe - Large SUV",
        "Mercedes Sprinter - Luxury Van",
        "Hyundai Elantra - Compact"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_search);
        
        initializeViews();
        setupSearchFunctionality();
    }
    
    private void initializeViews() {
        searchQuery = findViewById(R.id.searchQuery);
        phoneNumber = findViewById(R.id.phoneNumber);
        customerName = findViewById(R.id.customerName);
        searchButton = findViewById(R.id.searchButton);
        searchResultsContainer = findViewById(R.id.searchResultsContainer);
        
        // Set hints
        searchQuery.setHint("What vehicle are you looking for? (e.g., 'sedan', 'SUV', 'luxury')");
        phoneNumber.setHint("Your phone number");
        customerName.setHint("Your full name (required)");
        
        // Hide location text since we're not using GPS
        TextView locationText = findViewById(R.id.locationText);
        if (locationText != null) {
            locationText.setVisibility(View.GONE);
        }
    }
    
    private void setupSearchFunctionality() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
    }
    
    private void performSearch() {
        String query = searchQuery.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String name = customerName.getText().toString().trim();
        
        // Validation - Name is now mandatory
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter what vehicle you are looking for", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Validate phone number format (basic validation)
        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Save search data for admin dashboard
        saveSearchData(query, phone, name);
        
        // Perform search and display results
        displaySearchResults(query);
        
        // Personalized thank you message with customer name
        String thankYouMessage = String.format(
            "✅ Thank you %s!\n" +
            "Your search for '%s' has been saved.\n" +
            "Our admin team will contact you soon at %s.",
            name, query, phone
        );
        Toast.makeText(this, thankYouMessage, Toast.LENGTH_LONG).show();
    }
    
    private boolean isValidPhoneNumber(String phone) {
        // Basic phone number validation
        return phone.matches("^[+]?[0-9\\s\\-\\(\\)]{10,}$");
    }
    
    private void saveSearchData(String query, String phone, String name) {
        try {
            // Create search record for admin dashboard
            VehicleSearchActivity.SearchRecord searchRecord = new VehicleSearchActivity.SearchRecord();
            searchRecord.searchQuery = query;
            searchRecord.phoneNumber = phone;
            searchRecord.customerName = name; // Name is now mandatory, so always provided
            searchRecord.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            
            // No location data in simple version
            searchRecord.locationAvailable = false;
            searchRecord.latitude = 0.0;
            searchRecord.longitude = 0.0;
            
            // Save to storage
            SearchStorage.saveSearchRecord(this, searchRecord);
        } catch (Exception e) {
            // If saving fails, still continue with search display
            Toast.makeText(this, "Note: Unable to save search data", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void displaySearchResults(String query) {
        searchResultsContainer.removeAllViews();
        
        // Add search results header
        TextView headerText = new TextView(this);
        headerText.setText("🔍 Search Results for: \"" + query + "\"");
        headerText.setTextSize(16);
        headerText.setPadding(0, 20, 0, 10);
        searchResultsContainer.addView(headerText);
        
        // Search through available vehicles
        boolean foundResults = false;
        String queryLower = query.toLowerCase();
        
        for (String vehicle : availableVehicles) {
            if (vehicle.toLowerCase().contains(queryLower)) {
                foundResults = true;
                addVehicleResult(vehicle);
            }
        }
        
        if (!foundResults) {
            TextView noResultsText = new TextView(this);
            noResultsText.setText("❌ No vehicles found matching \"" + query + "\"\n\n" +
                "🔔 Don't worry! Our admin has been notified of your search and will contact you with available options.");
            noResultsText.setPadding(0, 10, 0, 10);
            searchResultsContainer.addView(noResultsText);
        } else {
            TextView contactText = new TextView(this);
            contactText.setText("\n📞 Our admin will contact you shortly to help with booking!");
            contactText.setPadding(0, 20, 0, 0);
            searchResultsContainer.addView(contactText);
        }
    }
    
    private void addVehicleResult(String vehicle) {
        TextView vehicleText = new TextView(this);
        vehicleText.setText("🚗 " + vehicle);
        vehicleText.setTextSize(14);
        vehicleText.setPadding(0, 8, 0, 8);
        vehicleText.setBackgroundResource(android.R.drawable.list_selector_background);
        vehicleText.setClickable(true);
        
        vehicleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VehicleSearchActivitySimple.this, 
                    "✅ Interest in " + vehicle + " noted! Admin will contact you.", 
                    Toast.LENGTH_LONG).show();
                
                // Save specific vehicle interest
                saveVehicleInterest(vehicle);
            }
        });
        
        searchResultsContainer.addView(vehicleText);
    }
    
    private void saveVehicleInterest(String vehicle) {
        try {
            // Update the search record with specific vehicle interest
            String phone = phoneNumber.getText().toString().trim();
            SearchStorage.updateVehicleInterest(this, phone, vehicle);
        } catch (Exception e) {
            // Ignore if update fails
        }
    }
}