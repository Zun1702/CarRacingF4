package com.example.carracing;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    
    private TextView tvNoHistory, tvStatsSummary;
    private RecyclerView rvHistory;
    private RaceHistoryAdapter historyAdapter;
    private RaceHistoryManager raceHistoryManager;
    private GameAudioManager audioManager;
    private String playerName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        
        initializeViews();
        setupManagers();
        loadPlayerData();
        loadHistory();
        displayStatistics();
    }
    
    private void initializeViews() {
        tvNoHistory = findViewById(R.id.tvNoHistory);
        tvStatsSummary = findViewById(R.id.tvStatsSummary);
        rvHistory = findViewById(R.id.rvHistory);
        
        // Setup RecyclerView
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void setupManagers() {
        raceHistoryManager = RaceHistoryManager.getInstance(this);
        audioManager = GameAudioManager.getInstance(this);
    }
    
    private void loadPlayerData() {
        android.content.SharedPreferences prefs = getSharedPreferences("CarRacingPrefs", MODE_PRIVATE);
        playerName = prefs.getString("player_name", "Player");
    }
    
    private void loadHistory() {
        List<RaceHistory> playerHistory = raceHistoryManager.getPlayerHistory(playerName);
        
        if (playerHistory.isEmpty()) {
            tvNoHistory.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            tvNoHistory.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
            
            historyAdapter = new RaceHistoryAdapter(playerHistory, this);
            rvHistory.setAdapter(historyAdapter);
        }
    }
    
    private void displayStatistics() {
        RaceHistoryManager.PlayerStatistics stats = raceHistoryManager.getPlayerStatistics(playerName);
        
        if (stats.getTotalRaces() > 0) {
            String statsText = String.format(
                "🏁 Total Races: %d\n" +
                "🏆 Wins: %d | 💔 Losses: %d\n" +
                "📊 Win Rate: %s\n" +
                "💰 Net Profit: %s%d coins\n" +
                "🚗 Favorite Car: %s\n" +
                "💎 Biggest Win: %d coins\n" +
                "💸 Biggest Loss: %d coins",
                stats.getTotalRaces(),
                stats.getWins(),
                stats.getLosses(),
                stats.getFormattedWinRate(),
                stats.isProfitable() ? "+" : "",
                stats.getNetProfit(),
                stats.getFavoriteCar().isEmpty() ? "None" : stats.getFavoriteCar(),
                stats.getBiggestWin(),
                stats.getBiggestLoss()
            );
            
            tvStatsSummary.setText(statsText);
            tvStatsSummary.setVisibility(View.VISIBLE);
        } else {
            tvStatsSummary.setVisibility(View.GONE);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (audioManager != null) {
            audioManager.onResume();
        }
        
        // Refresh data when returning to this activity
        loadHistory();
        displayStatistics();
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
        if (audioManager != null) {
            audioManager.playButtonClick();
        }
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}