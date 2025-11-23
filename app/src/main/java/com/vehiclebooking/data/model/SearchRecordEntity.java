package com.vehiclebooking.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.vehiclebooking.VehicleSearchActivity;

@Entity(tableName = "search_records")
public class SearchRecordEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String searchQuery;
    public String phoneNumber;
    public String customerName;
    public String timestamp;
    public double latitude;
    public double longitude;
    public boolean locationAvailable;
    public String vehicleInterest;
    public String status;
    public String adminNotes;

    public SearchRecordEntity() {}

    public SearchRecordEntity(VehicleSearchActivity.SearchRecord record) {
        this.searchQuery = record.searchQuery;
        this.phoneNumber = record.phoneNumber;
        this.customerName = record.customerName;
        this.timestamp = record.timestamp;
        this.latitude = record.latitude;
        this.longitude = record.longitude;
        this.locationAvailable = record.locationAvailable;
        this.vehicleInterest = record.vehicleInterest;
        this.status = record.status;
        this.adminNotes = record.adminNotes;
    }

    public VehicleSearchActivity.SearchRecord toSearchRecord() {
        VehicleSearchActivity.SearchRecord record = new VehicleSearchActivity.SearchRecord();
        record.searchQuery = this.searchQuery;
        record.phoneNumber = this.phoneNumber;
        record.customerName = this.customerName;
        record.timestamp = this.timestamp;
        record.latitude = this.latitude;
        record.longitude = this.longitude;
        record.locationAvailable = this.locationAvailable;
        record.vehicleInterest = this.vehicleInterest;
        record.status = this.status;
        record.adminNotes = this.adminNotes;
        return record;
    }
}
