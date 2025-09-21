package com.vehiclebooking;

import android.content.Intent;
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

public class CustomerBookingStatusActivity extends AppCompatActivity {
    private LinearLayout bookingsContainer;
    private TextView headerText;
    private Button backButton;
    private Button newBookingButton;
    private TextView emptyStateText;
    private String customerPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_booking_status);
        
        // Get phone number from intent (passed after booking)
        customerPhoneNumber = getIntent().getStringExtra("phone_number");
        
        initializeViews();
        setupButtons();
        loadCustomerBookings();
    }

    private void initializeViews() {
        bookingsContainer = findViewById(R.id.bookingsContainer);
        headerText = findViewById(R.id.headerText);
        backButton = findViewById(R.id.backButton);
        newBookingButton = findViewById(R.id.newBookingButton);
        emptyStateText = findViewById(R.id.emptyStateText);
    }

    private void setupButtons() {
        backButton.setOnClickListener(v -> finish());
        
        newBookingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadCustomerBookings() {
        bookingsContainer.removeAllViews();
        
        // Get all bookings and filter by phone number
        List<BookingRequest> allBookings = BookingStorage.getAllBookings(this);
        List<BookingRequest> customerBookings = new java.util.ArrayList<>();
        
        for (BookingRequest booking : allBookings) {
            if (booking.getPhoneNumber() != null && 
                booking.getPhoneNumber().equals(customerPhoneNumber)) {
                customerBookings.add(booking);
            }
        }
        
        if (customerBookings.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            displayCustomerBookings(customerBookings);
        }
    }

    private void showEmptyState() {
        emptyStateText.setVisibility(View.VISIBLE);
        bookingsContainer.setVisibility(View.GONE);
        headerText.setText("📋 Your Bookings");
    }

    private void hideEmptyState() {
        emptyStateText.setVisibility(View.GONE);
        bookingsContainer.setVisibility(View.VISIBLE);
        headerText.setText("📋 Your Bookings");
    }

    private void displayCustomerBookings(List<BookingRequest> bookings) {
        // Sort by timestamp (newest first)
        Collections.sort(bookings, new Comparator<BookingRequest>() {
            @Override
            public int compare(BookingRequest b1, BookingRequest b2) {
                return Long.compare(b2.getTimestamp(), b1.getTimestamp());
            }
        });
        
        for (BookingRequest booking : bookings) {
            createBookingStatusView(booking);
        }
    }

    private void createBookingStatusView(BookingRequest booking) {
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
        String statusColor = getStatusColor(booking.getStatus());
        
        String bookingText = String.format(
            "🆔 BOOKING ID: %s\n\n" +
            "📍 From: %s\n" +
            "📍 To: %s\n" +
            "📅 Date: %s\n" +
            "🚗 Vehicle: %s\n" +
            "📊 Status: %s\n" +
            "🕐 Booked: %s\n\n" +
            "📞 Contact: %s",
            
            booking.getBookingId() != null ? booking.getBookingId() : "N/A",
            booking.getSource(),
            booking.getDestination(),
            booking.getFormattedTravelDate(),
            booking.getVehicleType() != null ? booking.getVehicleType() : "Not specified",
            booking.getStatusDisplayText(),
            new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
                .format(new java.util.Date(booking.getTimestamp())),
            booking.getPhoneNumber()
        );
        
        bookingInfo.setText(bookingText);
        bookingInfo.setTextSize(13);
        bookingContainer.addView(bookingInfo);
        
        // Status-specific message
        TextView statusMessage = new TextView(this);
        statusMessage.setTextSize(12);
        statusMessage.setPadding(0, 10, 0, 0);
        
        String message = getStatusMessage(booking.getStatus());
        statusMessage.setText(message);
        statusMessage.setTextColor(getResources().getColor(getStatusColorResource(booking.getStatus())));
        bookingContainer.addView(statusMessage);
        
        // Action buttons
        LinearLayout buttonsContainer = new LinearLayout(this);
        buttonsContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonsContainer.setPadding(0, 15, 0, 0);
        
        // Only show modify/cancel if booking is pending or confirmed
        if (booking.getStatus() == BookingStatus.PENDING || 
            booking.getStatus() == BookingStatus.CONFIRMED) {
            
            Button modifyButton = new Button(this);
            modifyButton.setText("✏️ Modify");
            modifyButton.setTextSize(12);
            modifyButton.setOnClickListener(v -> modifyBooking(booking));
            buttonsContainer.addView(modifyButton);
            
            Button cancelButton = new Button(this);
            cancelButton.setText("❌ Cancel");
            cancelButton.setTextSize(12);
            cancelButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            cancelButton.setOnClickListener(v -> cancelBooking(booking));
            buttonsContainer.addView(cancelButton);
        }
        
        // View details button (always available)
        Button detailsButton = new Button(this);
        detailsButton.setText("👁️ Details");
        detailsButton.setTextSize(12);
        detailsButton.setOnClickListener(v -> showBookingDetails(booking));
        buttonsContainer.addView(detailsButton);
        
        bookingContainer.addView(buttonsContainer);
        bookingsContainer.addView(bookingContainer);
    }

    private String getStatusColor(BookingStatus status) {
        switch (status) {
            case PENDING: return "🟡";
            case CONFIRMED: return "🟢";
            case IN_PROGRESS: return "🔵";
            case COMPLETED: return "✅";
            case CANCELLED: return "🔴";
            default: return "⚪";
        }
    }

    private int getStatusColorResource(BookingStatus status) {
        switch (status) {
            case PENDING: return android.R.color.holo_orange_dark;
            case CONFIRMED: return android.R.color.holo_green_dark;
            case IN_PROGRESS: return android.R.color.holo_blue_dark;
            case COMPLETED: return android.R.color.holo_green_dark;
            case CANCELLED: return android.R.color.holo_red_dark;
            default: return android.R.color.darker_gray;
        }
    }

    private String getStatusMessage(BookingStatus status) {
        switch (status) {
            case PENDING:
                return "⏳ Your booking is being processed. We'll confirm shortly!";
            case CONFIRMED:
                return "✅ Great! Your booking is confirmed. We'll contact you before your trip.";
            case IN_PROGRESS:
                return "🚗 Your trip is in progress. Enjoy your ride!";
            case COMPLETED:
                return "🎉 Trip completed! Thank you for choosing our service.";
            case CANCELLED:
                return "❌ This booking has been cancelled.";
            default:
                return "ℹ️ Booking status pending...";
        }
    }

    private void modifyBooking(BookingRequest booking) {
        try {
            Toast.makeText(this, "Opening modify page for booking: " + booking.getBookingId(), Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(this, ModifyBookingActivity.class);
            intent.putExtra("booking_id", booking.getBookingId());
            intent.putExtra("phone_number", booking.getPhoneNumber());
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening modify page: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void cancelBooking(BookingRequest booking) {
        new AlertDialog.Builder(this)
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel booking " + booking.getBookingId() + "?")
            .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                // Update booking status to cancelled
                booking.changeStatus(BookingStatus.CANCELLED, "Cancelled by customer");
                BookingStorage.updateBooking(this, booking);
                
                Toast.makeText(this, "✅ Booking cancelled successfully", Toast.LENGTH_LONG).show();
                loadCustomerBookings(); // Refresh the view
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void showBookingDetails(BookingRequest booking) {
        StringBuilder details = new StringBuilder();
        details.append("📋 COMPLETE BOOKING DETAILS\n");
        details.append("═══════════════════════════\n\n");
        details.append("🆔 Booking ID: ").append(booking.getBookingId()).append("\n");
        details.append("📍 From: ").append(booking.getSource()).append("\n");
        details.append("📍 To: ").append(booking.getDestination()).append("\n");
        details.append("📅 Travel Date: ").append(booking.getFormattedTravelDate()).append("\n");
        details.append("🚗 Vehicle Type: ").append(booking.getVehicleType() != null ? booking.getVehicleType() : "Not specified").append("\n");
        details.append("📞 Phone: ").append(booking.getPhoneNumber()).append("\n");
        details.append("📊 Current Status: ").append(booking.getStatusDisplayText()).append("\n");
        details.append("🕐 Booking Time: ").append(
            new java.text.SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' HH:mm", java.util.Locale.getDefault())
                .format(new java.util.Date(booking.getTimestamp()))
        ).append("\n\n");
        
        // Show status history if available
        if (booking.getStatusHistory() != null && !booking.getStatusHistory().isEmpty()) {
            details.append("📊 STATUS HISTORY\n");
            details.append("════════════════\n");
            for (StatusChange change : booking.getStatusHistory()) {
                details.append("• ").append(change.getStatus().getDisplayName())
                    .append(" - ").append(change.getReason()).append("\n");
            }
        }
        
        new AlertDialog.Builder(this)
            .setTitle("Booking Details")
            .setMessage(details.toString())
            .setPositiveButton("OK", null)
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Always refresh when returning to this activity (including after modification)
        loadCustomerBookings();
    }

    // Static method to launch this activity with phone number
    public static void launch(android.content.Context context, String phoneNumber) {
        Intent intent = new Intent(context, CustomerBookingStatusActivity.class);
        intent.putExtra("phone_number", phoneNumber);
        context.startActivity(intent);
    }
}