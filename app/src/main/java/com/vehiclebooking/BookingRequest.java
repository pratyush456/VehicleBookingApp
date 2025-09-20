package com.vehiclebooking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingRequest {
    private String source;
    private String destination;
    private Date travelDate;
    private long timestamp;

    public BookingRequest(String source, String destination, Date travelDate) {
        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
        this.timestamp = System.currentTimeMillis();
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

    public String getFormattedTravelDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(travelDate);
    }

    public String getBookingSummary() {
        return "From: " + source + "\n" +
               "To: " + destination + "\n" +
               "Date: " + getFormattedTravelDate();
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