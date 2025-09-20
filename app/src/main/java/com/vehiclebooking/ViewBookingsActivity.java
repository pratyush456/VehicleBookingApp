package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewBookingsActivity extends AppCompatActivity {

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
                bookingAdapter = new BookingAdapter(bookingList);
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
}