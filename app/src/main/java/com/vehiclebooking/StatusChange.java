package com.vehiclebooking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Represents a status change event in a booking's history
 */
public class StatusChange {
    private BookingStatus status;
    private long timestamp;
    private String reason;

    public StatusChange(BookingStatus status, long timestamp, String reason) {
        this.status = status;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getReason() {
        return reason;
    }

    /**
     * Get formatted timestamp for display
     */
    public String getFormattedTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }

    /**
     * Get a user-friendly description of this status change
     */
    public String getDescription() {
        return status.getIcon() + " " + status.getDisplayName() + " - " + reason + 
               " (" + getFormattedTimestamp() + ")";
    }

    @Override
    public String toString() {
        return "StatusChange{" +
                "status=" + status +
                ", timestamp=" + timestamp +
                ", reason='" + reason + '\'' +
                '}';
    }
}