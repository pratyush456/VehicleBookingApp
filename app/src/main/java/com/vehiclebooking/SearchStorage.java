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
    
    /**
     * Save a new search record to storage
     */
    public static void saveSearchRecord(Context context, VehicleSearchActivity.SearchRecord searchRecord) {
        SearchRecordDao dao = AppDatabase.getDatabase(context).searchRecordDao();
        
        // Check if record with same phone number and timestamp already exists
        SearchRecordEntity existing = dao.getSearchRecord(searchRecord.phoneNumber, searchRecord.timestamp);
        
        // Add new record if it doesn't exist
        if (existing == null) {
            dao.insertSearchRecord(new SearchRecordEntity(searchRecord));
        }
    }

    /**
     * Update vehicle interest for an existing search record
     */
    /**
     * Update vehicle interest for an existing search record
     */
    public static void updateVehicleInterest(Context context, String phoneNumber, String vehicleInterest) {
        SearchRecordDao dao = AppDatabase.getDatabase(context).searchRecordDao();
        List<SearchRecordEntity> entities = dao.getAllSearchRecords();
        
        SearchRecordEntity mostRecent = null;
        
        // Find the most recent record for this phone number
        for (SearchRecordEntity record : entities) {
            if (record.phoneNumber.equals(phoneNumber)) {
                if (mostRecent == null || record.timestamp.compareTo(mostRecent.timestamp) > 0) {
                    mostRecent = record;
                }
            }
        }
        
        if (mostRecent != null) {
            if (mostRecent.vehicleInterest == null || mostRecent.vehicleInterest.isEmpty()) {
                mostRecent.vehicleInterest = vehicleInterest;
            } else {
                mostRecent.vehicleInterest += ", " + vehicleInterest;
            }
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
    
    // Alias for compatibility
    public static void clearAllSearchRecords(Context context) {
        clearAllRecords(context);
    }
    
    // Get search records (alias for getAllSearchRecords)
    public static List<VehicleSearchActivity.SearchRecord> getSearchRecords(Context context) {
        return getAllSearchRecords(context);
    }
    
    // Update search status
    public static void updateSearchStatus(Context context, String phoneNumber, String timestamp, String newStatus) {
        SearchRecordDao dao = AppDatabase.getDatabase(context).searchRecordDao();
        SearchRecordEntity entity = dao.getSearchRecord(phoneNumber, timestamp);
        if (entity != null) {
            entity.status = newStatus;
            dao.updateSearchRecord(entity);
        }
    }
    
    // Update admin notes
    public static void updateAdminNotes(Context context, String phoneNumber, String timestamp, String notes) {
        SearchRecordDao dao = AppDatabase.getDatabase(context).searchRecordDao();
        SearchRecordEntity entity = dao.getSearchRecord(phoneNumber, timestamp);
        if (entity != null) {
            entity.adminNotes = notes;
            dao.updateSearchRecord(entity);
        }
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