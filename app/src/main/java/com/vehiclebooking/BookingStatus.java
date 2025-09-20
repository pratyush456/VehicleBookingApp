package com.vehiclebooking;

import android.graphics.Color;

/**
 * Enum representing different booking statuses with associated colors and display properties
 */
public enum BookingStatus {
    PENDING("Pending", "#FF9800", "⏳", "Booking request submitted, awaiting confirmation"),
    CONFIRMED("Confirmed", "#4CAF50", "✅", "Booking confirmed by vehicle owner"),
    IN_PROGRESS("In Progress", "#2196F3", "🚗", "Trip is currently in progress"),
    COMPLETED("Completed", "#607D8B", "🏁", "Trip completed successfully"),
    CANCELLED("Cancelled", "#F44336", "❌", "Booking cancelled");

    private final String displayName;
    private final String colorHex;
    private final String icon;
    private final String description;

    BookingStatus(String displayName, String colorHex, String icon, String description) {
        this.displayName = displayName;
        this.colorHex = colorHex;
        this.icon = icon;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public int getColor() {
        return Color.parseColor(colorHex);
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get the next possible statuses that this status can transition to
     */
    public BookingStatus[] getNextPossibleStatuses() {
        switch (this) {
            case PENDING:
                return new BookingStatus[]{CONFIRMED, CANCELLED};
            case CONFIRMED:
                return new BookingStatus[]{IN_PROGRESS, CANCELLED};
            case IN_PROGRESS:
                return new BookingStatus[]{COMPLETED, CANCELLED};
            case COMPLETED:
            case CANCELLED:
                return new BookingStatus[]{}; // Terminal states
            default:
                return new BookingStatus[]{};
        }
    }

    /**
     * Check if this status can transition to the target status
     */
    public boolean canTransitionTo(BookingStatus targetStatus) {
        BookingStatus[] possibleStatuses = getNextPossibleStatuses();
        for (BookingStatus status : possibleStatuses) {
            if (status == targetStatus) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get user-friendly message for status transitions
     */
    public String getTransitionMessage(BookingStatus toStatus) {
        if (!canTransitionTo(toStatus)) {
            return "Cannot change status from " + this.displayName + " to " + toStatus.displayName;
        }

        switch (toStatus) {
            case CONFIRMED:
                return "Booking confirmed! You will be contacted shortly.";
            case IN_PROGRESS:
                return "Your trip has started. Have a safe journey!";
            case COMPLETED:
                return "Trip completed successfully. Thank you for using our service!";
            case CANCELLED:
                return "Booking has been cancelled.";
            default:
                return "Status updated to " + toStatus.displayName;
        }
    }
}