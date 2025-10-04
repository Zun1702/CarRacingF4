package com.example.carracing;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carracing.utils.CarAnimationUtils;

public class MainActivity extends AppCompatActivity {
    
    private EditText etPlayerName;
    private Button btnStartGame, btnViewHistory, btnViewAchievements;
    private ImageView ivCarDemo1, ivCarDemo2, ivCarDemo3;
    private FrameLayout flSettingsContainer;
    private SharedPreferences sharedPreferences;
    private GameAudioManager audioManager;
    
    private static final String PREFS_NAME = "CarRacingPrefs";
    private static final String KEY_PLAYER_NAME = "player_name";
    private static final String KEY_PLAYER_BALANCE = "player_balance";
    private static final int DEFAULT_BALANCE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initializeViews();
        setupPreferences();
        setupClickListeners();
        startCarAnimations();
        loadSavedPlayerName();
    }
    
    private void initializeViews() {
        etPlayerName = findViewById(R.id.etPlayerName);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnViewAchievements = findViewById(R.id.btnViewAchievements);
        flSettingsContainer = findViewById(R.id.flSettingsContainer);
        ivCarDemo1 = findViewById(R.id.ivCarDemo1);
        ivCarDemo2 = findViewById(R.id.ivCarDemo2);
        ivCarDemo3 = findViewById(R.id.ivCarDemo3);
        
        // Add logos with animations
        ImageView ivF1Logo = findViewById(R.id.ivF1Logo);
        ImageView ivRacingCarLogo = findViewById(R.id.ivRacingCarLogo);
        
        // Add subtle animation to F1 logo
        addLogoAnimation(ivF1Logo);
        
        // Add pulse animation to racing car logo
        addPulseAnimation(ivRacingCarLogo);
        
        // Set enhanced PNG racing cars with animation for all demo cars (display mode)
        CarAnimationUtils.toggleCarAnimation(this, ivCarDemo1, true, 0); // Red car
        CarAnimationUtils.toggleCarAnimation(this, ivCarDemo2, true, 1); // Blue car
        CarAnimationUtils.toggleCarAnimation(this, ivCarDemo3, true, 2); // Green car
    }
    
    private void setupPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        audioManager = GameAudioManager.getInstance(this);
        
        // Initialize balance if first time
        if (!sharedPreferences.contains(KEY_PLAYER_BALANCE)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_PLAYER_BALANCE, DEFAULT_BALANCE);
            editor.apply();
        }
    }
    
    private void setupClickListeners() {
        btnStartGame.setOnClickListener(v -> {
            audioManager.playButtonClick();
            
            String playerName = etPlayerName.getText().toString().trim();
            
            // Enhanced validation with ErrorHandler
            if (!ErrorHandler.validatePlayerName(playerName)) {
                String error = ErrorHandler.getValidationErrorMessage("Player Name", playerName);
                ErrorHandler.showErrorToast(this, error != null ? error : "Invalid player name");
                etPlayerName.requestFocus();
                return;
            }
            
            try {
                savePlayerName(playerName);
                startBettingActivity();
                ErrorHandler.logInfo("Player " + playerName + " started new game");
            } catch (Exception e) {
                ErrorHandler.handleStorageError(this, e);
            }
        });
        
        btnViewHistory.setOnClickListener(v -> {
            audioManager.playButtonClick();
            startHistoryActivity();
        });
        
        btnViewAchievements.setOnClickListener(v -> {
            audioManager.playButtonClick();
            startAchievementsActivity();
        });
        
        flSettingsContainer.setOnClickListener(v -> {
            audioManager.playButtonClick();
            showSettingsPopup();
        });
    }
    
    private void startCarAnimations() {
        // Animate cars moving across screen
        animateCar(ivCarDemo1, 2000, 0);
        animateCar(ivCarDemo2, 2500, 500);
        animateCar(ivCarDemo3, 3000, 1000);
    }
    
    private void animateCar(ImageView carView, long duration, long delay) {
        carView.postDelayed(() -> {
            ObjectAnimator animator = ObjectAnimator.ofFloat(carView, "translationX", -200f, 200f);
            animator.setDuration(duration);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.setRepeatMode(ObjectAnimator.RESTART);
            animator.start();
        }, delay);
    }
    
    private void savePlayerName(String playerName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PLAYER_NAME, playerName);
        editor.apply();
    }
    
    private void loadSavedPlayerName() {
        String savedName = sharedPreferences.getString(KEY_PLAYER_NAME, "");
        if (!TextUtils.isEmpty(savedName)) {
            etPlayerName.setText(savedName);
        }
    }
    
    private void startBettingActivity() {
        Intent intent = new Intent(this, BettingActivity.class);
        startActivity(intent);
        
        // Add transition animation
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    
    private void startHistoryActivity() {
        String playerName = etPlayerName.getText().toString().trim();
        if (TextUtils.isEmpty(playerName)) {
            playerName = "Player";
        }
        
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra("player_name", playerName);
        startActivity(intent);
        
        // Add transition animation
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    
    private void startAchievementsActivity() {
        try {
            Intent intent = new Intent(this, AchievementsActivity.class);
            startActivity(intent);
            
            // Add transition animation
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } catch (Exception e) {
            ErrorHandler.logError("AchievementsActivity", e);
            ErrorHandler.showErrorToast(this, "Failed to open achievements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showSettingsPopup() {
        // Create popup dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view.View popupView = getLayoutInflater().inflate(R.layout.popup_settings, null);
        builder.setView(popupView);
        
        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Initialize popup views
        android.widget.Switch switchSound = popupView.findViewById(R.id.switchSound);
        android.widget.Switch switchMusic = popupView.findViewById(R.id.switchMusic);
        android.widget.Button btnResetGamePopup = popupView.findViewById(R.id.btnResetGamePopup);
        android.widget.Button btnCloseSettings = popupView.findViewById(R.id.btnCloseSettings);
        
        // Load current audio settings
        switchSound.setChecked(audioManager.isSoundEnabled());
        switchMusic.setChecked(audioManager.isMusicEnabled());
        
        // Setup click listeners
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            audioManager.playButtonClick();
            audioManager.setSoundEnabled(isChecked);
        });
        
        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            audioManager.playButtonClick();
            audioManager.setMusicEnabled(isChecked);
        });
        
        btnResetGamePopup.setOnClickListener(v -> {
            audioManager.playButtonClick();
            dialog.dismiss();
            showResetConfirmationDialog();
        });
        
        btnCloseSettings.setOnClickListener(v -> {
            audioManager.playButtonClick();
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private void showResetConfirmationDialog() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("🚨 Reset Game")
            .setMessage("Are you sure you want to reset the game?\n\n⚠️ This will:\n• Reset coins to 1000\n• Clear all statistics\n• Delete all achievements\n• Clear race history\n\nThis action cannot be undone!")
            .setPositiveButton("Reset", (dialog, which) -> {
                audioManager.playButtonClick();
                resetGameData();
            })
            .setNegativeButton("Cancel", (dialog, which) -> {
                audioManager.playButtonClick();
                dialog.dismiss();
            })
            .setCancelable(true)
            .show();
    }
    
    private void resetGameData() {
        try {
            // Reset main game preferences (CarRacingPrefs)
            SharedPreferences mainPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor mainEditor = mainPrefs.edit();
            mainEditor.clear();
            mainEditor.putInt(KEY_PLAYER_BALANCE, DEFAULT_BALANCE); // Reset to 1000 coins
            mainEditor.apply();
            
            // Reset game data preferences (used by BettingActivity and others)
            SharedPreferences gamePrefs = getSharedPreferences("game_data", MODE_PRIVATE);
            SharedPreferences.Editor gameEditor = gamePrefs.edit();
            gameEditor.clear();
            gameEditor.putInt(KEY_PLAYER_BALANCE, DEFAULT_BALANCE);
            gameEditor.apply();
            
            // Reset race history using manager instance
            RaceHistoryManager historyManager = RaceHistoryManager.getInstance(this);
            historyManager.clearHistory();
            
            // Reset race history preferences as well (double safety)
            SharedPreferences historyPrefs = getSharedPreferences("RaceHistoryPrefs", MODE_PRIVATE);
            SharedPreferences.Editor historyEditor = historyPrefs.edit();
            historyEditor.clear();
            historyEditor.apply();
            
            // Reset achievements using manager instance 
            AchievementManager achievementManager = AchievementManager.getInstance(this);
            achievementManager.resetAllAchievements();
            
            // Reset audio preferences
            SharedPreferences audioPrefs = getSharedPreferences("GameAudioPrefs", MODE_PRIVATE);
            SharedPreferences.Editor audioEditor = audioPrefs.edit();
            audioEditor.clear();
            audioEditor.apply();
            
            // Clear player name input
            etPlayerName.setText("");
            
            // Show success message
            android.widget.Toast.makeText(this, "🎆 Game Reset Successfully!\n💰 Coins: 1000\n🏆 Achievements: Reset\n📊 History: Cleared", android.widget.Toast.LENGTH_LONG).show();
            
            ErrorHandler.logInfo("Game data reset successfully - All preferences cleared, achievements reset, history cleared, balance set to 1000");
            
        } catch (Exception e) {
            ErrorHandler.handleStorageError(this, e);
            android.widget.Toast.makeText(this, "❌ Failed to reset game data", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Restart animations when coming back to this activity
        startCarAnimations();
        
        // Resume audio
        if (audioManager != null) {
            audioManager.onResume();
            audioManager.playBackgroundMusic();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Pause audio
        if (audioManager != null) {
            audioManager.onPause();
        }
    }
    
    /**
     * Add subtle rotation animation to F1 logo
     */
    private void addLogoAnimation(ImageView logoView) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(logoView, "rotation", 0f, 5f, 0f, -5f, 0f);
        rotation.setDuration(3000);
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        rotation.start();
    }
    
    /**
     * Add pulse animation to racing car logo
     */
    private void addPulseAnimation(ImageView logoView) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logoView, "scaleX", 1.0f, 1.1f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logoView, "scaleY", 1.0f, 1.1f, 1.0f);
        
        scaleX.setDuration(2000);
        scaleY.setDuration(2000);
        
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        
        scaleX.start();
        scaleY.start();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Release audio resources
        if (audioManager != null) {
            audioManager.onDestroy();
        }
    }
}