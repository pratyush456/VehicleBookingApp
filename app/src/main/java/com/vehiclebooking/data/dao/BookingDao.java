package com.vehiclebooking.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.vehiclebooking.BookingStatus;
import com.vehiclebooking.data.model.BookingEntity;
import java.util.List;

@Dao
public interface BookingDao {
    @Query("SELECT * FROM bookings")
    List<BookingEntity> getAllBookings();

    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId")
    BookingEntity getBookingById(String bookingId);

    @Query("SELECT * FROM bookings WHERE status = :status")
    List<BookingEntity> getBookingsByStatus(BookingStatus status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBooking(BookingEntity booking);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookings(List<BookingEntity> bookings);

    @Update
    void updateBooking(BookingEntity booking);

    @Query("DELETE FROM bookings")
    void deleteAllBookings();
}
