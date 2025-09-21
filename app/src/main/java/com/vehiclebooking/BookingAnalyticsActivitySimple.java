package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

/**
 * Simplified analytics dashboard that won't crash
 */
public class BookingAnalyticsActivitySimple extends AppCompatActivity {
    
    private TextView analyticsTextView;
    private Button makeBookingButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_analytics_simple);
        
        initializeViews();
        loadAnalyticsData();
    }
    
    private void initializeViews() {
        analyticsTextView = findViewById(R.id.analytics_text);
        makeBookingButton = findViewById(R.id.btn_make_booking);
        backButton = findViewById(R.id.btn_back);
        
        makeBookingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            startActivity(intent);
        });
        
        backButton.setOnClickListener(v -> finish());
    }
    
    private void loadAnalyticsData() {
        try {
            // Get booking data safely
            BookingStorage storage = new BookingStorage(this);
            List<BookingRequest> bookings = storage.getBookings();
            
            // Get search data safely  
            List<VehicleSearchActivity.SearchRecord> searches = SearchStorage.getSearchRecords(this);
            SearchStorage.SearchAnalytics searchAnalytics = SearchStorage.getSearchAnalytics(this);
            
            StringBuilder analyticsText = new StringBuilder();
            
            // Booking Analytics
            analyticsText.append("📊 BOOKING ANALYTICS\n");
            analyticsText.append("═══════════════════\n\n");
            
            if (bookings.isEmpty()) {
                analyticsText.append("📋 Total Bookings: 0\n");
                analyticsText.append("🎯 No booking data available yet\n");
                analyticsText.append("💡 Create your first booking to see analytics!\n\n");
            } else {
                int totalBookings = bookings.size();
                int completedBookings = 0;
                int cancelledBookings = 0;
                int pendingBookings = 0;
                
                // Count by status
                for (BookingRequest booking : bookings) {
                    try {
                        BookingStatus status = booking.getStatus();
                        if (status == BookingStatus.COMPLETED) {
                            completedBookings++;
                        } else if (status == BookingStatus.CANCELLED) {
                            cancelledBookings++;
                        } else {
                            pendingBookings++;
                        }
                    } catch (Exception e) {
                        pendingBookings++;
                    }
                }
                
                double completionRate = totalBookings > 0 ? (completedBookings * 100.0 / totalBookings) : 0;
                
                analyticsText.append("📋 Total Bookings: ").append(totalBookings).append("\n");
                analyticsText.append("✅ Completed: ").append(completedBookings).append("\n");
                analyticsText.append("⏳ Pending: ").append(pendingBookings).append("\n");
                analyticsText.append("❌ Cancelled: ").append(cancelledBookings).append("\n");
                analyticsText.append("🎯 Completion Rate: ").append(String.format("%.1f%%", completionRate)).append("\n\n");
            }
            
            // Search Analytics  
            analyticsText.append("🔍 SEARCH ANALYTICS\n");
            analyticsText.append("═══════════════════\n\n");
            
            if (searches.isEmpty()) {
                analyticsText.append("🔍 Total Searches: 0\n");
                analyticsText.append("🎯 No search data available yet\n");
                analyticsText.append("💡 Customers can search vehicles to generate leads!\n\n");
            } else {
                analyticsText.append("🔍 Total Searches: ").append(searchAnalytics.totalSearches).append("\n");
                analyticsText.append("🆕 New Leads: ").append(searchAnalytics.newSearches).append("\n");
                analyticsText.append("📞 Contacted: ").append(searchAnalytics.contactedSearches).append("\n");
                analyticsText.append("✅ Completed: ").append(searchAnalytics.completedSearches).append("\n");
                analyticsText.append("📱 Contact Rate: ").append(String.format("%.1f%%", searchAnalytics.getContactRate())).append("\n");
                analyticsText.append("🎯 Completion Rate: ").append(String.format("%.1f%%", searchAnalytics.getCompletionRate())).append("\n\n");
                
                analyticsText.append("🚗 POPULAR VEHICLES\n");
                analyticsText.append("═══════════════════\n");
                analyticsText.append("🚙 Sedan: ").append(searchAnalytics.sedanSearches).append(" searches\n");
                analyticsText.append("🚐 SUV: ").append(searchAnalytics.suvSearches).append(" searches\n");
                analyticsText.append("🚚 Van: ").append(searchAnalytics.vanSearches).append(" searches\n");
                analyticsText.append("✨ Luxury: ").append(searchAnalytics.luxurySearches).append(" searches\n");
                analyticsText.append("🏆 Most Popular: ").append(searchAnalytics.getMostPopularVehicleType()).append("\n\n");
                
                analyticsText.append("📍 LOCATION DATA\n");
                analyticsText.append("═══════════════════\n");
                analyticsText.append("📍 Searches with Location: ").append(searchAnalytics.searchesWithLocation).append("\n");
                analyticsText.append("🌍 Location Coverage: ").append(
                    String.format("%.1f%%", searchAnalytics.totalSearches > 0 ? 
                        (searchAnalytics.searchesWithLocation * 100.0 / searchAnalytics.totalSearches) : 0)
                ).append("\n\n");
            }
            
            // Recent Activity
            analyticsText.append("📅 RECENT ACTIVITY\n");
            analyticsText.append("═══════════════════\n");
            
            if (!searches.isEmpty()) {
                analyticsText.append("Recent Customer Searches:\n");
                int count = 0;
                for (int i = searches.size() - 1; i >= 0 && count < 3; i--, count++) {
                    VehicleSearchActivity.SearchRecord search = searches.get(i);
                    analyticsText.append("• ").append(search.searchQuery)
                        .append(" (").append(search.phoneNumber).append(")")
                        .append(" - ").append(search.status).append("\n");
                }
            } else {
                analyticsText.append("No recent activity\n");
            }
            
            analyticsText.append("\n💡 TIP: Use Admin Dashboard to manage customer leads!");
            
            analyticsTextView.setText(analyticsText.toString());
            
        } catch (Exception e) {
            // Fallback if analytics fail
            analyticsTextView.setText("📊 ANALYTICS DASHBOARD\n\n❌ Unable to load analytics data.\n\n" +
                "This might happen if:\n" +
                "• No data is available yet\n" +
                "• Storage permissions issue\n" +
                "• First time using the app\n\n" +
                "💡 Try creating some bookings and vehicle searches first!");
        }
    }
}