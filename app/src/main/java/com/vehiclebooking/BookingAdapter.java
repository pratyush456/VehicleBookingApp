package com.vehiclebooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    
    private List<BookingRequest> bookingList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timestampFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());

    public BookingAdapter(List<BookingRequest> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingRequest booking = bookingList.get(position);
        
        // Generate booking ID based on timestamp
        String bookingId = "BK" + String.valueOf(booking.getTimestamp()).substring(8);
        holder.bookingId.setText("Booking #" + bookingId);
        
        // Set booking status (for now, all bookings are pending)
        holder.bookingStatus.setText("PENDING");
        
        // Set route information
        holder.source.setText("From: " + booking.getSource());
        holder.destination.setText("To: " + booking.getDestination());
        holder.travelDate.setText("📅 " + booking.getFormattedTravelDate());
        
        // Set booking timestamp
        String bookingTime = timestampFormat.format(new Date(booking.getTimestamp()));
        holder.bookingTime.setText("Booked: " + bookingTime);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void updateBookings(List<BookingRequest> newBookings) {
        this.bookingList = newBookings;
        notifyDataSetChanged();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingId, bookingStatus, source, destination, travelDate, bookingTime;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingId = itemView.findViewById(R.id.tv_booking_id);
            bookingStatus = itemView.findViewById(R.id.tv_booking_status);
            source = itemView.findViewById(R.id.tv_source);
            destination = itemView.findViewById(R.id.tv_destination);
            travelDate = itemView.findViewById(R.id.tv_travel_date);
            bookingTime = itemView.findViewById(R.id.tv_booking_time);
        }
    }
}