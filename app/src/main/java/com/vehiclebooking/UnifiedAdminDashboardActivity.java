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
import androidx.core.content.ContextCompat;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class UnifiedAdminDashboardActivity extends AppCompatActivity {
    private LinearLayout recordsContainer;
    private TextView analyticsText;
    private Button refreshButton;
    private Button backButton;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unified_admin_dashboard);
        
        initializeViews();
        setupButtons();
        loadAllData();
    }

    private void initializeViews() {
        recordsContainer = findViewById(R.id.recordsContainer);
        analyticsText = findViewById(R.id.analyticsText);
        refreshButton = findViewById(R.id.refreshButton);
        backButton = findViewById(R.id.backButton);
        emptyStateText = findViewById(R.id.emptyStateText);
    }

    private void setupButtons() {
        refreshButton.setOnClickListener(v -> loadAllData());
        backButton.setOnClickListener(v -> finish());
    }

    private void loadAllData() {
        // Clear existing views
        recordsContainer.removeAllViews();
        
        // Get both bookings and vehicle searches
        List<BookingRequest> bookings = BookingStorage.getAllBookings(this);
        List<VehicleSearchActivity.SearchRecord> searches = SearchStorage.getSearchRecords(this);
        
        if (bookings.isEmpty() && searches.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            analyticsText.setText("📊 No data available");
        } else {
            emptyStateText.setVisibility(View.GONE);
            displayUnifiedAnalytics(bookings, searches);
            displayAllRecords(bookings, searches);
        }
    }

    private void displayUnifiedAnalytics(List<BookingRequest> bookings, List<VehicleSearchActivity.SearchRecord> searches) {
        StringBuilder analytics = new StringBuilder();
        
        // Booking Analytics
        int totalBookings = bookings.size();
        int pendingBookings = 0, confirmedBookings = 0, completedBookings = 0;
        
        for (BookingRequest booking : bookings) {
            if (booking.getStatus() == BookingStatus.PENDING) pendingBookings++;
            else if (booking.getStatus() == BookingStatus.CONFIRMED) confirmedBookings++;
            else if (booking.getStatus() == BookingStatus.COMPLETED) completedBookings++;
        }
        
        // Search Analytics
        SearchStorage.SearchAnalytics searchAnalytics = SearchStorage.getSearchAnalytics(this);
        
        analytics.append("📊 UNIFIED ADMIN DASHBOARD\n");
        analytics.append("══════════════════════════\n\n");
        
        analytics.append("📋 BOOKINGS OVERVIEW\n");
        analytics.append("═══════════════════\n");
        analytics.append("📋 Total Bookings: ").append(totalBookings).append("\n");
        analytics.append("🆕 Pending: ").append(pendingBookings).append("\n");
        analytics.append("✅ Confirmed: ").append(confirmedBookings).append("\n");
        analytics.append("🎯 Completed: ").append(completedBookings).append("\n\n");
        
        analytics.append("🔍 VEHICLE SEARCH LEADS\n");
        analytics.append("══════════════════════\n");
        analytics.append("🔍 Total Searches: ").append(searchAnalytics.totalSearches).append("\n");
        analytics.append("🆕 New Leads: ").append(searchAnalytics.newSearches).append("\n");
        analytics.append("📞 Contacted: ").append(searchAnalytics.contactedSearches).append("\n");
        analytics.append("✅ Completed: ").append(searchAnalytics.completedSearches).append("\n");
        analytics.append("📱 Lead Contact Rate: ").append(String.format("%.1f%%", searchAnalytics.getContactRate())).append("\n\n");
        
        analytics.append("🚗 POPULAR VEHICLES\n");
        analytics.append("══════════════════\n");
        analytics.append("🚙 Sedan: ").append(searchAnalytics.sedanSearches).append(" requests\n");
        analytics.append("🚐 SUV: ").append(searchAnalytics.suvSearches).append(" requests\n");
        analytics.append("🚚 Van: ").append(searchAnalytics.vanSearches).append(" requests\n");
        analytics.append("✨ Luxury: ").append(searchAnalytics.luxurySearches).append(" requests\n\n");
        
        int totalCustomerContacts = totalBookings + searchAnalytics.totalSearches;
        analytics.append("👥 TOTAL CUSTOMER CONTACTS: ").append(totalCustomerContacts).append("\n");
        
        analyticsText.setText(analytics.toString());
    }

    private void displayAllRecords(List<BookingRequest> bookings, List<VehicleSearchActivity.SearchRecord> searches) {
        // Add section header for bookings
        if (!bookings.isEmpty()) {
            addSectionHeader("📋 BOOKINGS WITH FULL DETAILS");
            displayBookings(bookings);
        }
        
        // Add section header for vehicle searches
        if (!searches.isEmpty()) {
            addSectionHeader("🔍 VEHICLE SEARCH LEADS");
            displaySearchRecords(searches);
        }
    }

    private void addSectionHeader(String title) {
        TextView headerText = new TextView(this);
        headerText.setText(title);
        headerText.setTextSize(16);
        headerText.setTypeface(null, android.graphics.Typeface.BOLD);
        headerText.setPadding(0, 20, 0, 10);
        headerText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        recordsContainer.addView(headerText);
    }

    private void displayBookings(List<BookingRequest> bookings) {
        // Sort by timestamp (newest first)
        Collections.sort(bookings, new Comparator<BookingRequest>() {
            @Override
            public int compare(BookingRequest b1, BookingRequest b2) {
                return Long.compare(b2.getTimestamp(), b1.getTimestamp());
            }
        });
        
        for (BookingRequest booking : bookings) {
            createBookingView(booking);
        }
    }

    private void displaySearchRecords(List<VehicleSearchActivity.SearchRecord> searches) {
        // Sort by timestamp (newest first)
        Collections.sort(searches, new Comparator<VehicleSearchActivity.SearchRecord>() {
            @Override
            public int compare(VehicleSearchActivity.SearchRecord r1, VehicleSearchActivity.SearchRecord r2) {
                return r2.timestamp.compareTo(r1.timestamp);
            }
        });
        
        for (VehicleSearchActivity.SearchRecord search : searches) {
            createSearchView(search);
        }
    }

    private void createBookingView(BookingRequest booking) {
        LinearLayout bookingContainer = new LinearLayout(this);
        bookingContainer.setOrientation(LinearLayout.VERTICAL);
        bookingContainer.setPadding(20, 15, 20, 15);
        bookingContainer.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 15);
        bookingContainer.setLayoutParams(params);
        
        TextView bookingInfo = new TextView(this);
        String bookingText = String.format(
            "📋 BOOKING: %s\n\n" +
            "📍 Route: %s → %s\n" +
            "📅 Date: %s\n" +
            "🚗 Vehicle: %s\n" +
            "📞 Phone: %s\n" +
            "📊 Status: %s",
            
            booking.getBookingId() != null ? booking.getBookingId() : "N/A",
            booking.getSource(),
            booking.getDestination(),
            booking.getFormattedTravelDate(),
            booking.getVehicleType() != null ? booking.getVehicleType() : "Not specified",
            booking.getPhoneNumber() != null ? booking.getPhoneNumber() : "Not provided",
            booking.getStatusDisplayText()
        );
        
        bookingInfo.setText(bookingText);
        bookingInfo.setTextSize(12);
        bookingContainer.addView(bookingInfo);
        
        // Action buttons for bookings
        LinearLayout buttonsContainer = new LinearLayout(this);
        buttonsContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonsContainer.setPadding(0, 10, 0, 0);
        
        if (booking.getPhoneNumber() != null && !booking.getPhoneNumber().isEmpty()) {
            Button callButton = new Button(this);
            callButton.setText("📞 Call");
            callButton.setTextSize(11);
            callButton.setOnClickListener(v -> makePhoneCall(booking.getPhoneNumber()));
            buttonsContainer.addView(callButton);
            
            Button smsButton = new Button(this);
            smsButton.setText("💬 SMS");
            smsButton.setTextSize(11);
            smsButton.setOnClickListener(v -> sendBookingSMS(booking));
            buttonsContainer.addView(smsButton);
        }
        
        Button statusButton = new Button(this);
        statusButton.setText("📋 Status");
        statusButton.setTextSize(11);
        statusButton.setOnClickListener(v -> showBookingStatusDialog(booking));
        buttonsContainer.addView(statusButton);
        
        bookingContainer.addView(buttonsContainer);
        recordsContainer.addView(bookingContainer);
    }

    private void createSearchView(VehicleSearchActivity.SearchRecord search) {
        LinearLayout searchContainer = new LinearLayout(this);
        searchContainer.setOrientation(LinearLayout.VERTICAL);
        searchContainer.setPadding(20, 15, 20, 15);
        searchContainer.setBackgroundResource(android.R.drawable.editbox_background);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 15);
        searchContainer.setLayoutParams(params);
        
        TextView searchInfo = new TextView(this);
        String statusEmoji = getStatusEmoji(search.status);
        String locationInfo = search.locationAvailable ? 
            String.format("📍 %.4f, %.4f", search.latitude, search.longitude) : 
            "📍 Location not available";
        
        String searchText = String.format(
            "%s VEHICLE SEARCH LEAD\n\n" +
            "🔍 Looking for: \"%s\"\n" +
            "👤 Customer: %s\n" +
            "📞 Phone: %s\n" +
            "🕐 Time: %s\n" +
            "%s\n" +
            "📊 Status: %s",
            
            statusEmoji,
            search.searchQuery,
            search.customerName,
            search.phoneNumber,
            search.timestamp,
            locationInfo,
            search.status
        );
        
        if (!search.vehicleInterest.isEmpty()) {
            searchText += "\n🚗 Interested in: " + search.vehicleInterest;
        }
        
        if (!search.adminNotes.isEmpty()) {
            searchText += "\n📝 Notes: " + search.adminNotes;
        }
        
        searchInfo.setText(searchText);
        searchInfo.setTextSize(12);
        searchContainer.addView(searchInfo);
        
        // Action buttons for searches
        LinearLayout buttonsContainer = new LinearLayout(this);
        buttonsContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonsContainer.setPadding(0, 10, 0, 0);
        
        Button callButton = new Button(this);
        callButton.setText("📞 Call");
        callButton.setTextSize(11);
        callButton.setOnClickListener(v -> makePhoneCall(search.phoneNumber));
        buttonsContainer.addView(callButton);
        
        Button smsButton = new Button(this);
        smsButton.setText("💬 SMS");
        smsButton.setTextSize(11);
        smsButton.setOnClickListener(v -> sendSearchSMS(search));
        buttonsContainer.addView(smsButton);
        
        Button statusButton = new Button(this);
        statusButton.setText("📋 Status");
        statusButton.setTextSize(11);
        statusButton.setOnClickListener(v -> showSearchStatusDialog(search));
        buttonsContainer.addView(statusButton);
        
        Button notesButton = new Button(this);
        notesButton.setText("📝 Notes");
        notesButton.setTextSize(11);
        notesButton.setOnClickListener(v -> showNotesDialog(search));
        buttonsContainer.addView(notesButton);
        
        searchContainer.addView(buttonsContainer);
        recordsContainer.addView(searchContainer);
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

    private void sendBookingSMS(BookingRequest booking) {
        try {
            String message = String.format(
                "Hi! Your booking %s for %s to %s on %s is confirmed. " +
                "Vehicle: %s. Thank you for choosing our service!", 
                booking.getBookingId() != null ? booking.getBookingId() : "",
                booking.getSource(),
                booking.getDestination(),
                booking.getFormattedTravelDate(),
                booking.getVehicleType() != null ? booking.getVehicleType() : "vehicle"
            );
            
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + booking.getPhoneNumber()));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to send SMS", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSearchSMS(VehicleSearchActivity.SearchRecord search) {
        try {
            String message = String.format(
                "Hi %s! Thank you for searching for '%s'. " +
                "We have great options available. Let's discuss your requirements!", 
                search.customerName.equals("Not provided") ? "" : search.customerName,
                search.searchQuery
            );
            
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + search.phoneNumber));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to send SMS", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBookingStatusDialog(BookingRequest booking) {
        String[] statusOptions = {"Pending", "Confirmed", "In Progress", "Completed", "Cancelled"};
        BookingStatus[] statuses = {
            BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS, 
            BookingStatus.COMPLETED, BookingStatus.CANCELLED
        };
        
        new AlertDialog.Builder(this)
            .setTitle("Update Status for " + booking.getBookingId())
            .setItems(statusOptions, (dialog, which) -> {
                BookingStatus newStatus = statuses[which];
                booking.changeStatus(newStatus, "Status updated by admin");
                BookingStorage.updateBooking(this, booking);
                loadAllData();
                Toast.makeText(this, "Status updated to: " + newStatus.getDisplayName(), Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showSearchStatusDialog(VehicleSearchActivity.SearchRecord search) {
        String[] statusOptions = {"New", "Contacted", "Completed"};
        
        new AlertDialog.Builder(this)
            .setTitle("Update Status for " + search.customerName)
            .setItems(statusOptions, (dialog, which) -> {
                String newStatus = statusOptions[which];
                SearchStorage.updateSearchStatus(this, search.phoneNumber, search.timestamp, newStatus);
                loadAllData();
                Toast.makeText(this, "Status updated to: " + newStatus, Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showNotesDialog(VehicleSearchActivity.SearchRecord search) {
        android.widget.EditText noteInput = new android.widget.EditText(this);
        noteInput.setText(search.adminNotes);
        noteInput.setHint("Add notes about this customer...");
        
        new AlertDialog.Builder(this)
            .setTitle("Admin Notes for " + search.customerName)
            .setView(noteInput)
            .setPositiveButton("Save", (dialog, which) -> {
                String notes = noteInput.getText().toString().trim();
                SearchStorage.updateAdminNotes(this, search.phoneNumber, search.timestamp, notes);
                loadAllData();
                Toast.makeText(this, "Notes saved!", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllData();
    }
}