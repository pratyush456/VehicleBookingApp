package com.vehiclebooking;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive analytics engine for booking data analysis
 */
public class BookingAnalytics {
    
    /**
     * Overall booking statistics
     */
    public static class BookingStats {
        public int totalBookings = 0;
        public int pendingBookings = 0;
        public int confirmedBookings = 0;
        public int inProgressBookings = 0;
        public int completedBookings = 0;
        public int cancelledBookings = 0;
        
        public double completionRate = 0.0;
        public double cancellationRate = 0.0;
        public int activeBookings = 0;
        public int thisMonthBookings = 0;
        public int thisWeekBookings = 0;
        
        public long avgBookingDuration = 0; // in milliseconds
        public BookingRequest mostRecentBooking = null;
        public BookingRequest oldestBooking = null;
    }
    
    /**
     * Route popularity data
     */
    public static class RouteStats {
        public String route;
        public int count;
        public double percentage;
        public BookingStatus mostCommonStatus;
        public long avgDuration;
        
        public RouteStats(String route, int count, double percentage) {
            this.route = route;
            this.count = count;
            this.percentage = percentage;
        }
    }
    
    /**
     * Time-based booking trends
     */
    public static class BookingTrends {
        public Map<String, Integer> dailyBookings = new HashMap<>();
        public Map<String, Integer> weeklyBookings = new HashMap<>();
        public Map<String, Integer> monthlyBookings = new HashMap<>();
        public int peakBookingHour = 0;
        public String busiestDay = "";
        public String busiestMonth = "";
    }
    
    /**
     * Smart insights and recommendations
     */
    public static class BookingInsights {
        public String primaryInsight = "";
        public List<String> recommendations = new ArrayList<>();
        public String travelPattern = "";
        public String favoriteDestination = "";
        public String preferredBookingDay = "";
        public double avgBookingsPerWeek = 0.0;
    }
    
    /**
     * Calculate comprehensive booking statistics
     */
    public static BookingStats calculateBookingStats(List<BookingRequest> bookings) {
        BookingStats stats = new BookingStats();
        
        if (bookings.isEmpty()) {
            return stats;
        }
        
        stats.totalBookings = bookings.size();
        
        // Calculate current time boundaries using LocalDate
        LocalDate today = DateUtils.today();
        LocalDate weekStart = today.minusDays(7);
        LocalDate monthStart = today.minusDays(30);
        long weekStartTimestamp = DateUtils.localDateToTimestamp(weekStart);
        long monthStartTimestamp = DateUtils.localDateToTimestamp(monthStart);
        
        long totalDuration = 0;
        BookingRequest newest = bookings.get(0);
        BookingRequest oldest = bookings.get(0);
        
        for (BookingRequest booking : bookings) {
            BookingStatus status = booking.getStatus();
            if (status == null) status = BookingStatus.PENDING;
            
            // Count by status
            switch (status) {
                case PENDING: stats.pendingBookings++; break;
                case CONFIRMED: stats.confirmedBookings++; break;
                case IN_PROGRESS: stats.inProgressBookings++; break;
                case COMPLETED: stats.completedBookings++; break;
                case CANCELLED: stats.cancelledBookings++; break;
            }
            
            // Active bookings (not completed or cancelled)
            if (status != BookingStatus.COMPLETED && status != BookingStatus.CANCELLED) {
                stats.activeBookings++;
            }
            
            // Time-based counting
            long bookingTimestamp = booking.getTimestamp();
            if (bookingTimestamp >= weekStartTimestamp) {
                stats.thisWeekBookings++;
            }
            if (bookingTimestamp >= monthStartTimestamp) {
                stats.thisMonthBookings++;
            }
            
            // Duration calculation (from booking to travel date)
            long travelDateTimestamp = DateUtils.localDateToTimestamp(booking.getTravelDate());
            totalDuration += travelDateTimestamp - bookingTimestamp;
            
            // Find newest and oldest
            if (booking.getTimestamp() > newest.getTimestamp()) {
                newest = booking;
            }
            if (booking.getTimestamp() < oldest.getTimestamp()) {
                oldest = booking;
            }
        }
        
        // Calculate rates
        if (stats.totalBookings > 0) {
            stats.completionRate = (stats.completedBookings * 100.0) / stats.totalBookings;
            stats.cancellationRate = (stats.cancelledBookings * 100.0) / stats.totalBookings;
            stats.avgBookingDuration = totalDuration / stats.totalBookings;
        }
        
        stats.mostRecentBooking = newest;
        stats.oldestBooking = oldest;
        
        return stats;
    }
    
