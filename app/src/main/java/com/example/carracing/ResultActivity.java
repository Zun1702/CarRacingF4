package com.example.carracing;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ResultActivity extends AppCompatActivity {
    
    private TextView tvResultType, tvWinnerAnnouncement, tvBetResult, tvBalanceUpdate;
    private ImageView ivResultIcon;
    private Button btnPlayAgain, btnMainMenu;
    
    private String playerName;
    private String winnerCarName;
    private String selectedCarName;
    private boolean playerWon;
    private int betAmount;
    private int winnings;
    private int newBalance;
    
    private SharedPreferences sharedPreferences;
    private GameAudioManager audioManager;
    private RaceHistoryManager raceHistoryManager;
    private AchievementManager achievementManager;
    private static final String PREFS_NAME = "CarRacingPrefs";
    private static final String KEY_PLAYER_BALANCE = "player_balance";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        getIntentData();
        initializeViews();
        setupPreferences();
        updateBalance();
        displayResults();
        setupClickListeners();
        startAnimations();
    }
    
    private void getIntentData() {
        Intent intent = getIntent();
        playerName = intent.getStringExtra("player_name");
        winnerCarName = intent.getStringExtra("winner_car_name");
        selectedCarName = intent.getStringExtra("selected_car_name");
        playerWon = intent.getBooleanExtra("player_won", false);
        betAmount = intent.getIntExtra("bet_amount", 0);
        winnings = intent.getIntExtra("winnings", 0);
        newBalance = intent.getIntExtra("new_balance", 1000);
    }
    
    private void initializeViews() {
        tvResultType = findViewById(R.id.tvResultType);
        tvWinnerAnnouncement = findViewById(R.id.tvWinnerAnnouncement);
        tvBetResult = findViewById(R.id.tvBetResult);
        tvBalanceUpdate = findViewById(R.id.tvBalanceUpdate);
        ivResultIcon = findViewById(R.id.ivResultIcon);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnMainMenu = findViewById(R.id.btnMainMenu);
    }
    
    private void setupPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        audioManager = GameAudioManager.getInstance(this);
        raceHistoryManager = RaceHistoryManager.getInstance(this);
        achievementManager = AchievementManager.getInstance(this);
    }
    
    private void updateBalance() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_PLAYER_BALANCE, newBalance);
        editor.apply();
        
        // Save race result to history
        saveRaceToHistory();
    }
    
    private void saveRaceToHistory() {
        raceHistoryManager.addRaceResult(
            playerName,
            selectedCarName,
            winnerCarName,
            betAmount,
            winnings,
            playerWon,
            newBalance
        );
        
        // Process achievements after adding race to history
        processAchievements();
    }
    
    private void processAchievements() {
        try {
            // Get updated statistics and recent history
            RaceHistoryManager.PlayerStatistics stats = raceHistoryManager.getPlayerStatistics(playerName);
            List<RaceHistory> recentHistory = raceHistoryManager.getPlayerHistory(playerName);
            
            if (!recentHistory.isEmpty()) {
                RaceHistory lastRace = recentHistory.get(0); // Most recent race
                
                // Process achievements
                List<Achievement> newAchievements = achievementManager.processRaceResult(stats, lastRace, recentHistory);
                
                // Show achievement notifications if any were unlocked
                if (!newAchievements.isEmpty()) {
                    showAchievementNotifications(newAchievements);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAchievementNotifications(List<Achievement> newAchievements) {
        for (Achievement achievement : newAchievements) {
            String message = "🏆 Achievement Unlocked!\n" + 
                           achievement.getDisplayName() + "\n" +
                           achievement.getDescription();
            
            if (achievement.getRewardCoins() > 0) {
                message += "\n💰 Reward: " + achievement.getRewardCoins() + " coins";
                
                // Add reward to balance
                newBalance += achievement.getRewardCoins();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(KEY_PLAYER_BALANCE, newBalance);
                editor.apply();
                
                // Update balance display
                updateBalanceDisplay();
            }
            
            // Show achievement dialog
            showAchievementDialog(achievement);
        }
    }
    
    private void showAchievementDialog(Achievement achievement) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("🏆 Achievement Unlocked!")
               .setMessage(achievement.getDisplayName() + "\n\n" + 
                          achievement.getDescription() + 
                          (achievement.getRewardCoins() > 0 ? 
                           "\n\n💰 Reward: " + achievement.getRewardCoins() + " coins" : ""))
               .setPositiveButton("Awesome!", (dialog, which) -> {
                   audioManager.playButtonClick();
                   dialog.dismiss();
               })
               .setCancelable(false);
        
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
        
        // Style the dialog button
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
              .setTextColor(getResources().getColor(R.color.accent_yellow));
    }
    
    private void updateBalanceDisplay() {
        tvBalanceUpdate.setText("New Balance: " + newBalance + " coins");
    }
    
    private void displayResults() {
        // Display winner
        tvWinnerAnnouncement.setText(String.format(getString(R.string.winner_announcement), winnerCarName));
        
        if (playerWon) {
            // Player won
            tvResultType.setText("🎉 CONGRATULATIONS! 🎉");
            tvResultType.setTextColor(getResources().getColor(R.color.win_green));
            
            tvBetResult.setText(String.format(getString(R.string.you_won), winnings));
            tvBetResult.setTextColor(getResources().getColor(R.color.win_green));
            
            ivResultIcon.setImageResource(android.R.drawable.star_big_on);
            ivResultIcon.setColorFilter(getResources().getColor(R.color.accent_yellow));
            
        } else {
            // Player lost
            tvResultType.setText("😢 BETTER LUCK NEXT TIME");
            tvResultType.setTextColor(getResources().getColor(R.color.lose_red));
            
            tvBetResult.setText(String.format(getString(R.string.you_lost), betAmount));
            tvBetResult.setTextColor(getResources().getColor(R.color.lose_red));
            
            ivResultIcon.setImageResource(android.R.drawable.ic_delete);
            ivResultIcon.setColorFilter(getResources().getColor(R.color.lose_red));
        }
        
        // Display balance update
        tvBalanceUpdate.setText("New Balance: " + newBalance + " coins");
        tvBalanceUpdate.setTextColor(newBalance > 0 ? 
            getResources().getColor(R.color.win_green) : 
            getResources().getColor(R.color.warning_orange));
        
        // Show additional information
        String detailText = "Your bet: " + betAmount + " coins on " + selectedCarName + "\n" +
                           "Race winner: " + winnerCarName;
        
        // You can add this to a details TextView if you create one in the layout
    }
    
    private void setupClickListeners() {
        btnPlayAgain.setOnClickListener(v -> {
            audioManager.playButtonClick();
            if (newBalance >= 10) { // Minimum bet check
                playAgain();
            } else {
                // Show insufficient funds dialog or reset balance
                showInsufficientFundsDialog();
            }
        });
        
        btnMainMenu.setOnClickListener(v -> {
            audioManager.playButtonClick();
            goToMainMenu();
        });
    }
    
    private void startAnimations() {
        // Animate result icon
        ObjectAnimator iconAnimation = ObjectAnimator.ofFloat(ivResultIcon, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator iconAnimationY = ObjectAnimator.ofFloat(ivResultIcon, "scaleY", 0f, 1.2f, 1f);
        iconAnimation.setDuration(1000);
        iconAnimationY.setDuration(1000);
        iconAnimation.setInterpolator(new BounceInterpolator());
        iconAnimationY.setInterpolator(new BounceInterpolator());
        
        // Animate result text
        ObjectAnimator textAnimation = ObjectAnimator.ofFloat(tvResultType, "alpha", 0f, 1f);
        textAnimation.setDuration(1500);
        textAnimation.setStartDelay(500);
        
        // Start animations
        iconAnimation.start();
        iconAnimationY.start();
        textAnimation.start();
        
        // Animate buttons with delay
        new Handler().postDelayed(() -> {
            ObjectAnimator buttonAnimation1 = ObjectAnimator.ofFloat(btnPlayAgain, "translationY", 100f, 0f);
            ObjectAnimator buttonAnimation2 = ObjectAnimator.ofFloat(btnMainMenu, "translationY", 100f, 0f);
            buttonAnimation1.setDuration(800);
            buttonAnimation2.setDuration(800);
            buttonAnimation2.setStartDelay(200);
            
            btnPlayAgain.setVisibility(View.VISIBLE);
            btnMainMenu.setVisibility(View.VISIBLE);
            
            buttonAnimation1.start();
            buttonAnimation2.start();
        }, 1000);
    }
    
    private void playAgain() {
        Intent intent = new Intent(this, BettingActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
    
    private void goToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
    
    private void showInsufficientFundsDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Insufficient Funds")
               .setMessage("You don't have enough coins to place another bet. Would you like to reset your balance to 1000 coins?")
               .setPositiveButton("Reset Balance", (dialog, which) -> {
                   resetBalance();
                   playAgain();
               })
               .setNegativeButton("Main Menu", (dialog, which) -> goToMainMenu())
               .setCancelable(false)
               .show();
    }
    
    private void resetBalance() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_PLAYER_BALANCE, 1000);
        editor.apply();
        newBalance = 1000;
        tvBalanceUpdate.setText("New Balance: 1000 coins (Reset)");
        tvBalanceUpdate.setTextColor(getResources().getColor(R.color.win_green));
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
        audioManager.playButtonClick();
        goToMainMenu();
    }
}