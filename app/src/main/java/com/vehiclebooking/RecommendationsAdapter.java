package com.vehiclebooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying smart recommendations in analytics dashboard
 */
public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.RecommendationViewHolder> {
    
    private List<String> recommendations = new ArrayList<>();
    
    public void updateRecommendations(List<String> newRecommendations) {
        this.recommendations.clear();
        this.recommendations.addAll(newRecommendations);
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommendation, parent, false);
        return new RecommendationViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        String recommendation = recommendations.get(position);
        holder.bind(recommendation);
    }
    
    @Override
    public int getItemCount() {
        return recommendations.size();
    }
    
    static class RecommendationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRecommendation;
        
        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecommendation = itemView.findViewById(R.id.tv_recommendation);
        }
        
        public void bind(String recommendation) {
            tvRecommendation.setText(recommendation);
        }
    }
}