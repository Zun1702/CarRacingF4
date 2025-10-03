package com.example.carracing;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    
    private TextView tvTotalRaces, tvWinRate, tvTotalBet, tvTotalWinnings, tvCurrentStreak;
    private TextView tvFavoriteCar, tvBestWin, tvWorstLoss, tvAverageWin;
    private RecyclerView rvRaceHistory;
    private Button btnBack, btnClearHistory;
    
    private RaceHistoryManager raceHistoryManager;
    private GameAudioManager audioManager;
    private RaceHistoryAdapter historyAdapter;
    private String playerName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        
        playerName = getIntent().getStringExtra("player_name");
        if (playerName == null) playerName = "Player";
        
        initializeViews();
        setupManagers();
        setupRecyclerView();
        loadStatistics();
        setupClickListeners();
    }
    
    private void initializeViews() {
        // Statistics TextViews
        tvTotalRaces = findViewById(R.id.tvTotalRaces);
        tvWinRate = findViewById(R.id.tvWinRate);
        tvTotalBet = findViewById(R.id.tvTotalBet);
        tvTotalWinnings = findViewById(R.id.tvTotalWinnings);
        tvCurrentStreak = findViewById(R.id.tvCurrentStreak);
        tvFavoriteCar = findViewById(R.id.tvFavoriteCar);
        tvBestWin = findViewById(R.id.tvBestWin);
        tvWorstLoss = findViewById(R.id.tvWorstLoss);
        tvAverageWin = findViewById(R.id.tvAverageWin);
        
        // RecyclerView and Buttons
        rvRaceHistory = findViewById(R.id.rvRaceHistory);
        btnBack = findViewById(R.id.btnBack);
        btnClearHistory = findViewById(R.id.btnClearHistory);
    }
    
    private void setupManagers() {
        raceHistoryManager = RaceHistoryManager.getInstance(this);
        audioManager = GameAudioManager.getInstance(this);
    }
    
    private void setupRecyclerView() {
        rvRaceHistory.setLayoutManager(new LinearLayoutManager(this));
        List<RaceHistory> history = raceHistoryManager.getPlayerHistory(playerName);
        historyAdapter = new RaceHistoryAdapter(history, this);
        rvRaceHistory.setAdapter(historyAdapter);
    }
    
    private void loadStatistics() {
        RaceHistoryManager.PlayerStatistics stats = raceHistoryManager.getPlayerStatistics(playerName);
        
        // Basic statistics
        tvTotalRaces.setText("Total Races: " + stats.getTotalRaces());
        tvWinRate.setText(String.format("Win Rate: %.1f%%", stats.getWinRate()));
        tvTotalBet.setText("Total Bet: " + stats.getTotalBetAmount() + " coins");
        tvTotalWinnings.setText("Total Winnings: " + stats.getTotalWinnings() + " coins");
        
        // Current streak - simplified for now (can enhance later)
        int losses = stats.getLosses();
        if (stats.getWins() > losses) {
            tvCurrentStreak.setText("🔥 More Wins: " + stats.getWins());
            tvCurrentStreak.setTextColor(getResources().getColor(R.color.win_green));
        } else {
            tvCurrentStreak.setText("Record: " + stats.getWins() + "W-" + losses + "L");
            tvCurrentStreak.setTextColor(getResources().getColor(R.color.text_secondary));
        }
        
        // Advanced statistics
        tvFavoriteCar.setText("Favorite Car: " + stats.getFavoriteCar());
        tvBestWin.setText("Best Win: " + stats.getBiggestWin() + " coins");
        tvWorstLoss.setText("Worst Loss: " + stats.getBiggestLoss() + " coins");
        double avgWin = stats.getTotalRaces() > 0 ? (double)stats.getTotalWinnings() / stats.getTotalRaces() : 0;
        tvAverageWin.setText(String.format("Average Win: %.1f coins", avgWin));
        
        // Update colors based on profit/loss
        int netProfit = stats.getNetProfit();
        if (netProfit > 0) {
            tvTotalWinnings.setTextColor(getResources().getColor(R.color.win_green));
        } else if (netProfit < 0) {
            tvTotalWinnings.setTextColor(getResources().getColor(R.color.lose_red));
        }
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            audioManager.playButtonClick();
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
        
        btnClearHistory.setOnClickListener(v -> {
            audioManager.playButtonClick();
            showClearHistoryConfirmation();
        });
    }
    
    private void showClearHistoryConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Clear History")
               .setMessage("Are you sure you want to clear all race history? This action cannot be undone.")
               .setPositiveButton("Clear All", (dialog, which) -> {
                   raceHistoryManager.clearPlayerHistory(playerName);
                   refreshData();
                   audioManager.playShortSound(GameAudioManager.SoundType.SUCCESS);
               })
               .setNegativeButton("Cancel", (dialog, which) -> {
                   audioManager.playButtonClick();
               })
               .show();
    }
    
    private void refreshData() {
        loadStatistics();
        List<RaceHistory> newHistory = raceHistoryManager.getPlayerHistory(playerName);
        historyAdapter.updateHistory(newHistory);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (audioManager != null) {
            audioManager.onResume();
        }
        // Refresh data in case it was updated from other activities
        refreshData();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (audioManager != null) {
            audioManager.onPause();
        }
    }
    
    @Override
    public void onBackPressed() {
        audioManager.playButtonClick();
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}