    /**
     * Analyze route popularity and patterns
     */
    public static List<RouteStats> analyzeRoutePopularity(List<BookingRequest> bookings) {
        Map<String, Integer> routeCounts = new HashMap<>();
        Map<String, List<BookingStatus>> routeStatuses = new HashMap<>();
        Map<String, Long> routeDurations = new HashMap<>();
        
        for (BookingRequest booking : bookings) {
            String route = booking.getSource() + " → " + booking.getDestination();
            
            Integer currentCount = routeCounts.get(route);
            routeCounts.put(route, (currentCount == null ? 0 : currentCount) + 1);
            
            // Track statuses for this route
            if (!routeStatuses.containsKey(route)) {
                routeStatuses.put(route, new ArrayList<>());
            }
            BookingStatus status = booking.getStatus();
            if (status == null) status = BookingStatus.PENDING;
            routeStatuses.get(route).add(status);
            
            // Track durations
            long travelDateTimestamp = DateUtils.localDateToTimestamp(booking.getTravelDate());
            long duration = travelDateTimestamp - booking.getTimestamp();
            Long currentDuration = routeDurations.get(route);
            routeDurations.put(route, (currentDuration == null ? 0L : currentDuration) + duration);
        }
        
        List<RouteStats> routeStats = new ArrayList<>();
        int totalBookings = bookings.size();
        
        for (Map.Entry<String, Integer> entry : routeCounts.entrySet()) {
            String route = entry.getKey();
            int count = entry.getValue();
            double percentage = (count * 100.0) / totalBookings;
            
            RouteStats stats = new RouteStats(route, count, percentage);
            
            // Find most common status for this route
            List<BookingStatus> statuses = routeStatuses.get(route);
            Map<BookingStatus, Integer> statusCount = new HashMap<>();
            for (BookingStatus status : statuses) {
                Integer currentStatusCount = statusCount.get(status);
                statusCount.put(status, (currentStatusCount == null ? 0 : currentStatusCount) + 1);
            }
            
            BookingStatus mostCommon = BookingStatus.PENDING;
            int maxCount = 0;
            for (Map.Entry<BookingStatus, Integer> statusEntry : statusCount.entrySet()) {
                if (statusEntry.getValue() > maxCount) {
                    maxCount = statusEntry.getValue();
                    mostCommon = statusEntry.getKey();
                }
            }
            stats.mostCommonStatus = mostCommon;
            
            // Average duration for this route
            stats.avgDuration = routeDurations.get(route) / count;
            
            routeStats.add(stats);
        }
        
        // Sort by popularity
        Collections.sort(routeStats, (a, b) -> Integer.compare(b.count, a.count));
        
        return routeStats;
    }
    
    /**
     * Analyze booking trends over time
     */
    public static BookingTrends analyzeBookingTrends(List<BookingRequest> bookings) {
        BookingTrends trends = new BookingTrends();
        
        Map<Integer, Integer> hourCounts = new HashMap<>();
        Map<String, Integer> dayCounts = new HashMap<>();
        Map<String, Integer> monthCounts = new HashMap<>();
        
        for (BookingRequest booking : bookings) {
            long bookingTimestamp = booking.getTimestamp();
            
            // Daily trends
            String dayName = DateUtils.getDayName(bookingTimestamp);
            Integer currentDayCount = dayCounts.get(dayName);
            dayCounts.put(dayName, (currentDayCount == null ? 0 : currentDayCount) + 1);
            
            // Monthly trends
            String monthName = DateUtils.getMonthYear(bookingTimestamp);
            Integer currentMonthCount = monthCounts.get(monthName);
            monthCounts.put(monthName, (currentMonthCount == null ? 0 : currentMonthCount) + 1);
            
            // Hour trends
            int hour = DateUtils.getHour(bookingTimestamp);
            Integer currentHourCount = hourCounts.get(hour);
            hourCounts.put(hour, (currentHourCount == null ? 0 : currentHourCount) + 1);
        }
        
        trends.dailyBookings.putAll(dayCounts);
        trends.monthlyBookings.putAll(monthCounts);
        
        // Find peak hour
        int maxHourCount = 0;
        for (Map.Entry<Integer, Integer> entry : hourCounts.entrySet()) {
            if (entry.getValue() > maxHourCount) {
                maxHourCount = entry.getValue();
                trends.peakBookingHour = entry.getKey();
            }
        }
        
        // Find busiest day
        int maxDayCount = 0;
        for (Map.Entry<String, Integer> entry : dayCounts.entrySet()) {
            if (entry.getValue() > maxDayCount) {
                maxDayCount = entry.getValue();
                trends.busiestDay = entry.getKey();
            }
        }
        
        // Find busiest month
        int maxMonthCount = 0;
        for (Map.Entry<String, Integer> entry : monthCounts.entrySet()) {
            if (entry.getValue() > maxMonthCount) {
                maxMonthCount = entry.getValue();
                trends.busiestMonth = entry.getKey();
            }
        }
        
        return trends;
    }
    
