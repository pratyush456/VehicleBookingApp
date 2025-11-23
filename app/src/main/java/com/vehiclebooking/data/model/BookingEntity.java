package com.vehiclebooking.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.vehiclebooking.BookingRequest;
import com.vehiclebooking.BookingStatus;
import com.vehiclebooking.StatusChange;
import org.threeten.bp.LocalDate;
import java.util.List;

@Entity(tableName = "bookings")
public class BookingEntity {
    @PrimaryKey
    @NonNull
    public String bookingId;
    public String source;
    public String destination;
    public LocalDate travelDate;
    public long timestamp;
    public BookingStatus status;
    public List<StatusChange> statusHistory;
    public String phoneNumber;
    public String vehicleType;

    public BookingEntity() {}

    public BookingEntity(BookingRequest booking) {
        this.bookingId = booking.getBookingId();
        this.source = booking.getSource();
        this.destination = booking.getDestination();
        this.travelDate = booking.getTravelDate();
        this.timestamp = booking.getTimestamp();
        this.status = booking.getStatus();
        this.statusHistory = booking.getStatusHistory();
        this.phoneNumber = booking.getPhoneNumber();
        this.vehicleType = booking.getVehicleType();
    }

    public BookingRequest toBookingRequest() {
        BookingRequest booking = new BookingRequest(this.source, this.destination, this.travelDate, 
                                                  this.timestamp, this.status, this.statusHistory);
        booking.setBookingId(this.bookingId);
        booking.setPhoneNumber(this.phoneNumber);
        booking.setVehicleType(this.vehicleType);
        return booking;
    }
}
