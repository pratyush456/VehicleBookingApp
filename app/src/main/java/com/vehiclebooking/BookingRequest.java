package com.vehiclebooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class BookingRequest {
    @SerializedName("source")
    @NonNull
    private final String source;
    
    @SerializedName("destination")
    @NonNull
    private final String destination;
    
    @SerializedName("travel_date")
    @NonNull
    private final LocalDate travelDate;
    
    @SerializedName("timestamp")
    private final long timestamp;
    
    @SerializedName("status")
    @Nullable
    private BookingStatus status;
    
    @SerializedName("status_history")
    @NonNull
    private final List<StatusChange> statusHistory;
    
    @SerializedName("phone_number")
    @Nullable
    private String phoneNumber;
    
    @SerializedName("vehicle_type")
    @Nullable
    private String vehicleType;
    
    @SerializedName("booking_id")
    @Nullable
    private String bookingId;

    public BookingRequest(@NonNull String source, @NonNull String destination, @NonNull LocalDate travelDate) {
        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
        this.timestamp = System.currentTimeMillis();
        this.status = BookingStatus.PENDING;
        this.statusHistory = new ArrayList<>();
        this.statusHistory.add(new StatusChange(BookingStatus.PENDING, System.currentTimeMillis(), "Booking request submitted"));
    }

    // Constructor for restoring from storage
    public BookingRequest(@NonNull String source, @NonNull String destination, @NonNull LocalDate travelDate, 
                          long timestamp, BookingStatus status, List<StatusChange> statusHistory) {
        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
        this.timestamp = timestamp;
        this.status = status;
        this.statusHistory = statusHistory != null ? statusHistory : new ArrayList<>();
    }
    
    /**
     * Create a copy of this booking with new source, destination, and travel date
     * Preserves booking ID, phone number, vehicle type, status, and status history
     */
    @NonNull
    public BookingRequest createModifiedCopy(@NonNull String newSource, @NonNull String newDestination, @NonNull LocalDate newTravelDate) {
        BookingRequest copy = new BookingRequest(newSource, newDestination, newTravelDate);
        // Copy mutable fields
        copy.setBookingId(this.bookingId);
        copy.setPhoneNumber(this.phoneNumber);
        copy.setVehicleType(this.vehicleType);
        // Copy status (if not null, restore it)
        if (this.status != null) {
            // Clear the initial PENDING status added in constructor
            copy.statusHistory.clear();
            // Copy all status history from original
            copy.statusHistory.addAll(this.statusHistory);
            // Restore status
            copy.status = this.status;
        }
        return copy;
    }

    @NonNull
    public String getSource() {
        return source;
    }

    @NonNull
    public String getDestination() {
        return destination;
    }

    @NonNull
    public LocalDate getTravelDate() {
        return travelDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Nullable
    public BookingStatus getStatus() {
        return status;
    }

    @NonNull
    public List<StatusChange> getStatusHistory() {
        return statusHistory;
    }

    /**
     * Change the booking status with validation
     */
    public boolean changeStatus(@NonNull BookingStatus newStatus, @NonNull String reason) {
        if (status == null) {
            status = BookingStatus.PENDING;
        }
        
        if (!status.canTransitionTo(newStatus)) {
            return false;
        }
        
        status = newStatus;
        statusHistory.add(new StatusChange(newStatus, System.currentTimeMillis(), reason));
        return true;
    }

    /**
     * Get the latest status change
     */
    public StatusChange getLatestStatusChange() {
        if (statusHistory.isEmpty()) {
            return null;
        }
        return statusHistory.get(statusHistory.size() - 1);
    }

    /**
     * Get status display text with icon
     */
    public String getStatusDisplayText() {
        if (status == null) {
            status = BookingStatus.PENDING;
        }
        return status.getIcon() + " " + status.getDisplayName();
    }

    @NonNull
    public String getFormattedTravelDate() {
        return DateUtils.formatDate(travelDate);
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(@Nullable String vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Nullable
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(@Nullable String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingSummary() {
        return "Booking ID: " + (bookingId != null ? bookingId : "N/A") + "\n" +
               "From: " + source + "\n" +
               "To: " + destination + "\n" +
               "Date: " + getFormattedTravelDate() + "\n" +
               "Vehicle: " + (vehicleType != null ? vehicleType : "Not specified") + "\n" +
               "Phone: " + (phoneNumber != null ? phoneNumber : "Not provided");
    }

    @Override
    public String toString() {
        return "BookingRequest{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", travelDate=" + travelDate +
                ", timestamp=" + timestamp +
                '}';
    }
}