    /**
     * Generate smart insights and recommendations
     */
    public static BookingInsights generateInsights(List<BookingRequest> bookings) {
        BookingInsights insights = new BookingInsights();
        
        if (bookings.isEmpty()) {
            insights.primaryInsight = "No booking data available yet. Start making bookings to see insights!";
            return insights;
        }
        
        BookingStats stats = calculateBookingStats(bookings);
        List<RouteStats> routes = analyzeRoutePopularity(bookings);
        BookingTrends trends = analyzeBookingTrends(bookings);
        
        // Primary insight based on data
        if (stats.completionRate > 80) {
            insights.primaryInsight = "Excellent! You have a " + String.format("%.1f", stats.completionRate) + "% booking completion rate.";
        } else if (stats.cancellationRate > 20) {
            insights.primaryInsight = "Consider planning trips further in advance to reduce cancellations.";
        } else {
            insights.primaryInsight = "You're building a great travel history with " + stats.totalBookings + " bookings!";
        }
        
        // Travel pattern
        if (stats.thisWeekBookings > 0) {
            insights.travelPattern = "Active traveler - " + stats.thisWeekBookings + " bookings this week";
        } else if (stats.thisMonthBookings > 0) {
            insights.travelPattern = "Regular traveler - " + stats.thisMonthBookings + " bookings this month";
        } else {
            insights.travelPattern = "Occasional traveler - plan your next adventure!";
        }
        
        // Favorite destination
        if (!routes.isEmpty()) {
            insights.favoriteDestination = routes.get(0).route;
        }
        
        // Preferred booking day
        insights.preferredBookingDay = trends.busiestDay.isEmpty() ? "No pattern yet" : trends.busiestDay;
        
        // Average bookings per week
        if (!bookings.isEmpty()) {
            long daysSinceFirst = (System.currentTimeMillis() - stats.oldestBooking.getTimestamp()) / (24 * 60 * 60 * 1000);
            if (daysSinceFirst > 7) {
                insights.avgBookingsPerWeek = (stats.totalBookings * 7.0) / daysSinceFirst;
            }
        }
        
        // Generate recommendations
        insights.recommendations.add("🎯 " + generateMainRecommendation(stats, routes, trends));
        
        if (stats.activeBookings > 0) {
            insights.recommendations.add("📅 You have " + stats.activeBookings + " active booking(s) - don't forget your upcoming trips!");
        }
        
        if (!routes.isEmpty() && routes.get(0).count > 1) {
            insights.recommendations.add("🛣️ " + routes.get(0).route + " is your most popular route (" + routes.get(0).count + " times)");
        }
        
        if (!trends.busiestDay.isEmpty()) {
            insights.recommendations.add("📊 You prefer booking on " + trends.busiestDay + "s - plan accordingly!");
        }
        
        return insights;
    }
    
    private static String generateMainRecommendation(BookingStats stats, List<RouteStats> routes, BookingTrends trends) {
        if (stats.cancellationRate > 15) {
            return "Try booking trips closer to your travel date to reduce cancellations";
        } else if (stats.completedBookings == 0 && stats.totalBookings > 2) {
            return "Complete your first trip to unlock achievement badges!";
        } else if (!routes.isEmpty() && routes.size() > 3) {
            return "You're exploring many routes - consider keeping a travel journal!";
        } else if (stats.thisMonthBookings == 0 && stats.totalBookings > 0) {
            return "It's been a while since your last booking - time for a new adventure?";
        } else {
            return "Keep up the great booking habits!";
        }
    }
}