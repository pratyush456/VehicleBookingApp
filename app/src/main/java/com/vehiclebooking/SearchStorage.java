package com.vehiclebooking;

import android.content.Context;

import com.vehiclebooking.data.AppDatabase;
import com.vehiclebooking.data.dao.SearchRecordDao;
import com.vehiclebooking.data.model.SearchRecordEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                if (mostRecent == null || record.timestamp.compareTo(mostRecent.timestamp) > 0) {
                    mostRecent = record;
                }
            }
        }
        
        if (mostRecent != null) {
            mostRecent.vehicleInterest = vehicleInterest;
            dao.updateSearchRecord(mostRecent);
        }
    }
    
    // Get all search records (for admin dashboard)
    public static List<VehicleSearchActivity.SearchRecord> getAllSearchRecords(Context context) {
        SearchRecordDao dao = AppDatabase.getDatabase(context).searchRecordDao();
        List<SearchRecordEntity> entities = dao.getAllSearchRecords();
        
        List<VehicleSearchActivity.SearchRecord> records = new ArrayList<>();
        for (SearchRecordEntity entity : entities) {
            records.add(entity.toSearchRecord());
        }
        
        // Sort by timestamp descending (newest first)
        Collections.sort(records, new Comparator<VehicleSearchActivity.SearchRecord>() {
            @Override
            public int compare(VehicleSearchActivity.SearchRecord r1, VehicleSearchActivity.SearchRecord r2) {
                return r2.timestamp.compareTo(r1.timestamp);
            }
        });
        
        return records;
    }
    
    // Get records by status
    public static List<VehicleSearchActivity.SearchRecord> getSearchRecordsByStatus(Context context, String status) {
        SearchRecordDao dao = AppDatabase.getDatabase(context).searchRecordDao();
        List<SearchRecordEntity> entities = dao.getSearchRecordsByStatus(status);
        
        List<VehicleSearchActivity.SearchRecord> records = new ArrayList<>();
        for (SearchRecordEntity entity : entities) {
            records.add(entity.toSearchRecord());
        }
        return records;
    }
    
    // Delete a record
    public static void deleteSearchRecord(Context context, VehicleSearchActivity.SearchRecord record) {
        SearchRecordDao dao = AppDatabase.getDatabase(context).searchRecordDao();
        dao.deleteSearchRecord(record.phoneNumber, record.timestamp);
    }
    
    // Clear all records (for testing/maintenance)
    public static void clearAllRecords(Context context) {
        SearchRecordDao dao = AppDatabase.getDatabase(context).searchRecordDao();
        dao.deleteAllSearchRecords();
    }

    /**
     * Get search analytics data
     */
    public static SearchAnalytics getSearchAnalytics(Context context) {
        List<VehicleSearchActivity.SearchRecord> records = getAllSearchRecords(context);
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