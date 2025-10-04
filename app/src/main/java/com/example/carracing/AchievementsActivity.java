package com.example.carracing;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {
    
    private TextView tvAchievementStats, tvNoAchievements;
    private RecyclerView rvAchievements;
    private android.widget.ImageButton btnBackToHome;
    private AchievementAdapter achievementAdapter;
    private AchievementManager achievementManager;
    private GameAudioManager audioManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_achievements);
            
            initializeViews();
            setupManagers();
            loadAchievements();
            displayStats();
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("AchievementsActivity", "Error in onCreate: " + e.getMessage());
            finish(); // Close activity if there's an error
        }
    }
    
    private void initializeViews() {
        tvAchievementStats = findViewById(R.id.tvAchievementStats);
        tvNoAchievements = findViewById(R.id.tvNoAchievements);
        rvAchievements = findViewById(R.id.rvAchievements);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        
        // Setup RecyclerView
        rvAchievements.setLayoutManager(new LinearLayoutManager(this));
        
        // Setup back button
        btnBackToHome.setOnClickListener(v -> {
            audioManager.playButtonClick();
            finish(); // Go back to previous activity (MainActivity)
        });
    }
    
    private void setupManagers() {
        achievementManager = AchievementManager.getInstance(this);
        audioManager = GameAudioManager.getInstance(this);
    }
    
    private void loadAchievements() {
        List<Achievement> allAchievements = achievementManager.getAllAchievements();
        
        if (allAchievements.isEmpty()) {
            tvNoAchievements.setVisibility(View.VISIBLE);
            rvAchievements.setVisibility(View.GONE);
        } else {
            tvNoAchievements.setVisibility(View.GONE);
            rvAchievements.setVisibility(View.VISIBLE);
            
            achievementAdapter = new AchievementAdapter(allAchievements);
            rvAchievements.setAdapter(achievementAdapter);
        }
    }
    
    private void displayStats() {
        AchievementManager.AchievementStats stats = achievementManager.getStats();
        
        String statsText = String.format(
            "🏆 Achievement Progress\n\n" +
            "📊 Completed: %d/%d (%s)\n" +
            "💰 Total Rewards Earned: %d coins\n" +
            "🎯 Locked Achievements: %d",
            stats.getUnlockedAchievements(),
            stats.getTotalAchievements(),
            stats.getFormattedCompletion(),
            stats.getTotalRewards(),
            stats.getLockedAchievements()
        );
        
        tvAchievementStats.setText(statsText);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (audioManager != null) {
            audioManager.onResume();
        }
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