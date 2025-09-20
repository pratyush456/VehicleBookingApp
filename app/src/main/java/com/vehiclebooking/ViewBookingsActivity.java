package com.vehiclebooking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewBookingsActivity extends AppCompatActivity implements BookingAdapter.OnStatusChangeClickListener {

    private RecyclerView recyclerBookings;
    private LinearLayout emptyStateLayout;
    private TextView bookingCountText;
    private Button makeFirstBookingButton;
    private BookingAdapter bookingAdapter;
    private List<BookingRequest> bookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookings);

        initializeViews();
        setupRecyclerView();
        loadBookings();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh bookings when returning to this activity
        loadBookings();
    }

    private void initializeViews() {
        recyclerBookings = findViewById(R.id.recycler_bookings);
        emptyStateLayout = findViewById(R.id.layout_empty_state);
        bookingCountText = findViewById(R.id.tv_booking_count);
        makeFirstBookingButton = findViewById(R.id.btn_make_first_booking);
    }

    private void setupRecyclerView() {
        recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadBookings() {
        // Load bookings from storage
        bookingList = BookingStorage.getAllBookings(this);
        
        updateUI();
    }

    private void updateUI() {
        if (bookingList.isEmpty()) {
            // Show empty state
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerBookings.setVisibility(View.GONE);
            bookingCountText.setText("0 bookings");
        } else {
            // Show bookings list
            emptyStateLayout.setVisibility(View.GONE);
            recyclerBookings.setVisibility(View.VISIBLE);
            
            // Update booking count
            int count = bookingList.size();
            bookingCountText.setText(count + (count == 1 ? " booking" : " bookings"));
            
            // Setup or update adapter
            if (bookingAdapter == null) {
                bookingAdapter = new BookingAdapter(bookingList, this);
                recyclerBookings.setAdapter(bookingAdapter);
            } else {
                bookingAdapter.updateBookings(bookingList);
            }
        }
    }

    private void setupClickListeners() {
        makeFirstBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to booking activity
                Intent intent = new Intent(ViewBookingsActivity.this, BookingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStatusChangeClick(BookingRequest booking, int position) {
        showStatusChangeDialog(booking, position);
    }

    private void showStatusChangeDialog(BookingRequest booking, int position) {
        BookingStatus currentStatus = booking.getStatus();
        if (currentStatus == null) {
            currentStatus = BookingStatus.PENDING;
        }

        BookingStatus[] possibleStatuses = currentStatus.getNextPossibleStatuses();
        if (possibleStatuses.length == 0) {
            Toast.makeText(this, "No status changes available for " + currentStatus.getDisplayName() + " bookings", 
                         Toast.LENGTH_SHORT).show();
            return;
        }

        // Create options for the dialog
        String[] statusOptions = new String[possibleStatuses.length];
        for (int i = 0; i < possibleStatuses.length; i++) {
            BookingStatus status = possibleStatuses[i];
            statusOptions[i] = status.getIcon() + " " + status.getDisplayName() + 
                              "\n" + status.getDescription();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Booking Status")
               .setMessage("Current Status: " + currentStatus.getIcon() + " " + currentStatus.getDisplayName())
               .setItems(statusOptions, (dialog, which) -> {
                   BookingStatus newStatus = possibleStatuses[which];
                   updateBookingStatus(booking, newStatus, position);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void updateBookingStatus(BookingRequest booking, BookingStatus newStatus, int position) {
        String reason = "Status updated by admin"; // In a real app, you might ask for a reason
        
        if (booking.changeStatus(newStatus, reason)) {
            // Update the existing booking
            BookingStorage.updateBooking(this, booking);
            
            // Show success message
            String message = newStatus.getTransitionMessage(newStatus);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            
            // Send notification about status change
            NotificationHelper.sendStatusChangeNotification(this, booking, newStatus);
            
            // Refresh the UI
            loadBookings();
            
        } else {
            Toast.makeText(this, "Cannot change status from " + booking.getStatus().getDisplayName() + 
                         " to " + newStatus.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
    }
}
