package com.vehiclebooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying popular routes in analytics dashboard
 */
public class RouteAnalyticsAdapter extends RecyclerView.Adapter<RouteAnalyticsAdapter.RouteViewHolder> {
    
    private List<BookingAnalytics.RouteStats> routes = new ArrayList<>();
    
    public void updateRoutes(List<BookingAnalytics.RouteStats> newRoutes) {
        this.routes.clear();
        this.routes.addAll(newRoutes);
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route_analytics, parent, false);
        return new RouteViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        BookingAnalytics.RouteStats routeStats = routes.get(position);
        holder.bind(routeStats, position + 1);
    }
    
    @Override
    public int getItemCount() {
        return routes.size();
    }
    
    static class RouteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRank;
        private TextView tvRoute;
        private TextView tvCount;
        private TextView tvPercentage;
        private TextView tvStatus;
        
        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvRoute = itemView.findViewById(R.id.tv_route);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
        
        public void bind(BookingAnalytics.RouteStats routeStats, int rank) {
            tvRank.setText(String.valueOf(rank));
            tvRoute.setText(routeStats.route);
            tvCount.setText(String.valueOf(routeStats.count));
            tvPercentage.setText(String.format(Locale.getDefault(), "%.1f%%", routeStats.percentage));
            
            // Show most common status for this route
            if (routeStats.mostCommonStatus != null) {
                tvStatus.setText(routeStats.mostCommonStatus.getDisplayName());
                tvStatus.setBackgroundTintList(
                    itemView.getContext().getColorStateList(routeStats.mostCommonStatus.getColorRes()));
            }
        }
    }
}