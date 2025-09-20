package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.util.*;

/**
 * Analytics dashboard showing comprehensive booking statistics and insights
 */
public class BookingAnalyticsActivity extends AppCompatActivity {
    
    // UI Components
    private MaterialToolbar toolbar;
    private View emptyState;
    private View loadingIndicator;
    
    // Stats Card Components
    private TextView tvTotalBookings;
    private TextView tvActiveBookings;
    private TextView tvCompletedBookings;
    private TextView tvCompletionRate;
    private TextView tvThisWeek;
    private TextView tvThisMonth;
    
    // Status Chart Components
    private StatusPieChart pieChart;
    private TextView tvPendingCount;
    private TextView tvConfirmedCount;
    private TextView tvInProgressCount;
    private TextView tvCompletedCount;
    private TextView tvCancelledCount;
    
    // Routes Components
    private RecyclerView rvPopularRoutes;
    private View routesEmptyState;
    private RouteAnalyticsAdapter routeAdapter;
    
    // Trends Components
    private TextView tvPeakHour;
    private TextView tvBusiestDay;
    private TextView tvBusiestMonth;
    private WeeklyTrendChart weeklyChart;
    
    // Insights Components
    private TextView tvPrimaryInsight;
    private TextView tvTravelPattern;
    private TextView tvFavoriteDestination;
    private TextView tvPreferredDay;
    private RecyclerView rvRecommendations;
    private TextView tvNoRecommendations;
    private RecommendationsAdapter recommendationsAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_analytics);
        
        initializeViews();
        setupToolbar();
        setupRecyclerViews();
        loadAnalyticsData();
    }
    
    private void initializeViews() {
        // Toolbar and states
        toolbar = findViewById(R.id.toolbar);
        emptyState = findViewById(R.id.empty_state);
        loadingIndicator = findViewById(R.id.loading_indicator);
        
        // Stats card
        tvTotalBookings = findViewById(R.id.tv_total_bookings);
        tvActiveBookings = findViewById(R.id.tv_active_bookings);
        tvCompletedBookings = findViewById(R.id.tv_completed_bookings);
        tvCompletionRate = findViewById(R.id.tv_completion_rate);
        tvThisWeek = findViewById(R.id.tv_this_week);
        tvThisMonth = findViewById(R.id.tv_this_month);
        
        // Status chart
        pieChart = findViewById(R.id.pie_chart);
        tvPendingCount = findViewById(R.id.tv_pending_count);
        tvConfirmedCount = findViewById(R.id.tv_confirmed_count);
        tvInProgressCount = findViewById(R.id.tv_in_progress_count);
        tvCompletedCount = findViewById(R.id.tv_completed_count);
        tvCancelledCount = findViewById(R.id.tv_cancelled_count);
        
        // Routes
        rvPopularRoutes = findViewById(R.id.rv_popular_routes);
        routesEmptyState = findViewById(R.id.routes_empty_state);
        
        // Trends
        tvPeakHour = findViewById(R.id.tv_peak_hour);
        tvBusiestDay = findViewById(R.id.tv_busiest_day);
        tvBusiestMonth = findViewById(R.id.tv_busiest_month);
        weeklyChart = findViewById(R.id.weekly_chart);
        
        // Insights
        tvPrimaryInsight = findViewById(R.id.tv_primary_insight);
        tvTravelPattern = findViewById(R.id.tv_travel_pattern);
        tvFavoriteDestination = findViewById(R.id.tv_favorite_destination);
        tvPreferredDay = findViewById(R.id.tv_preferred_day);
        rvRecommendations = findViewById(R.id.rv_recommendations);
        tvNoRecommendations = findViewById(R.id.tv_no_recommendations);
        
        // Empty state button
        MaterialButton btnMakeBooking = findViewById(R.id.btn_make_booking);
        btnMakeBooking.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupRecyclerViews() {
        // Routes RecyclerView
        routeAdapter = new RouteAnalyticsAdapter();
        rvPopularRoutes.setLayoutManager(new LinearLayoutManager(this));
        rvPopularRoutes.setAdapter(routeAdapter);
        
        // Recommendations RecyclerView
        recommendationsAdapter = new RecommendationsAdapter();
        rvRecommendations.setLayoutManager(new LinearLayoutManager(this));
        rvRecommendations.setAdapter(recommendationsAdapter);
    }
    
    private void loadAnalyticsData() {
        showLoading(true);
        
        // Get booking data
        BookingStorage storage = new BookingStorage(this);
        List<BookingRequest> bookings = storage.getBookings();
        
        if (bookings.isEmpty()) {
            showEmptyState();
            return;
        }
        
        // Calculate analytics
        BookingAnalytics.BookingStats stats = BookingAnalytics.calculateBookingStats(bookings);
        List<BookingAnalytics.RouteStats> routes = BookingAnalytics.analyzeRoutePopularity(bookings);
        BookingAnalytics.BookingTrends trends = BookingAnalytics.analyzeBookingTrends(bookings);
        BookingAnalytics.BookingInsights insights = BookingAnalytics.generateInsights(bookings);
        
        // Populate UI
        populateStatsCard(stats);
        populateStatusChart(stats);
        populateRoutesCard(routes);
        populateTrendsCard(trends);
        populateInsightsCard(insights);
        
        showLoading(false);
    }
    
    private void populateStatsCard(BookingAnalytics.BookingStats stats) {
        tvTotalBookings.setText(String.valueOf(stats.totalBookings));
        tvActiveBookings.setText(String.valueOf(stats.activeBookings));
        tvCompletedBookings.setText(String.valueOf(stats.completedBookings));
        tvCompletionRate.setText(String.format(Locale.getDefault(), "%.1f%%", stats.completionRate));
        tvThisWeek.setText(String.valueOf(stats.thisWeekBookings));
        tvThisMonth.setText(String.valueOf(stats.thisMonthBookings));
    }
    
    private void populateStatusChart(BookingAnalytics.BookingStats stats) {
        // Prepare data for pie chart
        Map<BookingStatus, Integer> statusData = new HashMap<>();
        statusData.put(BookingStatus.PENDING, stats.pendingBookings);
        statusData.put(BookingStatus.CONFIRMED, stats.confirmedBookings);
        statusData.put(BookingStatus.IN_PROGRESS, stats.inProgressBookings);
        statusData.put(BookingStatus.COMPLETED, stats.completedBookings);
        statusData.put(BookingStatus.CANCELLED, stats.cancelledBookings);
        
        pieChart.setData(statusData);
        
        // Update legend counts
        tvPendingCount.setText(String.valueOf(stats.pendingBookings));
        tvConfirmedCount.setText(String.valueOf(stats.confirmedBookings));
        tvInProgressCount.setText(String.valueOf(stats.inProgressBookings));
        tvCompletedCount.setText(String.valueOf(stats.completedBookings));
        tvCancelledCount.setText(String.valueOf(stats.cancelledBookings));
    }
    
    private void populateRoutesCard(List<BookingAnalytics.RouteStats> routes) {
        if (routes.isEmpty()) {
            rvPopularRoutes.setVisibility(View.GONE);
            routesEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvPopularRoutes.setVisibility(View.VISIBLE);
            routesEmptyState.setVisibility(View.GONE);
            routeAdapter.updateRoutes(routes.subList(0, Math.min(5, routes.size()))); // Show top 5
        }
    }
    
    private void populateTrendsCard(BookingAnalytics.BookingTrends trends) {
        // Format peak hour
        String peakHourText = trends.peakBookingHour == 0 ? "Not Available" : 
            String.format(Locale.getDefault(), "%02d:00", trends.peakBookingHour);
        tvPeakHour.setText(peakHourText);
        
        tvBusiestDay.setText(trends.busiestDay.isEmpty() ? "Not Available" : trends.busiestDay);
        tvBusiestMonth.setText(trends.busiestMonth.isEmpty() ? "Not Available" : trends.busiestMonth);
        
        // Set weekly chart data
        weeklyChart.setData(trends.dailyBookings);
    }
    
    private void populateInsightsCard(BookingAnalytics.BookingInsights insights) {
        tvPrimaryInsight.setText(insights.primaryInsight);
        tvTravelPattern.setText(insights.travelPattern);
        tvFavoriteDestination.setText(insights.favoriteDestination.isEmpty() ? 
            "Not Available" : insights.favoriteDestination);
        tvPreferredDay.setText(insights.preferredBookingDay);
        
        // Update recommendations
        if (insights.recommendations.isEmpty()) {
            rvRecommendations.setVisibility(View.GONE);
            tvNoRecommendations.setVisibility(View.VISIBLE);
        } else {
            rvRecommendations.setVisibility(View.VISIBLE);
            tvNoRecommendations.setVisibility(View.GONE);
            recommendationsAdapter.updateRecommendations(insights.recommendations);
        }
    }
    
    private void showLoading(boolean show) {
        loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    private void showEmptyState() {
        showLoading(false);
        emptyState.setVisibility(View.VISIBLE);
        
        // Hide all cards
        findViewById(R.id.stats_card).setVisibility(View.GONE);
        findViewById(R.id.status_chart_card).setVisibility(View.GONE);
        findViewById(R.id.routes_card).setVisibility(View.GONE);
        findViewById(R.id.trends_card).setVisibility(View.GONE);
        findViewById(R.id.insights_card).setVisibility(View.GONE);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to activity
        loadAnalyticsData();
    }
}