package com.vehiclebooking;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a status change event in a booking's history
 */
public class StatusChange {
    @SerializedName("status")
    @NonNull
    private final BookingStatus status;
    
    @SerializedName("timestamp")
    private final long timestamp;
    
    @SerializedName("reason")
    @NonNull
    private final String reason;

    public StatusChange(@NonNull BookingStatus status, long timestamp, @NonNull String reason) {
        this.status = status;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    @NonNull
    public BookingStatus getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @NonNull
    public String getReason() {
        return reason;
    }

    /**
     * Get formatted timestamp for display
     */
    @NonNull
    public String getFormattedTimestamp() {
        return DateUtils.formatDateTime12Hour(DateUtils.timestampToLocalDateTime(timestamp));
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