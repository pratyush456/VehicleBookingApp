package com.vehiclebooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StatusHistoryAdapter extends RecyclerView.Adapter<StatusHistoryAdapter.StatusHistoryViewHolder> {
    
    private List<StatusChange> statusHistory;
    
    public StatusHistoryAdapter(List<StatusChange> statusHistory) {
        this.statusHistory = statusHistory;
    }
    
    @NonNull
    @Override
    public StatusHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_status_history, parent, false);
        return new StatusHistoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull StatusHistoryViewHolder holder, int position) {
        StatusChange statusChange = statusHistory.get(position);
        
        // Set status icon and name
        BookingStatus status = statusChange.getStatus();
        holder.statusIcon.setText(status.getIcon());
        holder.statusName.setText(status.getDisplayName());
        
        // Set timestamp
        holder.timestamp.setText(statusChange.getFormattedTimestamp());
        
        // Set reason/description
        holder.reason.setText(statusChange.getReason());
        
        // Set status color for the icon
        holder.statusIcon.setTextColor(status.getColor());
        
        // Show connector line for all items except the last one
        if (position == statusHistory.size() - 1) {
            holder.connectorLine.setVisibility(View.GONE);
        } else {
            holder.connectorLine.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public int getItemCount() {
        return statusHistory.size();
    }
    
    static class StatusHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView statusIcon;
        TextView statusName;
        TextView timestamp;
        TextView reason;
        View connectorLine;
        
        public StatusHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            statusIcon = itemView.findViewById(R.id.tv_status_icon);
            statusName = itemView.findViewById(R.id.tv_status_name);
            timestamp = itemView.findViewById(R.id.tv_timestamp);
            reason = itemView.findViewById(R.id.tv_reason);
            connectorLine = itemView.findViewById(R.id.view_connector_line);
        }
    }
}