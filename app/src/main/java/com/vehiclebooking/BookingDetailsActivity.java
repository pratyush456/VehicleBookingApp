package com.vehiclebooking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.threetenabp.AndroidThreeTen;
import org.threeten.bp.LocalDate;

public class BookingDetailsActivity extends AppCompatActivity {
    
    public static final String EXTRA_BOOKING_TIMESTAMP = "booking_timestamp";
    
    private BookingRequest booking;
    private StatusHistoryAdapter statusHistoryAdapter;
    
    // UI Components
    private TextView bookingIdHeader;
    private TextView bookingStatusHeader;
    private TextView sourceDetails;
    private TextView destinationDetails;
    private TextView travelDateDetails;
    private TextView bookingTimeDetails;
    private RecyclerView statusHistoryRecycler;
    private Button shareButton;
    private Button exportButton;
    private Button setReminderButton;
    private Button updateStatusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize ThreeTenABP for java.time backport (API < 26)
        AndroidThreeTen.init(this);
        
        setContentView(R.layout.activity_booking_details);
        
        initializeViews();
        loadBookingData();
        setupClickListeners();
    }
    
    private void initializeViews() {
        bookingIdHeader = findViewById(R.id.tv_booking_id_header);
        bookingStatusHeader = findViewById(R.id.tv_booking_status_header);
        sourceDetails = findViewById(R.id.tv_source_details);
        destinationDetails = findViewById(R.id.tv_destination_details);
        travelDateDetails = findViewById(R.id.tv_travel_date_details);
        bookingTimeDetails = findViewById(R.id.tv_booking_time_details);
        statusHistoryRecycler = findViewById(R.id.recycler_status_history);
        shareButton = findViewById(R.id.btn_share_booking);
        exportButton = findViewById(R.id.btn_export_booking);
        setReminderButton = findViewById(R.id.btn_set_reminder);
        updateStatusButton = findViewById(R.id.btn_update_status);
        
        // Setup status history RecyclerView
        statusHistoryRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void loadBookingData() {
        // Get booking timestamp from intent
        long bookingTimestamp = getIntent().getLongExtra(EXTRA_BOOKING_TIMESTAMP, 0);
        
        if (bookingTimestamp == 0) {
            Toast.makeText(this, "Invalid booking data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Find the booking from storage
        booking = findBookingByTimestamp(bookingTimestamp);
        
        if (booking == null) {
            Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        populateUI();
    }
    
    private BookingRequest findBookingByTimestamp(long timestamp) {
        for (BookingRequest b : BookingStorage.getAllBookings(this)) {
            if (b.getTimestamp() == timestamp) {
                return b;
            }
        }
        return null;
    }
    
    private void populateUI() {
        // Generate booking ID
        String bookingId = "BK" + String.valueOf(booking.getTimestamp()).substring(8);
        bookingIdHeader.setText("Booking #" + bookingId);
        
        // Set status
        BookingStatus status = booking.getStatus();
        if (status == null) {
            status = BookingStatus.PENDING;
        }
        bookingStatusHeader.setText(status.getIcon() + " " + status.getDisplayName());
        
        // Set journey details
        sourceDetails.setText("From: " + booking.getSource());
        destinationDetails.setText("To: " + booking.getDestination());
        
        // Format travel date
        String formattedTravelDate = DateUtils.formatDate(booking.getTravelDate(), "dd MMMM yyyy");
        travelDateDetails.setText(formattedTravelDate);
        
        // Format booking time
        String formattedBookingTime = DateUtils.formatDateTime12Hour(DateUtils.timestampToLocalDateTime(booking.getTimestamp()));
        bookingTimeDetails.setText(formattedBookingTime);
        
        // Setup status history
        if (booking.getStatusHistory() != null && !booking.getStatusHistory().isEmpty()) {
            statusHistoryAdapter = new StatusHistoryAdapter(booking.getStatusHistory());
            statusHistoryRecycler.setAdapter(statusHistoryAdapter);
        }
        
        // Update button states based on current status
        updateButtonStates(status);
    }
    
    private void updateButtonStates(BookingStatus currentStatus) {
        // Disable update status button for terminal states
        if (currentStatus == BookingStatus.COMPLETED || currentStatus == BookingStatus.CANCELLED) {
            updateStatusButton.setEnabled(false);
            updateStatusButton.setAlpha(0.5f);
            updateStatusButton.setText("✓ Final Status");
        }
        
        // Disable reminder for past travel dates
        LocalDate today = DateUtils.today();
        if (booking.getTravelDate().isBefore(today)) {
            setReminderButton.setEnabled(false);
            setReminderButton.setAlpha(0.5f);
            setReminderButton.setText("⏰ Past Date");
        }
    }
    
    private void setupClickListeners() {
        shareButton.setOnClickListener(v -> shareBooking());
        exportButton.setOnClickListener(v -> exportBooking());
        setReminderButton.setOnClickListener(v -> setReminder());
        updateStatusButton.setOnClickListener(v -> updateStatus());
    }
    
    private void shareBooking() {
        String shareText = createBookingSummary();
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Vehicle Booking Details");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        
        startActivity(Intent.createChooser(shareIntent, "Share Booking Details"));
    }
    
    private void exportBooking() {
        // For now, we'll create a simple text export
        // In a full implementation, this could generate PDF
        String exportText = createDetailedBookingSummary();
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Booking Export - " + 
                           "BK" + String.valueOf(booking.getTimestamp()).substring(8));
        shareIntent.putExtra(Intent.EXTRA_TEXT, exportText);
        
        startActivity(Intent.createChooser(shareIntent, "Export Booking"));
        
        Toast.makeText(this, "Booking exported successfully!", Toast.LENGTH_SHORT).show();
    }
    
    private void setReminder() {
        // Calculate days until travel
        LocalDate today = DateUtils.today();
        long todayTimestamp = DateUtils.localDateToTimestamp(today);
        long travelTimestamp = DateUtils.localDateToTimestamp(booking.getTravelDate());
        long timeDiff = travelTimestamp - todayTimestamp;
        long daysUntilTravel = timeDiff / (24 * 60 * 60 * 1000);
        
        if (daysUntilTravel < 0) {
            Toast.makeText(this, "Cannot set reminder for past dates", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] reminderOptions = {
            "1 day before travel",
            "2 days before travel", 
            "1 week before travel",
            "On travel day morning"
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Reminder")
               .setMessage("When would you like to be reminded about this booking?")
               .setItems(reminderOptions, (dialog, which) -> {
                   String selectedOption = reminderOptions[which];
                   // In a full implementation, this would schedule actual notifications
                   Toast.makeText(this, "Reminder set: " + selectedOption, Toast.LENGTH_LONG).show();
                   
                   // Send a confirmation notification
                   NotificationHelper.sendReminderConfirmation(this, booking, selectedOption);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void updateStatus() {
        BookingStatus currentStatus = booking.getStatus();
        if (currentStatus == null) {
            currentStatus = BookingStatus.PENDING;
        }
        
        BookingStatus[] possibleStatuses = currentStatus.getNextPossibleStatuses();
        if (possibleStatuses.length == 0) {
            Toast.makeText(this, "No status changes available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] statusOptions = new String[possibleStatuses.length];
        for (int i = 0; i < possibleStatuses.length; i++) {
            BookingStatus status = possibleStatuses[i];
            statusOptions[i] = status.getIcon() + " " + status.getDisplayName();
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Booking Status")
               .setMessage("Current: " + currentStatus.getIcon() + " " + currentStatus.getDisplayName())
               .setItems(statusOptions, (dialog, which) -> {
                   BookingStatus newStatus = possibleStatuses[which];
                   updateBookingStatus(newStatus);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void updateBookingStatus(BookingStatus newStatus) {
        String reason = "Status updated from booking details";
        
        if (booking.changeStatus(newStatus, reason)) {
            BookingStorage.updateBooking(this, booking);
            
            Toast.makeText(this, "Status updated to " + newStatus.getDisplayName(), 
                         Toast.LENGTH_SHORT).show();
            
            NotificationHelper.sendStatusChangeNotification(this, booking, newStatus);
            
            // Refresh the UI
            populateUI();
        } else {
            Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
        }
    }
    
    private String createBookingSummary() {
        String bookingId = "BK" + String.valueOf(booking.getTimestamp()).substring(8);
        BookingStatus status = booking.getStatus();
        if (status == null) status = BookingStatus.PENDING;
        
        return "🚗 Vehicle Booking Details\n\n" +
               "Booking ID: " + bookingId + "\n" +
               "Status: " + status.getIcon() + " " + status.getDisplayName() + "\n" +
               "From: " + booking.getSource() + "\n" +
               "To: " + booking.getDestination() + "\n" +
               "Travel Date: " + booking.getFormattedTravelDate() + "\n\n" +
               "Booked via Vehicle Booking App";
    }
    
    private String createDetailedBookingSummary() {
        StringBuilder summary = new StringBuilder();
        String bookingId = "BK" + String.valueOf(booking.getTimestamp()).substring(8);
        BookingStatus status = booking.getStatus();
        if (status == null) status = BookingStatus.PENDING;
        
        summary.append("🚗 VEHICLE BOOKING EXPORT\n");
        summary.append("========================\n\n");
        summary.append("Booking ID: ").append(bookingId).append("\n");
        summary.append("Current Status: ").append(status.getIcon()).append(" ").append(status.getDisplayName()).append("\n\n");
        
        summary.append("JOURNEY DETAILS:\n");
        summary.append("From: ").append(booking.getSource()).append("\n");
        summary.append("To: ").append(booking.getDestination()).append("\n");
        summary.append("Travel Date: ").append(booking.getFormattedTravelDate()).append("\n");
        
        summary.append("Booked On: ").append(DateUtils.formatDateTime12Hour(DateUtils.timestampToLocalDateTime(booking.getTimestamp()))).append("\n\n");
        
        summary.append("STATUS HISTORY:\n");
        if (booking.getStatusHistory() != null) {
            for (StatusChange change : booking.getStatusHistory()) {
                summary.append("• ").append(change.getDescription()).append("\n");
            }
        }
        
        summary.append("\n------------------------\n");
        summary.append("Generated by Vehicle Booking App\n");
        summary.append("Export Date: ").append(DateUtils.formatDateTime12Hour(DateUtils.now()));
        
        return summary.toString();
    }
}