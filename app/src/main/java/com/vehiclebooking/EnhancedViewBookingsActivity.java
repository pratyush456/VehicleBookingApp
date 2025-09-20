package com.vehiclebooking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EnhancedViewBookingsActivity extends AppCompatActivity implements BookingAdapter.OnStatusChangeClickListener {

    // UI Components
    private TextView bookingCountText;
    private EditText searchEditText;
    private TextView clearSearchButton;
    private RecyclerView recyclerBookings;
    private LinearLayout emptyStateLayout;
    private TextView emptyIcon, emptyTitle, emptyMessage;
    private Button makeFirstBookingButton;
    private TextView resultsCountText;
    private TextView clearFiltersButton;

    // Filter chips
    private TextView chipAll, chipPending, chipConfirmed, chipInProgress, chipCompleted, chipCancelled;
    
    // Sort buttons
    private TextView sortByDate, sortByStatus, sortByRoute;

    // Data
    private List<BookingRequest> allBookings;
    private List<BookingRequest> filteredBookings;
    private BookingAdapter bookingAdapter;
    private BookingFilter.FilterStats filterStats;

    // Filter state
    private String currentSearchQuery = "";
    private BookingStatus currentStatusFilter = null;
    private BookingFilter.SortType currentSort = BookingFilter.SortType.DATE_NEWEST_FIRST;
    private boolean isDateSortAscending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookings_enhanced);
        
        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadBookings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }

    private void initializeViews() {
        bookingCountText = findViewById(R.id.tv_booking_count);
        searchEditText = findViewById(R.id.et_search);
        clearSearchButton = findViewById(R.id.btn_clear_search);
        recyclerBookings = findViewById(R.id.recycler_bookings);
        emptyStateLayout = findViewById(R.id.layout_empty_state);
        emptyIcon = findViewById(R.id.tv_empty_icon);
        emptyTitle = findViewById(R.id.tv_empty_title);
        emptyMessage = findViewById(R.id.tv_empty_message);
        makeFirstBookingButton = findViewById(R.id.btn_make_first_booking);
        resultsCountText = findViewById(R.id.tv_results_count);
        clearFiltersButton = findViewById(R.id.btn_clear_filters);

        // Filter chips
        chipAll = findViewById(R.id.chip_all);
        chipPending = findViewById(R.id.chip_pending);
        chipConfirmed = findViewById(R.id.chip_confirmed);
        chipInProgress = findViewById(R.id.chip_in_progress);
        chipCompleted = findViewById(R.id.chip_completed);
        chipCancelled = findViewById(R.id.chip_cancelled);

        // Sort buttons
        sortByDate = findViewById(R.id.btn_sort_date);
        sortByStatus = findViewById(R.id.btn_sort_status);
        sortByRoute = findViewById(R.id.btn_sort_route);
    }

    private void setupRecyclerView() {
        recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                clearSearchButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        clearSearchButton.setOnClickListener(v -> {
            searchEditText.setText("");
            clearSearchButton.setVisibility(View.GONE);
        });

        // Filter chips
        chipAll.setOnClickListener(v -> setStatusFilter(null));
        chipPending.setOnClickListener(v -> setStatusFilter(BookingStatus.PENDING));
        chipConfirmed.setOnClickListener(v -> setStatusFilter(BookingStatus.CONFIRMED));
        chipInProgress.setOnClickListener(v -> setStatusFilter(BookingStatus.IN_PROGRESS));
        chipCompleted.setOnClickListener(v -> setStatusFilter(BookingStatus.COMPLETED));
        chipCancelled.setOnClickListener(v -> setStatusFilter(BookingStatus.CANCELLED));

        // Sort buttons
        sortByDate.setOnClickListener(v -> toggleDateSort());
        sortByStatus.setOnClickListener(v -> setSortType(BookingFilter.SortType.STATUS));
        sortByRoute.setOnClickListener(v -> setSortType(BookingFilter.SortType.ROUTE_ALPHABETICAL));

        // Clear filters
        clearFiltersButton.setOnClickListener(v -> clearAllFilters());

        // Make first booking
        makeFirstBookingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            startActivity(intent);
        });
    }

    private void loadBookings() {
        allBookings = BookingStorage.getAllBookings(this);
        filterStats = BookingFilter.getFilterStats(allBookings);
        updateFilterChips();
        applyFilters();
    }

    private void applyFilters() {
        // Apply search and status filters
        filteredBookings = BookingFilter.filterBookings(allBookings, currentSearchQuery, currentStatusFilter);
        
        // Apply sorting
        filteredBookings = BookingFilter.sortBookings(filteredBookings, currentSort);

        updateUI();
    }

    private void updateUI() {
        // Update booking count
        int totalCount = allBookings.size();
        bookingCountText.setText(totalCount + (totalCount == 1 ? " booking" : " bookings"));

        // Update results count
        int filteredCount = filteredBookings.size();
        if (hasActiveFilters()) {
            resultsCountText.setText("Showing " + filteredCount + " of " + totalCount + " bookings");
            clearFiltersButton.setVisibility(View.VISIBLE);
        } else {
            resultsCountText.setText("Showing all bookings");
            clearFiltersButton.setVisibility(View.GONE);
        }

        // Update empty state
        if (filteredBookings.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            updateRecyclerView();
        }
    }

    private void showEmptyState() {
        emptyStateLayout.setVisibility(View.VISIBLE);
        recyclerBookings.setVisibility(View.GONE);

        if (allBookings.isEmpty()) {
            // No bookings at all
            emptyIcon.setText("📋");
            emptyTitle.setText("No Bookings Yet");
            emptyMessage.setText("Your booking history will appear here");
            makeFirstBookingButton.setVisibility(View.VISIBLE);
        } else if (hasActiveFilters()) {
            // No results for current filters
            emptyIcon.setText("🔍");
            emptyTitle.setText("No Results Found");
            emptyMessage.setText("Try adjusting your search or filters");
            makeFirstBookingButton.setVisibility(View.GONE);
        } else {
            // This shouldn't happen, but just in case
            emptyIcon.setText("📋");
            emptyTitle.setText("No Bookings");
            emptyMessage.setText("Something went wrong");
            makeFirstBookingButton.setVisibility(View.VISIBLE);
        }
    }

    private void hideEmptyState() {
        emptyStateLayout.setVisibility(View.GONE);
        recyclerBookings.setVisibility(View.VISIBLE);
    }

    private void updateRecyclerView() {
        if (bookingAdapter == null) {
            bookingAdapter = new BookingAdapter(filteredBookings, this);
            recyclerBookings.setAdapter(bookingAdapter);
        } else {
            bookingAdapter.updateBookings(filteredBookings);
        }
    }

    private void setStatusFilter(BookingStatus status) {
        currentStatusFilter = status;
        updateFilterChipSelection();
        applyFilters();
    }

    private void updateFilterChipSelection() {
        // Reset all chips
        resetChip(chipAll);
        resetChip(chipPending);
        resetChip(chipConfirmed);
        resetChip(chipInProgress);
        resetChip(chipCompleted);
        resetChip(chipCancelled);

        // Highlight selected chip
        if (currentStatusFilter == null) {
            selectChip(chipAll);
        } else {
            switch (currentStatusFilter) {
                case PENDING: selectChip(chipPending); break;
                case CONFIRMED: selectChip(chipConfirmed); break;
                case IN_PROGRESS: selectChip(chipInProgress); break;
                case COMPLETED: selectChip(chipCompleted); break;
                case CANCELLED: selectChip(chipCancelled); break;
            }
        }
    }

    private void updateFilterChips() {
        // Update chip counts
        if (filterStats != null) {
            chipPending.setText("⏳ Pending (" + filterStats.pendingCount + ")");
            chipConfirmed.setText("✅ Confirmed (" + filterStats.confirmedCount + ")");
            chipInProgress.setText("🚗 In Progress (" + filterStats.inProgressCount + ")");
            chipCompleted.setText("🏁 Completed (" + filterStats.completedCount + ")");
            chipCancelled.setText("❌ Cancelled (" + filterStats.cancelledCount + ")");
        }
    }

    private void toggleDateSort() {
        if (currentSort == BookingFilter.SortType.DATE_NEWEST_FIRST) {
            setSortType(BookingFilter.SortType.DATE_OLDEST_FIRST);
            sortByDate.setText("📅 Date ↑");
        } else {
            setSortType(BookingFilter.SortType.DATE_NEWEST_FIRST);
            sortByDate.setText("📅 Date ↓");
        }
    }

    private void setSortType(BookingFilter.SortType sortType) {
        currentSort = sortType;
        updateSortButtonSelection();
        applyFilters();
    }

    private void updateSortButtonSelection() {
        // Reset all sort buttons
        resetSortButton(sortByDate);
        resetSortButton(sortByStatus);
        resetSortButton(sortByRoute);

        // Highlight selected sort button
        switch (currentSort) {
            case DATE_NEWEST_FIRST:
                selectSortButton(sortByDate);
                sortByDate.setText("📅 Date ↓");
                break;
            case DATE_OLDEST_FIRST:
                selectSortButton(sortByDate);
                sortByDate.setText("📅 Date ↑");
                break;
            case STATUS:
                selectSortButton(sortByStatus);
                break;
            case ROUTE_ALPHABETICAL:
                selectSortButton(sortByRoute);
                break;
        }
    }

    private void selectChip(TextView chip) {
        chip.setBackground(ContextCompat.getDrawable(this, R.drawable.chip_selected));
        chip.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
    }

    private void resetChip(TextView chip) {
        chip.setBackground(ContextCompat.getDrawable(this, R.drawable.chip_unselected));
        chip.setTextColor(ContextCompat.getColor(this, android.R.color.white));
    }

    private void selectSortButton(TextView button) {
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.chip_selected));
        button.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
    }

    private void resetSortButton(TextView button) {
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.chip_unselected));
        button.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
    }

    private boolean hasActiveFilters() {
        return !currentSearchQuery.trim().isEmpty() || currentStatusFilter != null;
    }

    private void clearAllFilters() {
        currentSearchQuery = "";
        currentStatusFilter = null;
        searchEditText.setText("");
        updateFilterChipSelection();
        applyFilters();
    }

    @Override
    public void onStatusChangeClick(BookingRequest booking, int position) {
        showStatusChangeDialog(booking);
    }

    private void showStatusChangeDialog(BookingRequest booking) {
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
                   updateBookingStatus(booking, newStatus);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void updateBookingStatus(BookingRequest booking, BookingStatus newStatus) {
        String reason = "Status updated by admin";
        
        if (booking.changeStatus(newStatus, reason)) {
            BookingStorage.updateBooking(this, booking);
            
            String message = newStatus.getTransitionMessage(newStatus);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            
            NotificationHelper.sendStatusChangeNotification(this, booking, newStatus);
            
            // Refresh the data
            loadBookings();
            
        } else {
            Toast.makeText(this, "Cannot change status from " + booking.getStatus().getDisplayName() + 
                         " to " + newStatus.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
    }
}