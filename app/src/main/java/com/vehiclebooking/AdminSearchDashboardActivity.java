package com.vehiclebooking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class AdminSearchDashboardActivity extends AppCompatActivity {
    private LinearLayout searchRecordsContainer;
    private TextView analyticsText;
    private Button refreshButton;
    private Button clearAllButton;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_search_dashboard);
        
        initializeViews();
        setupButtons();
        loadSearchRecords();
    }

    private void initializeViews() {
        searchRecordsContainer = findViewById(R.id.searchRecordsContainer);
        analyticsText = findViewById(R.id.analyticsText);
        refreshButton = findViewById(R.id.refreshButton);
        clearAllButton = findViewById(R.id.clearAllButton);
        emptyStateText = findViewById(R.id.emptyStateText);
    }

    private void setupButtons() {
        refreshButton.setOnClickListener(v -> loadSearchRecords());
        
        clearAllButton.setOnClickListener(v -> showClearAllDialog());
    }

    private void loadSearchRecords() {
        // Clear existing views
        searchRecordsContainer.removeAllViews();
        
        // Get search records from storage
        List<VehicleSearchActivity.SearchRecord> records = SearchStorage.getSearchRecords(this);
        
        if (records.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            analyticsText.setText("📊 No search data available");
        } else {
            emptyStateText.setVisibility(View.GONE);
            displayAnalytics(records);
            displaySearchRecords(records);
        }
    }

    private void displayAnalytics(List<VehicleSearchActivity.SearchRecord> records) {
        SearchStorage.SearchAnalytics analytics = SearchStorage.getSearchAnalytics(this);
        
        String analyticsInfo = String.format(
            "📊 SEARCH ANALYTICS\n\n" +
            "📈 Total Searches: %d\n" +
            "🆕 New: %d\n" +
            "📞 Contacted: %d\n" +
            "✅ Completed: %d\n\n" +
            "📍 With Location: %d\n" +
            "📱 Contact Rate: %.1f%%\n" +
            "🎯 Completion Rate: %.1f%%\n\n" +
            "🚗 Popular Vehicle Types:\n" +
            "   • Sedan: %d searches\n" +
            "   • SUV: %d searches\n" +
            "   • Van: %d searches\n" +
            "   • Luxury: %d searches\n" +
            "🏆 Most Popular: %s",
            
            analytics.totalSearches,
            analytics.newSearches,
            analytics.contactedSearches,
            analytics.completedSearches,
            analytics.searchesWithLocation,
            analytics.getContactRate(),
            analytics.getCompletionRate(),
            analytics.sedanSearches,
            analytics.suvSearches,
            analytics.vanSearches,
            analytics.luxurySearches,
            analytics.getMostPopularVehicleType()
        );
        
        analyticsText.setText(analyticsInfo);
    }

    private void displaySearchRecords(List<VehicleSearchActivity.SearchRecord> records) {
        // Sort records by timestamp (newest first) - compatible with API 21+
        Collections.sort(records, new Comparator<VehicleSearchActivity.SearchRecord>() {
            @Override
            public int compare(VehicleSearchActivity.SearchRecord r1, VehicleSearchActivity.SearchRecord r2) {
                return r2.timestamp.compareTo(r1.timestamp);
            }
        });
        
        for (VehicleSearchActivity.SearchRecord record : records) {
            createSearchRecordView(record);
        }
    }

    private void createSearchRecordView(VehicleSearchActivity.SearchRecord record) {
        // Create container for this record
        LinearLayout recordContainer = new LinearLayout(this);
        recordContainer.setOrientation(LinearLayout.VERTICAL);
        recordContainer.setPadding(20, 15, 20, 15);
        recordContainer.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 15);
        recordContainer.setLayoutParams(params);
        
        // Status indicator
        String statusEmoji = getStatusEmoji(record.status);
        
        // Main record info
        TextView recordInfo = new TextView(this);
        String locationInfo = record.locationAvailable ? 
            String.format("📍 %.4f, %.4f", record.latitude, record.longitude) : 
            "📍 Location not available";
        
        String recordText = String.format(
            "%s %s\n\n" +
            "🔍 Searched: \"%s\"\n" +
            "👤 Name: %s\n" +
            "📞 Phone: %s\n" +
            "🕐 Time: %s\n" +
            "%s\n" +
            "%s",
            
            statusEmoji, record.status.toUpperCase(),
            record.searchQuery,
            record.customerName,
            record.phoneNumber,
            record.timestamp,
            locationInfo,
            record.vehicleInterest.isEmpty() ? "" : "🚗 Interested in: " + record.vehicleInterest
        );
        
        if (!record.adminNotes.isEmpty()) {
            recordText += "\n📝 Admin Notes: " + record.adminNotes;
        }
        
        recordInfo.setText(recordText);
        recordInfo.setTextSize(12);
        recordContainer.addView(recordInfo);
        
        // Action buttons container
        LinearLayout buttonsContainer = new LinearLayout(this);
        buttonsContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonsContainer.setPadding(0, 10, 0, 0);
        
        // Call button
        Button callButton = new Button(this);
        callButton.setText("📞 Call");
        callButton.setOnClickListener(v -> makePhoneCall(record.phoneNumber));
        
        // SMS button
        Button smsButton = new Button(this);
        smsButton.setText("💬 SMS");
        smsButton.setOnClickListener(v -> sendSMS(record.phoneNumber, record.searchQuery));
        
        // Status button
        Button statusButton = new Button(this);
        statusButton.setText("📋 Status");
        statusButton.setOnClickListener(v -> showStatusDialog(record));
        
        // Notes button
        Button notesButton = new Button(this);
        notesButton.setText("📝 Notes");
        notesButton.setOnClickListener(v -> showNotesDialog(record));
        
        // Add buttons to container
        buttonsContainer.addView(callButton);
        buttonsContainer.addView(smsButton);
        buttonsContainer.addView(statusButton);
        buttonsContainer.addView(notesButton);
        
        recordContainer.addView(buttonsContainer);
        
        // Add location button if location is available
        if (record.locationAvailable) {
            Button locationButton = new Button(this);
            locationButton.setText("🗺️ View Location");
            locationButton.setOnClickListener(v -> openLocationInMaps(record.latitude, record.longitude));
            
            LinearLayout locationContainer = new LinearLayout(this);
            locationContainer.addView(locationButton);
            recordContainer.addView(locationContainer);
        }
        
        // Add to main container
        searchRecordsContainer.addView(recordContainer);
    }

    private String getStatusEmoji(String status) {
        switch (status) {
            case "New": return "🆕";
            case "Contacted": return "📞";
            case "Completed": return "✅";
            default: return "❓";
        }
    }

    private void makePhoneCall(String phoneNumber) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to make call", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSMS(String phoneNumber, String searchQuery) {
        try {
            String message = String.format(
                "Hi! Thank you for searching for '%s' on our vehicle booking app. " +
                "We have some great options available. How can we help you?", 
                searchQuery
            );
            
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + phoneNumber));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to send SMS", Toast.LENGTH_SHORT).show();
        }
    }

    private void openLocationInMaps(double latitude, double longitude) {
        try {
            String uri = String.format("geo:%f,%f?q=%f,%f(Customer Search Location)", 
                latitude, longitude, latitude, longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");
            
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback to web browser
                String webUrl = String.format("https://www.google.com/maps/@%f,%f,15z", 
                    latitude, longitude);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                startActivity(webIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open location", Toast.LENGTH_SHORT).show();
        }
    }

    private void showStatusDialog(VehicleSearchActivity.SearchRecord record) {
        String[] statusOptions = {"New", "Contacted", "Completed"};
        
        new AlertDialog.Builder(this)
            .setTitle("Update Status for " + record.customerName)
            .setItems(statusOptions, (dialog, which) -> {
                String newStatus = statusOptions[which];
                SearchStorage.updateSearchStatus(this, record.phoneNumber, record.timestamp, newStatus);
                loadSearchRecords(); // Refresh the display
                Toast.makeText(this, "Status updated to: " + newStatus, Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showNotesDialog(VehicleSearchActivity.SearchRecord record) {
        android.widget.EditText noteInput = new android.widget.EditText(this);
        noteInput.setText(record.adminNotes);
        noteInput.setHint("Add notes about this customer...");
        
        new AlertDialog.Builder(this)
            .setTitle("Admin Notes for " + record.customerName)
            .setView(noteInput)
            .setPositiveButton("Save", (dialog, which) -> {
                String notes = noteInput.getText().toString().trim();
                SearchStorage.updateAdminNotes(this, record.phoneNumber, record.timestamp, notes);
                loadSearchRecords(); // Refresh the display
                Toast.makeText(this, "Notes saved!", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showClearAllDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Clear All Search Records")
            .setMessage("Are you sure you want to delete all search records? This action cannot be undone.")
            .setPositiveButton("Yes, Clear All", (dialog, which) -> {
                SearchStorage.clearAllSearchRecords(this);
                loadSearchRecords();
                Toast.makeText(this, "All search records cleared", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to the activity
        loadSearchRecords();
    }
}