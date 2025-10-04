package com.example.carracing;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carracing.utils.CarAnimationUtils;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    
    private List<Car> carList;
    private OnCarClickListener listener;
    private int selectedPosition = -1;
    
    // Multi-betting support
    private boolean isMultiBettingMode = false;
    private List<Integer> multiBetCarIds = new java.util.ArrayList<>();
    
    public interface OnCarClickListener {
        void onCarClick(int carId);
    }
    
    public CarAdapter(List<Car> carList, OnCarClickListener listener) {
        this.carList = carList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);
        
        holder.tvCarName.setText(car.getName());
        holder.tvCarStats.setText("Speed: " + car.getBaseSpeed() + " | Odds: " + car.getOdds() + "x");
        
        // Use display effects for all cars in car selection with correct color
        CarAnimationUtils.toggleCarAnimation(holder.itemView.getContext(), holder.ivCarImage, true, car.getId());
        
        // Handle selection based on mode
        boolean isSelected = isMultiBettingMode ? 
            multiBetCarIds.contains(car.getId()) : 
            position == selectedPosition;
            
        if (isSelected) {
            holder.vSelectionIndicator.setVisibility(View.VISIBLE);
            holder.cardView.setCardElevation(12f);
            
            // Show different indicators for multi-betting
            if (isMultiBettingMode) {
                holder.vSelectionIndicator.setBackgroundColor(
                    holder.itemView.getContext().getResources().getColor(R.color.accent_yellow));
                // Show bet amount if available
                int betIndex = multiBetCarIds.indexOf(car.getId());
                if (betIndex != -1 && holder.tvBetAmount != null) {
                    holder.tvBetAmount.setVisibility(View.VISIBLE);
                    holder.tvBetAmount.setText("💰 Bet placed!");
                }
            } else {
                holder.vSelectionIndicator.setBackgroundColor(
                    holder.itemView.getContext().getResources().getColor(R.color.win_green));
                if (holder.tvBetAmount != null) {
                    holder.tvBetAmount.setVisibility(View.GONE);
                }
            }
            
            animateCarSelection(holder, true);
        } else {
            holder.vSelectionIndicator.setVisibility(View.INVISIBLE);
            holder.cardView.setCardElevation(6f);
            if (holder.tvBetAmount != null) {
                holder.tvBetAmount.setVisibility(View.GONE);
            }
            animateCarSelection(holder, false);
        }
        
        holder.itemView.setOnClickListener(v -> {
            // Add click animation
            animateCarClick(holder);
            
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) {
                return; // Item was removed, ignore click
            }
            
            int previousPosition = selectedPosition;
            selectedPosition = currentPosition;
            
            // Refresh the previously selected item and current item
            if (previousPosition != -1) {
                notifyItemChanged(previousPosition);
            }
            notifyItemChanged(currentPosition);
            
            if (listener != null) {
                Car clickedCar = carList.get(currentPosition);
                listener.onCarClick(clickedCar.getId());
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return carList.size();
    }
    
    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        
        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        if (position != -1) {
            notifyItemChanged(position);
        }
    }
    
    public void setMultiBettingMode(boolean isMultiBettingMode) {
        this.isMultiBettingMode = isMultiBettingMode;
        // Clear selection when switching modes
        if (isMultiBettingMode) {
            selectedPosition = -1;
        } else {
            multiBetCarIds.clear();
        }
        notifyDataSetChanged();
    }
    
    public void setMultiBetData(List<Integer> multiBetCarIds) {
        this.multiBetCarIds = new java.util.ArrayList<>(multiBetCarIds);
        notifyDataSetChanged();
    }
    
    private void animateCarSelection(CarViewHolder holder, boolean isSelected) {
        float targetScale = isSelected ? 1.05f : 1.0f;
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.cardView, "scaleX", targetScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.cardView, "scaleY", targetScale);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(holder.ivCarImage, "rotation", 
                isSelected ? 5f : 0f);
        
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotation);
        animatorSet.setDuration(200);
        animatorSet.start();
    }
    
    private void animateCarClick(CarViewHolder holder) {
        // Quick pulse animation on click
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(holder.cardView, "scaleX", 0.95f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(holder.cardView, "scaleY", 0.95f);
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(holder.cardView, "scaleX", 1.0f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(holder.cardView, "scaleY", 1.0f);
        
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.playTogether(scaleDownX, scaleDownY);
        scaleDown.setDuration(100);
        
        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(scaleUpX, scaleUpY);
        scaleUp.setDuration(100);
        
        scaleDown.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                scaleUp.start();
            }
        });
        
        scaleDown.start();
    }
    
    static class CarViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivCarImage;
        TextView tvCarName;
        TextView tvCarStats;
        TextView tvBetAmount;
        View vSelectionIndicator;
        
        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivCarImage = itemView.findViewById(R.id.ivCarImage);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvCarStats = itemView.findViewById(R.id.tvCarStats);
            tvBetAmount = itemView.findViewById(R.id.tvBetAmount);
            vSelectionIndicator = itemView.findViewById(R.id.vSelectionIndicator);
        }
    }
}