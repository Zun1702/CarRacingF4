package com.example.carracing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RaceHistoryAdapter extends RecyclerView.Adapter<RaceHistoryAdapter.HistoryViewHolder> {
    
    private List<RaceHistory> historyList;
    private Context context;
    private SimpleDateFormat dateFormat;
    
    public RaceHistoryAdapter(List<RaceHistory> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_race_history, parent, false);
        return new HistoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        RaceHistory race = historyList.get(position);
        
        // Format date
        String formattedDate = dateFormat.format(new Date(race.getTimestamp()));
        holder.tvDate.setText(formattedDate);
        
        // Car information
        holder.tvSelectedCar.setText("Bet on: " + race.getSelectedCarName());
        holder.tvWinner.setText("Winner: " + race.getWinnerCarName());
        
        // Bet amount
        holder.tvBetAmount.setText("Bet: " + race.getBetAmount() + " coins");
        
        // Result with enhanced styling
        if (race.isPlayerWon()) {
            holder.tvResult.setText("🎉 WON");
            holder.tvResult.setTextColor(context.getResources().getColor(R.color.win_green));
            holder.tvBalance.setText("+" + race.getWinnings() + " coins");
            holder.tvBalance.setTextColor(context.getResources().getColor(R.color.win_green));
            
            if (holder.ivResultIcon != null) {
                holder.ivResultIcon.setImageResource(android.R.drawable.star_big_on);
                holder.ivResultIcon.setColorFilter(context.getResources().getColor(R.color.accent_yellow));
            }
        } else {
            holder.tvResult.setText("😢 LOST");
            holder.tvResult.setTextColor(context.getResources().getColor(R.color.lose_red));
            holder.tvBalance.setText("-" + race.getBetAmount() + " coins");
            holder.tvBalance.setTextColor(context.getResources().getColor(R.color.lose_red));
            
            if (holder.ivResultIcon != null) {
                holder.ivResultIcon.setImageResource(android.R.drawable.ic_delete);
                holder.ivResultIcon.setColorFilter(context.getResources().getColor(R.color.lose_red));
            }
        }
        
        // Add alternating background colors for better readability
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
        }
    }
    
    @Override
    public int getItemCount() {
        return historyList.size();
    }
    
    public void updateHistory(List<RaceHistory> newHistory) {
        this.historyList = newHistory;
        notifyDataSetChanged();
    }
    
    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvSelectedCar, tvWinner, tvBetAmount, tvResult, tvBalance;
        ImageView ivResultIcon;
        
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvSelectedCar = itemView.findViewById(R.id.tvSelectedCar);
            tvWinner = itemView.findViewById(R.id.tvWinner);
            tvBetAmount = itemView.findViewById(R.id.tvBetAmount);
            tvResult = itemView.findViewById(R.id.tvResult);
            tvBalance = itemView.findViewById(R.id.tvBalance);
            ivResultIcon = itemView.findViewById(R.id.ivResultIcon);
        }
    }
}