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

public class AdminBookingViewActivity extends AppCompatActivity {
    private LinearLayout bookingsContainer;
    private TextView analyticsText;
    private Button refreshButton;
    private Button backButton;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_booking_view);
        
        initializeViews();
        setupButtons();
        loadBookingData();
    }

    private void initializeViews() {
        bookingsContainer = findViewById(R.id.bookingsContainer);
        analyticsText = findViewById(R.id.analyticsText);
        refreshButton = findViewById(R.id.refreshButton);
        backButton = findViewById(R.id.backButton);
        emptyStateText = findViewById(R.id.emptyStateText);
    }

    private void setupButtons() {
        refreshButton.setOnClickListener(v -> loadBookingData());
        backButton.setOnClickListener(v -> finish());
    }

    private void loadBookingData() {
        // Clear existing views
        bookingsContainer.removeAllViews();
        
        // Get all bookings
        List<BookingRequest> bookings = BookingStorage.getAllBookings(this);
        
        if (bookings.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            analyticsText.setText("📊 No booking data available");
        } else {
            emptyStateText.setVisibility(View.GONE);
            displayBookingAnalytics(bookings);
            displayBookings(bookings);
        }
    }

    private void displayBookingAnalytics(List<BookingRequest> bookings) {
        StringBuilder analytics = new StringBuilder();
        
        // Basic stats
        int totalBookings = bookings.size();
        int pendingBookings = 0;
        int confirmedBookings = 0;
        int completedBookings = 0;
        int cancelledBookings = 0;
        
        // Vehicle type stats
        int sedanRequests = 0;
        int suvRequests = 0;
        int vanRequests = 0;
        int luxuryRequests = 0;
        
        for (BookingRequest booking : bookings) {
            // Count by status
            if (booking.getStatus() == BookingStatus.PENDING) pendingBookings++;
            else if (booking.getStatus() == BookingStatus.CONFIRMED) confirmedBookings++;
            else if (booking.getStatus() == BookingStatus.COMPLETED) completedBookings++;
            else if (booking.getStatus() == BookingStatus.CANCELLED) cancelledBookings++;
            
            // Count by vehicle type
            String vehicleType = booking.getVehicleType();
            if (vehicleType != null) {
                String type = vehicleType.toLowerCase();
                if (type.contains("sedan")) sedanRequests++;
                else if (type.contains("suv")) suvRequests++;
                else if (type.contains("van")) vanRequests++;
                else if (type.contains("luxury")) luxuryRequests++;
            }
        }
        
        analytics.append("📊 BOOKING ANALYTICS\n");
        analytics.append("═══════════════════\n\n");
        analytics.append("📋 Total Bookings: ").append(totalBookings).append("\n");
        analytics.append("🆕 Pending: ").append(pendingBookings).append("\n");
        analytics.append("✅ Confirmed: ").append(confirmedBookings).append("\n");
        analytics.append("🎯 Completed: ").append(completedBookings).append("\n");
        analytics.append("❌ Cancelled: ").append(cancelledBookings).append("\n\n");
        
        analytics.append("🚗 POPULAR VEHICLES\n");
        analytics.append("═══════════════════\n");
        analytics.append("🚙 Sedan: ").append(sedanRequests).append(" requests\n");
        analytics.append("🚐 SUV: ").append(suvRequests).append(" requests\n");
        analytics.append("🚚 Van: ").append(vanRequests).append(" requests\n");
        analytics.append("✨ Luxury: ").append(luxuryRequests).append(" requests\n\n");
        
        analyticsText.setText(analytics.toString());
    }

    private void displayBookings(List<BookingRequest> bookings) {
        // Sort bookings by timestamp (newest first) - compatible with API 21+
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

    private void createBookingView(BookingRequest booking) {
        // Create container for this booking
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
        
        // Booking details
        TextView bookingInfo = new TextView(this);
        String bookingText = String.format(
            "🆔 BOOKING ID: %s\n\n" +
            "📍 Route: %s → %s\n" +
            "📅 Date: %s\n" +
            "🚗 Vehicle: %s\n" +
            "📞 Phone: %s\n" +
            "📊 Status: %s\n" +
            "🕐 Created: %s",
            
            booking.getBookingId() != null ? booking.getBookingId() : "N/A",
            booking.getSource(),
            booking.getDestination(),
            booking.getFormattedTravelDate(),
            booking.getVehicleType() != null ? booking.getVehicleType() : "Not specified",
            booking.getPhoneNumber() != null ? booking.getPhoneNumber() : "Not provided",
            booking.getStatusDisplayText(),
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                .format(new java.util.Date(booking.getTimestamp()))
        );
        
        bookingInfo.setText(bookingText);
        bookingInfo.setTextSize(12);
        bookingContainer.addView(bookingInfo);
        
        // Action buttons container
        LinearLayout buttonsContainer = new LinearLayout(this);
        buttonsContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonsContainer.setPadding(0, 10, 0, 0);
        
        // Call button (if phone number available)
        if (booking.getPhoneNumber() != null && !booking.getPhoneNumber().isEmpty()) {
            Button callButton = new Button(this);
            callButton.setText("📞 Call");
            callButton.setTextSize(12);
            callButton.setOnClickListener(v -> makePhoneCall(booking.getPhoneNumber()));
            buttonsContainer.addView(callButton);
            
            // SMS button
            Button smsButton = new Button(this);
            smsButton.setText("💬 SMS");
            smsButton.setTextSize(12);
            smsButton.setOnClickListener(v -> sendSMS(booking));
            buttonsContainer.addView(smsButton);
        }
        
        // Status button
        Button statusButton = new Button(this);
        statusButton.setText("📋 Status");
        statusButton.setTextSize(12);
        statusButton.setOnClickListener(v -> showStatusDialog(booking));
        buttonsContainer.addView(statusButton);
        
        bookingContainer.addView(buttonsContainer);
        
        // Add to main container
        bookingsContainer.addView(bookingContainer);
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

    private void sendSMS(BookingRequest booking) {
        try {
            String message = String.format(
                "Hi! Regarding your booking %s for %s to %s on %s. " +
                "Your %s vehicle booking is confirmed. Thank you!", 
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

    private void showStatusDialog(BookingRequest booking) {
        String[] statusOptions = {"Pending", "Confirmed", "In Progress", "Completed", "Cancelled"};
        BookingStatus[] statuses = {
            BookingStatus.PENDING, 
            BookingStatus.CONFIRMED, 
            BookingStatus.IN_PROGRESS, 
            BookingStatus.COMPLETED, 
            BookingStatus.CANCELLED
        };
        
        new AlertDialog.Builder(this)
            .setTitle("Update Status for Booking " + booking.getBookingId())
            .setItems(statusOptions, (dialog, which) -> {
                BookingStatus newStatus = statuses[which];
                booking.changeStatus(newStatus, "Status updated by admin");
                
                // Save updated booking
                BookingStorage.updateBooking(this, booking);
                
                loadBookingData(); // Refresh the display
                Toast.makeText(this, "Status updated to: " + newStatus.getDisplayName(), Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to the activity
        loadBookingData();
    }
}