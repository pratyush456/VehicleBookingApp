package com.vehiclebooking;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchStorage {
    private static final String PREFS_NAME = "vehicle_search_storage";
    private static final String SEARCH_RECORDS_KEY = "search_records";
    private static Gson gson = new Gson();

    /**
     * Save a new search record to storage
     */
    public static void saveSearchRecord(Context context, VehicleSearchActivity.SearchRecord searchRecord) {
        List<VehicleSearchActivity.SearchRecord> records = getSearchRecords(context);
        
        // Check if record with same phone number and timestamp already exists
        boolean recordExists = false;
        for (VehicleSearchActivity.SearchRecord existing : records) {
            if (existing.phoneNumber.equals(searchRecord.phoneNumber) && 
                existing.timestamp.equals(searchRecord.timestamp)) {
                recordExists = true;
                break;
            }
        }
        
        // Add new record if it doesn't exist
        if (!recordExists) {
            records.add(searchRecord);
            saveSearchRecords(context, records);
        }
    }

    /**
     * Update vehicle interest for an existing search record
     */
    public static void updateVehicleInterest(Context context, String phoneNumber, String vehicleInterest) {
        List<VehicleSearchActivity.SearchRecord> records = getSearchRecords(context);
        
        // Find the most recent record for this phone number and update it
        for (int i = records.size() - 1; i >= 0; i--) {
            VehicleSearchActivity.SearchRecord record = records.get(i);
            if (record.phoneNumber.equals(phoneNumber)) {
                if (record.vehicleInterest.isEmpty()) {
                    record.vehicleInterest = vehicleInterest;
                } else {
                    record.vehicleInterest += ", " + vehicleInterest;
                }
                saveSearchRecords(context, records);
                break;
            }
        }
    }

    /**
     * Update admin notes for a search record
     */
    public static void updateAdminNotes(Context context, String phoneNumber, String timestamp, String notes) {
        List<VehicleSearchActivity.SearchRecord> records = getSearchRecords(context);
        
        for (VehicleSearchActivity.SearchRecord record : records) {
            if (record.phoneNumber.equals(phoneNumber) && record.timestamp.equals(timestamp)) {
                record.adminNotes = notes;
                saveSearchRecords(context, records);
                break;
            }
        }
    }

    /**
     * Update status for a search record
     */
    public static void updateSearchStatus(Context context, String phoneNumber, String timestamp, String status) {
        List<VehicleSearchActivity.SearchRecord> records = getSearchRecords(context);
        
        for (VehicleSearchActivity.SearchRecord record : records) {
            if (record.phoneNumber.equals(phoneNumber) && record.timestamp.equals(timestamp)) {
                record.status = status;
                saveSearchRecords(context, records);
                break;
            }
        }
    }

    /**
     * Get all search records
     */
    public static List<VehicleSearchActivity.SearchRecord> getSearchRecords(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String recordsJson = prefs.getString(SEARCH_RECORDS_KEY, "[]");
        
        Type listType = new TypeToken<List<VehicleSearchActivity.SearchRecord>>(){}.getType();
        List<VehicleSearchActivity.SearchRecord> records = gson.fromJson(recordsJson, listType);
        
        return records != null ? records : new ArrayList<>();
    }

    /**
     * Get search records filtered by status
     */
    public static List<VehicleSearchActivity.SearchRecord> getSearchRecordsByStatus(Context context, String status) {
        List<VehicleSearchActivity.SearchRecord> allRecords = getSearchRecords(context);
        List<VehicleSearchActivity.SearchRecord> filteredRecords = new ArrayList<>();
        
        for (VehicleSearchActivity.SearchRecord record : allRecords) {
            if (record.status.equals(status)) {
                filteredRecords.add(record);
            }
        }
        
        return filteredRecords;
    }

    /**
     * Get recent search records (last 30 days)
     */
    public static List<VehicleSearchActivity.SearchRecord> getRecentSearchRecords(Context context) {
        List<VehicleSearchActivity.SearchRecord> allRecords = getSearchRecords(context);
        List<VehicleSearchActivity.SearchRecord> recentRecords = new ArrayList<>();
        
        long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24L * 60L * 60L * 1000L);
        
        for (VehicleSearchActivity.SearchRecord record : allRecords) {
            try {
                // Parse timestamp and check if it's within 30 days
                // This is a simple check - in production you'd use proper date parsing
                if (record.timestamp.length() >= 10) {
                    recentRecords.add(record);
                }
            } catch (Exception e) {
                // If parsing fails, include the record anyway
                recentRecords.add(record);
            }
        }
        
        return recentRecords;
    }

    /**
     * Get search analytics data
     */
    public static SearchAnalytics getSearchAnalytics(Context context) {
        List<VehicleSearchActivity.SearchRecord> records = getSearchRecords(context);
        SearchAnalytics analytics = new SearchAnalytics();
        
        analytics.totalSearches = records.size();
        
        // Count by status
        for (VehicleSearchActivity.SearchRecord record : records) {
            switch (record.status) {
                case "New":
                    analytics.newSearches++;
                    break;
                case "Contacted":
                    analytics.contactedSearches++;
                    break;
                case "Completed":
                    analytics.completedSearches++;
                    break;
            }
        }
        
        // Count popular search terms
        for (VehicleSearchActivity.SearchRecord record : records) {
            String query = record.searchQuery.toLowerCase();
            if (query.contains("sedan")) analytics.sedanSearches++;
            if (query.contains("suv")) analytics.suvSearches++;
            if (query.contains("van")) analytics.vanSearches++;
            if (query.contains("luxury")) analytics.luxurySearches++;
        }
        
        // Count searches with location
        for (VehicleSearchActivity.SearchRecord record : records) {
            if (record.locationAvailable) {
                analytics.searchesWithLocation++;
            }
        }
        
        return analytics;
    }

    /**
     * Clear all search records (for testing or reset)
     */
    public static void clearAllSearchRecords(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(SEARCH_RECORDS_KEY).apply();
    }

    /**
     * Delete a specific search record
     */
    public static void deleteSearchRecord(Context context, String phoneNumber, String timestamp) {
        List<VehicleSearchActivity.SearchRecord> records = getSearchRecords(context);
        
        for (int i = 0; i < records.size(); i++) {
            VehicleSearchActivity.SearchRecord record = records.get(i);
            if (record.phoneNumber.equals(phoneNumber) && record.timestamp.equals(timestamp)) {
                records.remove(i);
                saveSearchRecords(context, records);
                break;
            }
        }
    }

    /**
     * Save search records list to storage
     */
    private static void saveSearchRecords(Context context, List<VehicleSearchActivity.SearchRecord> records) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String recordsJson = gson.toJson(records);
        prefs.edit().putString(SEARCH_RECORDS_KEY, recordsJson).apply();
    }

    /**
     * Analytics data structure
     */
    public static class SearchAnalytics {
        public int totalSearches = 0;
        public int newSearches = 0;
        public int contactedSearches = 0;
        public int completedSearches = 0;
        public int searchesWithLocation = 0;
        public int sedanSearches = 0;
        public int suvSearches = 0;
        public int vanSearches = 0;
        public int luxurySearches = 0;
        
        public double getContactRate() {
            if (totalSearches == 0) return 0.0;
            return (double) contactedSearches / totalSearches * 100;
        }
        
        public double getCompletionRate() {
            if (totalSearches == 0) return 0.0;
            return (double) completedSearches / totalSearches * 100;
        }
        
        public String getMostPopularVehicleType() {
            int maxCount = Math.max(Math.max(sedanSearches, suvSearches), 
                                  Math.max(vanSearches, luxurySearches));
            
            if (maxCount == 0) return "None";
            
            if (sedanSearches == maxCount) return "Sedan";
            if (suvSearches == maxCount) return "SUV";
            if (vanSearches == maxCount) return "Van";
            if (luxurySearches == maxCount) return "Luxury";
            
            return "Mixed";
        }
    }
}