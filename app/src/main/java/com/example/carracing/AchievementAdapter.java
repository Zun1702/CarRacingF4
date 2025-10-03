package com.example.carracing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {
    
    private List<Achievement> achievements;
    
    public AchievementAdapter(List<Achievement> achievements) {
        this.achievements = achievements;
    }
    
    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.bind(achievement);
    }
    
    @Override
    public int getItemCount() {
        return achievements.size();
    }
    
    public void updateAchievements(List<Achievement> newAchievements) {
        this.achievements = newAchievements;
        notifyDataSetChanged();
    }
    
    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDescription, tvProgress, tvReward, tvUnlockDate;
        private ProgressBar progressBar;
        
        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            tvReward = itemView.findViewById(R.id.tvReward);
            tvUnlockDate = itemView.findViewById(R.id.tvUnlockDate);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
        
        public void bind(Achievement achievement) {
            tvName.setText(achievement.getDisplayName());
            tvDescription.setText(achievement.getDescription());
            
            if (achievement.isUnlocked()) {
                // Unlocked achievement
                tvProgress.setText("COMPLETED!");
                tvProgress.setTextColor(itemView.getContext().getResources().getColor(R.color.win_green));
                
                tvUnlockDate.setText("Unlocked: " + achievement.getFormattedUnlockDate());
                tvUnlockDate.setVisibility(View.VISIBLE);
                
                progressBar.setProgress(100);
                progressBar.setProgressTintList(itemView.getContext().getResources().getColorStateList(R.color.win_green));
                
                // Highlight the entire item
                itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.win_background));
                
            } else {
                // Locked achievement
                tvProgress.setText(achievement.getProgressText() + " (" + 
                                 String.format("%.0f%%", achievement.getProgressPercentage()) + ")");
                tvProgress.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary));
                
                tvUnlockDate.setVisibility(View.GONE);
                
                progressBar.setProgress((int) achievement.getProgressPercentage());
                progressBar.setProgressTintList(itemView.getContext().getResources().getColorStateList(R.color.accent_yellow));
                
                // Normal background
                itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.card_background));
            }
            
            // Show reward if available
            if (achievement.getRewardCoins() > 0) {
                tvReward.setText("💰 " + achievement.getRewardCoins() + " coins");
                tvReward.setVisibility(View.VISIBLE);
                if (achievement.isUnlocked()) {
                    tvReward.setTextColor(itemView.getContext().getResources().getColor(R.color.win_green));
                } else {
                    tvReward.setTextColor(itemView.getContext().getResources().getColor(R.color.accent_yellow));
                }
            } else {
                tvReward.setVisibility(View.GONE);
            }
            
            // Adjust opacity for locked achievements
            float alpha = achievement.isUnlocked() ? 1.0f : 0.7f;
            itemView.setAlpha(alpha);
        }
    }
}