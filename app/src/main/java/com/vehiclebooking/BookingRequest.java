package com.vehiclebooking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingRequest {
    private String source;
    private String destination;
    private Date travelDate;
    private long timestamp;
    private BookingStatus status;
    private List<StatusChange> statusHistory;
    private String phoneNumber;
    private String vehicleType;
    private String bookingId;

    public BookingRequest(String source, String destination, Date travelDate) {
        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
        this.timestamp = System.currentTimeMillis();
        this.status = BookingStatus.PENDING;
        this.statusHistory = new ArrayList<>();
        this.statusHistory.add(new StatusChange(BookingStatus.PENDING, System.currentTimeMillis(), "Booking request submitted"));
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(Date travelDate) {
        this.travelDate = travelDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public List<StatusChange> getStatusHistory() {
        return statusHistory;
    }

    /**
     * Change the booking status with validation
     */
    public boolean changeStatus(BookingStatus newStatus, String reason) {
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

    public String getFormattedTravelDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(travelDate);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
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