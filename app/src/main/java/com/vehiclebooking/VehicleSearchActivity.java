package com.vehiclebooking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class VehicleSearchActivity extends AppCompatActivity {
    private EditText searchQuery;
    private EditText phoneNumber;
    private EditText customerName;
    private Button searchButton;
    private LinearLayout searchResultsContainer;
    private TextView locationText;
    
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    // Sample vehicle data - in real app this would come from database
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
        
        // Initialize ThreeTenABP for java.time backport (API < 26)
        AndroidThreeTen.init(this);
        
        setContentView(R.layout.activity_vehicle_search);
        
        initializeViews();
        setupLocationServices();
        setupSearchFunctionality();
        
        // Request location permission
        requestLocationPermission();
    }
    
    private void initializeViews() {
        searchQuery = findViewById(R.id.searchQuery);
        phoneNumber = findViewById(R.id.phoneNumber);
        customerName = findViewById(R.id.customerName);
        searchButton = findViewById(R.id.searchButton);
        searchResultsContainer = findViewById(R.id.searchResultsContainer);
        locationText = findViewById(R.id.locationText);
        
        // Set hints
        searchQuery.setHint("Search for vehicles (e.g., 'sedan', 'SUV', 'luxury')");
        phoneNumber.setHint("Your phone number");
        customerName.setHint("Your name (optional)");
    }
    
    private void setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }
    
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }
    
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            userLocation = location;
                            locationText.setText(String.format("📍 Your location: %.4f, %.4f", 
                                location.getLatitude(), location.getLongitude()));
                            locationText.setVisibility(View.VISIBLE);
                        } else {
                            locationText.setText("📍 Location not available");
                            locationText.setVisibility(View.VISIBLE);
                        }
                    }
                });
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
        
        // Validation
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
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
        
        Toast.makeText(this, "✅ Search saved! Admin will contact you soon.", Toast.LENGTH_LONG).show();
    }
    
    private boolean isValidPhoneNumber(String phone) {
        // Basic phone number validation - accepts numbers with optional formatting
        return phone.matches("^[+]?[0-9\\s\\-\\(\\)]{10,}$");
    }
    
    private void saveSearchData(String query, String phone, String name) {
        // Create search record for admin dashboard
        SearchRecord searchRecord = new SearchRecord();
        searchRecord.searchQuery = query;
        searchRecord.phoneNumber = phone;
        searchRecord.customerName = name.isEmpty() ? "Not provided" : name;
        searchRecord.timestamp = DateUtils.formatTimestamp(DateUtils.localDateTimeToTimestamp(DateUtils.now()));
        
        // Add location if available
        if (userLocation != null) {
            searchRecord.latitude = userLocation.getLatitude();
            searchRecord.longitude = userLocation.getLongitude();
            searchRecord.locationAvailable = true;
        } else {
            searchRecord.locationAvailable = false;
        }
        
        // Save to storage (using our existing storage system)
        SearchStorage.saveSearchRecord(this, searchRecord);
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
                Toast.makeText(VehicleSearchActivity.this, 
                    "✅ Interest in " + vehicle + " noted! Admin will contact you.", 
                    Toast.LENGTH_LONG).show();
                
                // Save specific vehicle interest
                saveVehicleInterest(vehicle);
            }
        });
        
        searchResultsContainer.addView(vehicleText);
    }
    
    private void saveVehicleInterest(String vehicle) {
        // Update the search record with specific vehicle interest
        String phone = phoneNumber.getText().toString().trim();
        SearchStorage.updateVehicleInterest(this, phone, vehicle);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                locationText.setText("📍 Location permission denied");
                locationText.setVisibility(View.VISIBLE);
            }
        }
    }
    
    // Inner class for search record structure
    public static class SearchRecord {
        public String searchQuery;
        public String phoneNumber;
        public String customerName;
        public String timestamp;
        public double latitude;
        public double longitude;
        public boolean locationAvailable;
        public String vehicleInterest = "";
        public String status = "New"; // New, Contacted, Completed
        public String adminNotes = "";
    }
}