package com.vehiclebooking;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    
    private List<BookingRequest> bookingList;
    private OnStatusChangeClickListener statusChangeClickListener;

    public interface OnStatusChangeClickListener {
        void onStatusChangeClick(BookingRequest booking, int position);
    }

    public BookingAdapter(List<BookingRequest> bookingList) {
        this.bookingList = bookingList;
    }

    public BookingAdapter(List<BookingRequest> bookingList, OnStatusChangeClickListener statusChangeClickListener) {
        this.bookingList = bookingList;
        this.statusChangeClickListener = statusChangeClickListener;
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
        
        // Set dynamic booking status with color and icon
        BookingStatus status = booking.getStatus();
        if (status == null) {
            status = BookingStatus.PENDING; // Default fallback
        }
        
        holder.bookingStatus.setText(status.getDisplayName());
        
        // Set status background color
        GradientDrawable statusBackground = new GradientDrawable();
        statusBackground.setShape(GradientDrawable.RECTANGLE);
        statusBackground.setCornerRadius(12f);
        statusBackground.setColor(status.getColor());
        holder.bookingStatus.setBackground(statusBackground);
        
        // Set text color to white for better contrast
        holder.bookingStatus.setTextColor(0xFFFFFFFF);
        
        // Set route information
        holder.source.setText("From: " + booking.getSource());
        holder.destination.setText("To: " + booking.getDestination());
        holder.travelDate.setText("📅 " + booking.getFormattedTravelDate());
        
        // Set booking timestamp and latest status change info
        String bookingTime = DateUtils.formatDateTime12Hour(DateUtils.timestampToLocalDateTime(booking.getTimestamp()));
        
        // Show latest status change if available
        StatusChange latestChange = booking.getLatestStatusChange();
        if (latestChange != null && latestChange.getStatus() != BookingStatus.PENDING) {
            String statusTime = DateUtils.formatDateTime12Hour(DateUtils.timestampToLocalDateTime(latestChange.getTimestamp()));
            holder.bookingTime.setText("Booked: " + bookingTime + "\n" + 
                                    status.getIcon() + " " + status.getDisplayName() + ": " + statusTime);
        } else {
            holder.bookingTime.setText("Booked: " + bookingTime);
        }

        // Set up booking card click listener for details
        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, BookingDetailsActivity.class);
            intent.putExtra(BookingDetailsActivity.EXTRA_BOOKING_TIMESTAMP, booking.getTimestamp());
            context.startActivity(intent);
        });
        
        // Set up status change button click listener
        holder.changeStatusButton.setOnClickListener(v -> {
            if (statusChangeClickListener != null) {
                statusChangeClickListener.onStatusChangeClick(booking, position);
            }
        });
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
        TextView bookingId, bookingStatus, source, destination, travelDate, bookingTime, changeStatusButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingId = itemView.findViewById(R.id.tv_booking_id);
            bookingStatus = itemView.findViewById(R.id.tv_booking_status);
            source = itemView.findViewById(R.id.tv_source);
            destination = itemView.findViewById(R.id.tv_destination);
            travelDate = itemView.findViewById(R.id.tv_travel_date);
            bookingTime = itemView.findViewById(R.id.tv_booking_time);
            changeStatusButton = itemView.findViewById(R.id.btn_change_status);
        }
    }
}