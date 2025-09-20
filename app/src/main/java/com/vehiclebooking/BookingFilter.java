package com.vehiclebooking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for filtering and sorting booking requests
 */
public class BookingFilter {
    
    public enum SortType {
        DATE_NEWEST_FIRST,
        DATE_OLDEST_FIRST,
        STATUS,
        ROUTE_ALPHABETICAL
    }
    
    /**
     * Filter bookings based on search query and status
     */
    public static List<BookingRequest> filterBookings(
            List<BookingRequest> bookings, 
            String searchQuery, 
            BookingStatus statusFilter) {
        
        List<BookingRequest> filtered = new ArrayList<>();
        
        for (BookingRequest booking : bookings) {
            // Apply status filter
            if (statusFilter != null) {
                BookingStatus bookingStatus = booking.getStatus();
                if (bookingStatus == null) bookingStatus = BookingStatus.PENDING;
                if (bookingStatus != statusFilter) {
                    continue;
                }
            }
            
            // Apply search query filter
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String query = searchQuery.toLowerCase().trim();
                if (!matchesSearchQuery(booking, query)) {
                    continue;
                }
            }
            
            filtered.add(booking);
        }
        
        return filtered;
    }
    
    /**
     * Check if booking matches search query
     */
    private static boolean matchesSearchQuery(BookingRequest booking, String query) {
        // Search in source
        if (booking.getSource().toLowerCase().contains(query)) {
            return true;
        }
        
        // Search in destination
        if (booking.getDestination().toLowerCase().contains(query)) {
            return true;
        }
        
        // Search in booking ID
        String bookingId = "BK" + String.valueOf(booking.getTimestamp()).substring(8);
        if (bookingId.toLowerCase().contains(query)) {
            return true;
        }
        
        // Search in route (combined source -> destination)
        String route = booking.getSource() + " " + booking.getDestination();
        if (route.toLowerCase().contains(query)) {
            return true;
        }
        
        // Search in status
        BookingStatus status = booking.getStatus();
        if (status == null) status = BookingStatus.PENDING;
        if (status.getDisplayName().toLowerCase().contains(query)) {
            return true;
        }
        
        // Search in formatted travel date
        if (booking.getFormattedTravelDate().toLowerCase().contains(query)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Sort bookings based on the specified sort type
     */
    public static List<BookingRequest> sortBookings(List<BookingRequest> bookings, SortType sortType) {
        List<BookingRequest> sorted = new ArrayList<>(bookings);
        
        switch (sortType) {
            case DATE_NEWEST_FIRST:
                Collections.sort(sorted, (b1, b2) -> Long.compare(b2.getTimestamp(), b1.getTimestamp()));
                break;
                
            case DATE_OLDEST_FIRST:
                Collections.sort(sorted, (b1, b2) -> Long.compare(b1.getTimestamp(), b2.getTimestamp()));
                break;
                
            case STATUS:
                Collections.sort(sorted, new Comparator<BookingRequest>() {
                    @Override
                    public int compare(BookingRequest b1, BookingRequest b2) {
                        BookingStatus s1 = b1.getStatus();
                        BookingStatus s2 = b2.getStatus();
                        if (s1 == null) s1 = BookingStatus.PENDING;
                        if (s2 == null) s2 = BookingStatus.PENDING;
                        
                        // Sort by status priority: PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
                        int priority1 = getStatusPriority(s1);
                        int priority2 = getStatusPriority(s2);
                        
                        int statusCompare = Integer.compare(priority1, priority2);
                        if (statusCompare != 0) {
                            return statusCompare;
                        }
                        
                        // If same status, sort by date (newest first)
                        return Long.compare(b2.getTimestamp(), b1.getTimestamp());
                    }
                });
                break;
                
            case ROUTE_ALPHABETICAL:
                Collections.sort(sorted, (b1, b2) -> {
                    String route1 = b1.getSource() + " → " + b1.getDestination();
                    String route2 = b2.getSource() + " → " + b2.getDestination();
                    return route1.compareToIgnoreCase(route2);
                });
                break;
        }
        
        return sorted;
    }
    
    /**
     * Get priority for status sorting (lower number = higher priority)
     */
    private static int getStatusPriority(BookingStatus status) {
        switch (status) {
            case PENDING: return 1;
            case CONFIRMED: return 2;
            case IN_PROGRESS: return 3;
            case COMPLETED: return 4;
            case CANCELLED: return 5;
            default: return 6;
        }
    }
    
    /**
     * Get search suggestions based on existing bookings
     */
    public static List<String> getSearchSuggestions(List<BookingRequest> bookings) {
        List<String> suggestions = new ArrayList<>();
        
        for (BookingRequest booking : bookings) {
            // Add unique sources and destinations
            if (!suggestions.contains(booking.getSource())) {
                suggestions.add(booking.getSource());
            }
            if (!suggestions.contains(booking.getDestination())) {
                suggestions.add(booking.getDestination());
            }
            
            // Add routes
            String route = booking.getSource() + " → " + booking.getDestination();
            if (!suggestions.contains(route)) {
                suggestions.add(route);
            }
        }
        
        Collections.sort(suggestions, String.CASE_INSENSITIVE_ORDER);
        return suggestions;
    }
    
    /**
     * Get booking statistics for filters
     */
    public static FilterStats getFilterStats(List<BookingRequest> bookings) {
        FilterStats stats = new FilterStats();
        
        for (BookingRequest booking : bookings) {
            BookingStatus status = booking.getStatus();
            if (status == null) status = BookingStatus.PENDING;
            
            switch (status) {
                case PENDING: stats.pendingCount++; break;
                case CONFIRMED: stats.confirmedCount++; break;
                case IN_PROGRESS: stats.inProgressCount++; break;
                case COMPLETED: stats.completedCount++; break;
                case CANCELLED: stats.cancelledCount++; break;
            }
            stats.totalCount++;
        }
        
        return stats;
    }
    
    /**
     * Statistics class for filter information
     */
    public static class FilterStats {
        public int totalCount = 0;
        public int pendingCount = 0;
        public int confirmedCount = 0;
        public int inProgressCount = 0;
        public int completedCount = 0;
        public int cancelledCount = 0;
        
        public int getCountForStatus(BookingStatus status) {
            switch (status) {
                case PENDING: return pendingCount;
                case CONFIRMED: return confirmedCount;
                case IN_PROGRESS: return inProgressCount;
                case COMPLETED: return completedCount;
                case CANCELLED: return cancelledCount;
                default: return 0;
            }
        }
    }
}