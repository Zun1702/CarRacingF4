package com.example.carracing;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carracing.utils.CarAnimationUtils;
import com.example.carracing.utils.SoundManager;
import com.example.carracing.utils.SoundManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RacingActivity extends AppCompatActivity {
    
    private SoundManager soundManager;
    private TextView tvRaceTitle, tvCountdown, tvRaceInfo, tvSkipInstruction;
    private ImageView[] carImageViews;
    private ImageView[] smokeImageViews; // For exhaust smoke effects
    private ProgressBar[] progressBars;
    private TextView[] positionTexts;
    
    // Pause functionality
    private ImageButton btnPause;
    private LinearLayout pauseOverlay;
    private Button btnResume, btnRestartRace, btnExitToMenu;
    private Switch switchSoundEffects, switchBackgroundMusic;
    
    private String playerName;
    private int playerBalance;
    private int betAmount;
    private int selectedCarId;
    private String selectedCarName;
    private float selectedCarOdds;
    
    // Lane mapping: Cars stay in their original lanes based on color
    // Car array:    0=Red, 1=Blue,  2=Green, 3=Orange, 4=Purple
    // Lane mapping: 0=Red, 1=Blue,  2=Green, 3=Orange, 4=Purple (same as car array)
    private int[] laneToCarMapping = {0, 1, 2, 3, 4}; // Lane index -> Car array index (same)
    private int[] carToLaneMapping = {0, 1, 2, 3, 4}; // Car index -> Lane index (same)
    
    // Helper method to get lane index from car index
    private int getLaneFromCarIndex(int carIndex) {
        return carToLaneMapping[carIndex];
    }
    
    // Multi-betting support
    private boolean isMultiBet = false;
    private List<Integer> betCarIds;
    private List<String> betCarNames;
    private List<Integer> betAmounts;
    private List<Float> betOdds;
    
    private List<Car> racingCars;
    private Handler raceHandler;
    private Runnable raceRunnable;
    private Random random;
    private GameAudioManager audioManager;
    
    private boolean raceFinished = false;
    private boolean raceSkipped = false;
    private boolean raceStarted = false;
    private boolean racePaused = false;
    private int winnerCarId = -1;
    private final int RACE_DISTANCE = 1000; // pixels
    private final int RACE_UPDATE_INTERVAL = 30; // milliseconds - faster updates for 30s race
    private final int COLLISION_THRESHOLD = 20; // pixels for collision detection
    private final int CAR_LENGTH = 30; // pixels
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_racing);
        
        // Initialize Sound Manager
        soundManager = SoundManager.getInstance(this);
        
        // Debug: Check if sound is enabled
        android.util.Log.d("RacingActivity", "SoundManager initialized. Music enabled: " + soundManager.isMusicEnabled());
        
        // Start racing music
        soundManager.playRacingMusic();
        android.util.Log.d("RacingActivity", "Racing music started");
        
        getIntentData();
        initializeViews();
        setupRacingCars();
        startRaceCountdown();
    }
    
    private void getIntentData() {
        Intent intent = getIntent();
        playerName = intent.getStringExtra("player_name");
        playerBalance = intent.getIntExtra("player_balance", 1000);
        isMultiBet = intent.getBooleanExtra("is_multi_bet", false);
        
        if (isMultiBet) {
            // Get multi-bet data
            betCarIds = intent.getIntegerArrayListExtra("bet_car_ids");
            betCarNames = intent.getStringArrayListExtra("bet_car_names");
            betAmounts = intent.getIntegerArrayListExtra("bet_amounts");
            float[] oddsArray = intent.getFloatArrayExtra("bet_odds");
            betOdds = new ArrayList<>();
            if (oddsArray != null) {
                for (float odds : oddsArray) {
                    betOdds.add(odds);
                }
            }
        } else {
            // Single bet data
            betAmount = intent.getIntExtra("bet_amount", 0);
            selectedCarId = intent.getIntExtra("selected_car_id", 0);
            selectedCarName = intent.getStringExtra("selected_car_name");
            selectedCarOdds = intent.getFloatExtra("selected_car_odds", 2.0f);
        }
    }
    
    private void initializeViews() {
        tvRaceTitle = findViewById(R.id.tvRaceTitle);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvRaceInfo = findViewById(R.id.tvRaceInfo);
        tvSkipInstruction = findViewById(R.id.tvSkipInstruction);
        
        // Pause elements
        btnPause = findViewById(R.id.btnPause);
        pauseOverlay = findViewById(R.id.pauseOverlay);
        btnResume = findViewById(R.id.btnResume);
        btnRestartRace = findViewById(R.id.btnRestartRace);
        btnExitToMenu = findViewById(R.id.btnExitToMenu);
        switchSoundEffects = findViewById(R.id.switchSoundEffects);
        switchBackgroundMusic = findViewById(R.id.switchBackgroundMusic);

        // Initialize car ImageViews
        carImageViews = new ImageView[5];
        carImageViews[0] = findViewById(R.id.ivCar1);
        carImageViews[1] = findViewById(R.id.ivCar2);
        carImageViews[2] = findViewById(R.id.ivCar3);
        carImageViews[3] = findViewById(R.id.ivCar4);
        carImageViews[4] = findViewById(R.id.ivCar5);
        
        // Initialize smoke ImageViews 
        smokeImageViews = new ImageView[5];
        smokeImageViews[0] = findViewById(R.id.ivSmoke1);
        smokeImageViews[1] = findViewById(R.id.ivSmoke2);
        smokeImageViews[2] = findViewById(R.id.ivSmoke3);
        smokeImageViews[3] = findViewById(R.id.ivSmoke4);
        smokeImageViews[4] = findViewById(R.id.ivSmoke5);
        
        // Initialize progress bars
        progressBars = new ProgressBar[5];
        progressBars[0] = findViewById(R.id.progressCar1);
        progressBars[1] = findViewById(R.id.progressCar2);
        progressBars[2] = findViewById(R.id.progressCar3);
        progressBars[3] = findViewById(R.id.progressCar4);
        progressBars[4] = findViewById(R.id.progressCar5);
        
        // Initialize position texts
        positionTexts = new TextView[5];
        positionTexts[0] = findViewById(R.id.tvPosition1);
        positionTexts[1] = findViewById(R.id.tvPosition2);
        positionTexts[2] = findViewById(R.id.tvPosition3);
        positionTexts[3] = findViewById(R.id.tvPosition4);
        positionTexts[4] = findViewById(R.id.tvPosition5);
        
        // Setup race info
        if (isMultiBet) {
            int totalBets = 0;
            for (int amount : betAmounts) {
                totalBets += amount;
            }
            tvRaceInfo.setText("Multi-Bet: " + totalBets + " coins on " + betCarIds.size() + " cars");
        } else {
            tvRaceInfo.setText("Your Bet: " + betAmount + " coins on " + selectedCarName);
        }
        
        random = new Random();
        raceHandler = new Handler();
        // audioManager = GameAudioManager.getInstance(this); // DISABLED - using SoundManager instead
        
        // Setup key listener for skip functionality
        setupSkipKeyListener();
        
        // Setup pause functionality
        setupPauseListeners();
    }
    
    private void setupSkipKeyListener() {
        // Make the main layout focusable to receive key events
        View mainLayout = findViewById(R.id.main_layout);
        if (mainLayout != null) {
            mainLayout.setFocusableInTouchMode(true);
            mainLayout.requestFocus();
            mainLayout.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_SPACE && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (raceStarted && !raceFinished && !raceSkipped) {
                        skipRace();
                        return true;
                    }
                }
                return false;
            });
        }
    }
    
    private void skipRace() {
        if (raceSkipped || raceFinished) return;
        
        raceSkipped = true;
        soundManager.playSound(SoundManager.SOUND_BUTTON_CLICK); // Button click sound
        
        // Hide skip instruction
        tvSkipInstruction.setVisibility(View.GONE);
        
        // Stop current race handler
        if (raceHandler != null && raceRunnable != null) {
            raceHandler.removeCallbacks(raceRunnable);
        }
        
        // Show skip animation
        showSkipAnimation();
        
        // Determine random winner and finish race quickly
        finishRaceQuickly();
    }
    
    private void showSkipAnimation() {
        // Create a fast-forward effect
        tvRaceInfo.setText("⏩ SKIPPING RACE...");
        
        // First determine the winner before animation
        winnerCarId = random.nextInt(racingCars.size());
        
        // Animate cars with different speeds - winner first, others follow
        for (int i = 0; i < carImageViews.length; i++) {
            ImageView carView = carImageViews[i];
            ProgressBar progressBar = progressBars[i];
            
            boolean isWinner = (i == winnerCarId);
            
            // Winner gets fastest animation, others slower with delays
            int animationDuration;
            int animationDelay;
            float finalPosition;
            int finalProgress;
            
            if (isWinner) {
                // Winner: Fast to finish line
                animationDuration = 600;
                animationDelay = 0;
                finalPosition = getResources().getDisplayMetrics().widthPixels * 0.6f;
                finalProgress = RACE_DISTANCE;
            } else {
                // Others: Slower and don't reach finish line
                animationDuration = 800 + random.nextInt(300); // 800-1100ms
                animationDelay = 100 + random.nextInt(200); // 100-300ms delay
                finalPosition = getResources().getDisplayMetrics().widthPixels * (0.45f + random.nextFloat() * 0.1f); // 45-55%
                finalProgress = RACE_DISTANCE - 50 - random.nextInt(150); // Don't reach finish
            }
            
            // Car movement animation
            ObjectAnimator carAnimation = ObjectAnimator.ofFloat(carView, "translationX", 
                carView.getTranslationX(), finalPosition);
            carAnimation.setDuration(animationDuration);
            carAnimation.setStartDelay(animationDelay);
            carAnimation.setInterpolator(new AccelerateInterpolator());
            carAnimation.start();
            
            // Progress bar animation
            ObjectAnimator progressAnimation = ObjectAnimator.ofInt(progressBar, "progress", 
                progressBar.getProgress(), finalProgress);
            progressAnimation.setDuration(animationDuration);
            progressAnimation.setStartDelay(animationDelay);
            progressAnimation.setInterpolator(new AccelerateInterpolator());
            progressAnimation.start();
            
            // Add winner visual effect
            if (isWinner) {
                // Winner gets special animation after reaching finish
                ObjectAnimator winnerEffect = ObjectAnimator.ofFloat(carView, "scaleY", 1.0f, 1.2f, 1.0f);
                winnerEffect.setDuration(400);
                winnerEffect.setStartDelay(animationDuration + 100);
                winnerEffect.setRepeatCount(1);
                winnerEffect.start();
                
                // Show winner immediately in race info
                carAnimation.addListener(new android.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        tvRaceInfo.setText("🏆 " + racingCars.get(winnerCarId).getName() + " WINS!");
                    }
                });
            }
        }
    }
    
    private void finishRaceQuickly() {
        // Winner already determined in showSkipAnimation()
        raceFinished = true;
        
        // Stop engine sounds (DISABLED - using SoundManager)
        // // audioManager.stopEngineSound(); // DISABLED
        
        // Update final positions to match animation
        for (int i = 0; i < racingCars.size(); i++) {
            if (i == winnerCarId) {
                // Winner finishes the race
                racingCars.get(i).setCurrentPosition(RACE_DISTANCE);
            } else {
                // Others don't reach finish line
                racingCars.get(i).setCurrentPosition(RACE_DISTANCE - 50 - random.nextInt(150));
            }
        }
        
        updatePositions();
        
        // Show result after winner finishes (winner animation takes ~600ms + delay)
        raceHandler.postDelayed(this::showResult, 1200);
    }
    
    private void setupPauseListeners() {
        // Pause button click
        btnPause.setOnClickListener(v -> {
            if (raceStarted && !raceFinished && !raceSkipped) {
                pauseRace();
            }
        });
        
        // Resume button
        btnResume.setOnClickListener(v -> {
            soundManager.playSound(SoundManager.SOUND_BUTTON_CLICK); // Button click
            resumeRace();
        });
        
        // Restart race button
        btnRestartRace.setOnClickListener(v -> {
            soundManager.playSound(SoundManager.SOUND_BUTTON_CLICK); // Button click
            showRestartConfirmation();
        });
        
        // Exit to menu button
        btnExitToMenu.setOnClickListener(v -> {
            soundManager.playSound(SoundManager.SOUND_BUTTON_CLICK); // Button click
            showExitConfirmation();
        });
        
        // Sound effects switch
        switchSoundEffects.setOnCheckedChangeListener((buttonView, isChecked) -> {
            soundManager.setSoundEnabled(isChecked); // Control sound effects
            soundManager.playSound(SoundManager.SOUND_BUTTON_CLICK); // Switch click sound
        });
        
        // Background music switch
        switchBackgroundMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            soundManager.setMusicEnabled(isChecked); // Control background music
            soundManager.playSound(SoundManager.SOUND_BUTTON_CLICK); // Switch click sound
        });
        
        // Initialize settings from audio manager
        initializePauseSettings();
    }
    
    private void initializePauseSettings() {
        // Load current audio settings
        // switchSoundEffects.setChecked(audioManager.isSoundEffectsEnabled()); // DISABLED
        // switchBackgroundMusic.setChecked(audioManager.isBackgroundMusicEnabled()); // DISABLED
    }
    
    private void pauseRace() {
        if (racePaused) return;
        
        racePaused = true;
        soundManager.playSound(SoundManager.SOUND_BUTTON_CLICK); // Pause button click
        
        // Pause race handler
        if (raceHandler != null && raceRunnable != null) {
            raceHandler.removeCallbacks(raceRunnable);
        }
        
        // Pause audio
        // audioManager.onPause(); // DISABLED
        
        // Show pause overlay with animation
        pauseOverlay.setVisibility(View.VISIBLE);
        pauseOverlay.setAlpha(0f);
        pauseOverlay.animate()
            .alpha(1f)
            .setDuration(300)
            .start();
            
        // Update race info
        tvRaceInfo.setText("⏸️ RACE PAUSED");
    }
    
    private void resumeRace() {
        if (!racePaused) return;
        
        racePaused = false;
        
        // Hide pause overlay with animation
        pauseOverlay.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction(() -> pauseOverlay.setVisibility(View.GONE))
            .start();
            
        // Resume audio
        // audioManager.onResume(); // DISABLED
        
        // Resume race handler
        if (raceHandler != null && raceRunnable != null && !raceFinished && !raceSkipped) {
            raceHandler.post(raceRunnable);
        }
        
        // Update race info back to normal
        updateRaceInfoForResume();
    }
    
    private void updateRaceInfoForResume() {
        double raceProgress = getRaceProgress();
        if (raceProgress < 0.3) {
            tvRaceInfo.setText("🏁 Race in progress...");
        } else if (raceProgress < 0.7) {
            tvRaceInfo.setText("🔥 Intense competition!");
        } else {
            tvRaceInfo.setText("🏁 FINAL STRETCH!");
        }
    }
    
    private void showRestartConfirmation() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("🔄 Restart Race")
            .setMessage("Are you sure you want to restart the race? Your current progress will be lost.")
            .setPositiveButton("Restart", (dialog, which) -> {
                restartRace();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void showExitConfirmation() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("🏠 Exit to Menu")
            .setMessage("Are you sure you want to exit? Your bet will be lost.")
            .setPositiveButton("Exit", (dialog, which) -> {
                exitToMenu();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void restartRace() {
        // Reset all race state
        raceFinished = false;
        raceSkipped = false;
        raceStarted = false;
        racePaused = false;
        winnerCarId = -1;
        
        // Hide pause overlay
        pauseOverlay.setVisibility(View.GONE);
        
        // Reset car positions
        for (Car car : racingCars) {
            car.setCurrentPosition(0);
        }
        
        // Reset UI
        for (int i = 0; i < carImageViews.length; i++) {
            carImageViews[i].setTranslationX(0);
            progressBars[i].setProgress(0);
        }
        
        btnPause.setVisibility(View.GONE);
        tvSkipInstruction.setVisibility(View.GONE);
        
        // Start countdown again
        startRaceCountdown();
    }
    
    private void exitToMenu() {
        // Stop all handlers and audio
        if (raceHandler != null && raceRunnable != null) {
            raceHandler.removeCallbacks(raceRunnable);
        }
        // audioManager.stopEngineSound(); // DISABLED
        
        // Return to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    
    private void setupRacingCars() {
        racingCars = new ArrayList<>();
        // Use EXACT same balanced stats as BettingActivity with racing PNG images
        racingCars.add(new Car(0, "Red Thunder", R.drawable.racingcar_red, R.color.car_red, 82, 2.1f));
        racingCars.add(new Car(1, "Blue Lightning", R.drawable.racingcar_blue, R.color.car_blue, 84, 2.0f));
        racingCars.add(new Car(2, "Green Machine", R.drawable.racingcar_green, R.color.car_green, 81, 2.2f));
        racingCars.add(new Car(3, "Orange Bullet", R.drawable.racingcar_orange, R.color.car_yellow, 83, 2.0f));
        racingCars.add(new Car(4, "Purple Phantom", R.drawable.racingcar_purple, R.color.car_purple, 80, 2.3f));
        
        // Set car images with racing-safe effects for all cars
        // Simple 1:1 mapping: car index matches lane index
        for (int i = 0; i < racingCars.size(); i++) {
            // Use racing-safe effects with appropriate car color
            CarAnimationUtils.setCarForRacing(this, carImageViews[i], true, i);
            progressBars[i].setMax(RACE_DISTANCE);
            progressBars[i].setProgress(0);
            positionTexts[i].setText(racingCars.get(i).getName());
            
            // Highlight selected car
            if (racingCars.get(i).getId() == selectedCarId) {
                positionTexts[i].setTextColor(getResources().getColor(R.color.accent_yellow));
                positionTexts[i].setText("★ " + racingCars.get(i).getName());
            }
        }
        
        // Start smoke animations
        startSmokeAnimations();
    }
    
    private void startSmokeAnimations() {
        for (int i = 0; i < smokeImageViews.length; i++) {
            if (smokeImageViews[i] != null) {
                Drawable smokeDrawable = smokeImageViews[i].getDrawable();
                if (smokeDrawable instanceof Animatable) {
                    ((Animatable) smokeDrawable).start();
                }
            }
        }
    }
    
    private void startRaceCountdown() {
        tvCountdown.setVisibility(View.VISIBLE);
        
        final int[] countdown = {3};
        Handler countdownHandler = new Handler();
        
        Runnable countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (countdown[0] > 0) {
                    // Play countdown beep sound
                    soundManager.playSound(SoundManager.SOUND_BUTTON_CLICK); // Use as countdown beep
                    android.util.Log.d("RacingActivity", "Countdown: " + countdown[0]);
                    
                    tvCountdown.setText(String.valueOf(countdown[0]));
                    animateCountdown();
                    countdown[0]--;
                    countdownHandler.postDelayed(this, 1000);
                } else {
                    // Play race start sound
                    soundManager.playSound(SoundManager.SOUND_RACE_START);
                    android.util.Log.d("RacingActivity", "Race started!");
                    
                    tvCountdown.setText("GO!");
                    animateCountdown();
                    countdownHandler.postDelayed(() -> {
                        tvCountdown.setVisibility(View.GONE);
                        startRace();
                    }, 1000);
                }
            }
        };
        
        countdownHandler.post(countdownRunnable);
    }
    
    private void animateCountdown() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvCountdown, "scaleX", 0.5f, 1.5f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvCountdown, "scaleY", 0.5f, 1.5f, 1.0f);
        scaleX.setDuration(800);
        scaleY.setDuration(800);
        scaleX.start();
        scaleY.start();
    }
    
    private void startRace() {
        raceStarted = true;
        
        // Show skip instruction and pause button
        tvSkipInstruction.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.VISIBLE);
        
        // Reset car positions
        for (Car car : racingCars) {
            car.setCurrentPosition(0);
        }
        
        // Debug: Log car stats at race start
        ErrorHandler.logInfo("=== RACE START DEBUG ===");
        for (Car car : racingCars) {
            ErrorHandler.logInfo(car.getName() + ": Speed=" + car.getBaseSpeed() + ", Odds=" + car.getOdds());
        }
        
        // Create fresh random seed for each race
        random = new Random(System.currentTimeMillis());
        
        // Randomize car conditions for each race
        randomizeCarConditions();
        
        // Start engine sounds for all cars
        soundManager.playSound(SoundManager.SOUND_RACE_START);
        for (int i = 0; i < 5; i++) {
            soundManager.startEngineSound(i, 50); // Base RPM
        }
        
        raceRunnable = new Runnable() {
            @Override
            public void run() {
                if (!raceFinished && !raceSkipped && !racePaused) {
                    updateRaceProgress();
                    raceHandler.postDelayed(this, RACE_UPDATE_INTERVAL);
                }
            }
        };
        
        raceHandler.post(raceRunnable);
    }
    
    private void updateRaceProgress() {
        // Update each car's position with enhanced racing dynamics
        boolean someoneWon = false;
        
        // Calculate current race progress
        double raceProgress = getRaceProgress();
        
        // Update slipstream effects
        updateSlipstreamEffects();
        
        // Create randomized processing order to avoid any systematic bias
        List<Integer> carIndices = new ArrayList<>();
        for (int i = 0; i < racingCars.size(); i++) {
            carIndices.add(i);
        }
        java.util.Collections.shuffle(carIndices);
        
        for (int idx : carIndices) {
            Car car = racingCars.get(idx);
            
            // Determine if this car is currently leading
            boolean isLeading = isCarLeading(car);
            
            // Calculate enhanced speed with realistic factors
            int currentSpeed = car.calculateRaceSpeed(raceProgress, isLeading);
            int speedIncrement = Math.max(2, (int) (currentSpeed / 15.0f)); // Faster speed for 30s race
            
            // Add controlled randomness based on race stage
            int randomVariation = getRandomVariation(raceProgress);
            speedIncrement += randomVariation;
            
            int newPosition = car.getCurrentPosition() + speedIncrement;
            
            // Ensure we don't go backwards
            if (newPosition < car.getCurrentPosition()) {
                newPosition = car.getCurrentPosition() + 1;
            }
            
            // Check for collisions before updating position
            if (checkCollision(car, newPosition, idx)) {
                // Collision detected - slow down this car
                newPosition = car.getCurrentPosition() + (speedIncrement / 3); // Reduce speed significantly
                animateCollisionEffect(carImageViews[idx]);
                // Play crash sound effect
                playCrashSound();
            }
            
            // Random dramatic events (rare)
            if (raceProgress > 0.5 && random.nextInt(1000) < 2) { // 0.2% chance
                addDramaticEvent(car, idx);
            }
            
            car.setCurrentPosition(newPosition);
            progressBars[idx].setProgress(newPosition);
            
            // Animate car movement
            animateCarMovement(carImageViews[idx], newPosition);
            
            // Check if this car finished
            if (newPosition >= RACE_DISTANCE && !someoneWon) {
                someoneWon = true;
                winnerCarId = car.getId();
                raceFinished = true;
                
                // Debug logging
                ErrorHandler.logInfo("RACE WINNER: " + car.getName() + " (ID: " + car.getId() + 
                                   ", Base Speed: " + car.getBaseSpeed() + ")");
            }
        }
        
        updatePositions();
        
        // Play dynamic engine sound based on race progress
        playDynamicEngineSound();
        
        if (someoneWon) {
            finishRace();
        }
    }
    
    private void animateCarMovement(ImageView carView, int position) {
        float progress = (float) position / RACE_DISTANCE;
        float maxTranslation = getResources().getDisplayMetrics().widthPixels * 0.6f;
        float translationX = progress * maxTranslation;
        carView.setTranslationX(translationX);
        
        // Find car index for sound and smoke effects
        int carIndex = -1;
        for (int i = 0; i < carImageViews.length; i++) {
            if (carImageViews[i] == carView) {
                carIndex = i;
                break;
            }
        }
        
        if (carIndex != -1) {
            // Update engine sound based on speed
            float speed = Math.min(100, progress * 100); // Speed 0-100 based on progress
            soundManager.updateEngineSound(carIndex, speed);
            
            // Move smoke with car
            if (smokeImageViews[carIndex] != null) {
                // Smoke should be slightly behind the bigger car
                smokeImageViews[carIndex].setTranslationX(translationX - 30);
            }
        }
    }
    
    private boolean checkCollision(Car currentCar, int newPosition, int currentCarIndex) {
        // Check collision with other cars
        for (int i = 0; i < racingCars.size(); i++) {
            if (i == currentCarIndex) continue; // Skip self
            
            Car otherCar = racingCars.get(i);
            int otherPosition = otherCar.getCurrentPosition();
            
            // Check if cars are too close (collision)
            int distance = Math.abs(newPosition - otherPosition);
            if (distance < COLLISION_THRESHOLD) {
                // Check if we're catching up from behind or side-by-side
                if (newPosition >= otherPosition - CAR_LENGTH && newPosition <= otherPosition + CAR_LENGTH) {
                    return true; // Collision detected
                }
            }
        }
        
        return false; // No collision
    }
    
    private void animateCollisionEffect(ImageView carView) {
        // Play collision sound
        soundManager.playSound(SoundManager.SOUND_CAR_CRASH);
        
        // Create a quick shake animation to show collision
        ObjectAnimator shake = ObjectAnimator.ofFloat(carView, "translationY", 0f, -10f, 10f, -5f, 5f, 0f);
        shake.setDuration(300);
        shake.start();
        
        // Also add a slight rotation effect
        ObjectAnimator rotate = ObjectAnimator.ofFloat(carView, "rotation", 0f, 5f, -5f, 0f);
        rotate.setDuration(300);
        rotate.start();
    }
    
    private void playCrashSound() {
        // Play crash sound through audio manager
        // audioManager.playCrashSound(); // DISABLED
    }
    
    private void updatePositions() {
        // Sort cars by position (descending)
        List<Car> sortedCars = new ArrayList<>(racingCars);
        sortedCars.sort((car1, car2) -> Integer.compare(car2.getCurrentPosition(), car1.getCurrentPosition()));
        
        // Update position display with enhanced info
        for (int i = 0; i < sortedCars.size(); i++) {
            Car car = sortedCars.get(i);
            int carIndex = car.getId();
            
            String positionText = (i + 1) + ". " + car.getName();
            
            // Add special indicators
            if (car.getId() == selectedCarId) {
                positionText = "★ " + positionText; // Player's car
            }
            if (car.isInSlipstream()) {
                positionText += " 💨"; // Slipstream indicator
            }
            
            positionTexts[carIndex].setText(positionText);
        }
        
        // Update race info with live commentary
        updateRaceCommentary(sortedCars);
    }
    
    private void updateRaceCommentary(List<Car> sortedCars) {
        double raceProgress = getRaceProgress();
        Car leader = sortedCars.get(0);
        Car playerCar = null;
        int playerPosition = 0;
        
        // Find player's car position
        for (int i = 0; i < sortedCars.size(); i++) {
            if (sortedCars.get(i).getId() == selectedCarId) {
                playerCar = sortedCars.get(i);
                playerPosition = i + 1;
                break;
            }
        }
        
        String commentary = "";
        
        if (raceProgress < 0.3) {
            // Early race commentary
            commentary = "🏁 Early lead: " + leader.getName();
            if (playerPosition <= 2) {
                commentary += " • You're in great position! 🔥";
            }
        } else if (raceProgress < 0.7) {
            // Mid race commentary
            int gap = leader.getCurrentPosition() - sortedCars.get(1).getCurrentPosition();
            if (gap < 20) {
                commentary = "🔥 Tight race! " + leader.getName() + " leads";
            } else {
                commentary = "⚡ " + leader.getName() + " pulling away!";
            }
            
            if (playerPosition == 1) {
                commentary += " • You're leading! 🏆";
            } else if (playerPosition <= 3) {
                commentary += " • You're in contention! 💪";
            }
        } else {
            // Final stretch commentary
            commentary = "🏁 FINAL STRETCH! " + leader.getName() + " leads!";
            if (playerPosition == 1) {
                commentary = "🏆 YOU'RE WINNING! Hold on!";
            } else if (playerPosition <= 2) {
                commentary = "⚡ CLOSE FINISH! You can still win!";
            }
        }
        
        tvRaceInfo.setText(commentary);
    }
    
    private void addDramaticEvent(Car car, int carIndex) {
        // Random dramatic racing events
        int eventType = random.nextInt(3);
        
        switch (eventType) {
            case 0: // Burst of speed
                car.setCurrentSpeed(car.getCurrentSpeed() * 1.3);
                // Visual effect
                ObjectAnimator speedBoost = ObjectAnimator.ofFloat(carImageViews[carIndex], "scaleX", 1.0f, 1.1f, 1.0f);
                speedBoost.setDuration(500);
                speedBoost.start();
                break;
                
            case 1: // Tire trouble (slowdown)
                car.setFatigue(car.getFatigue() + 0.1); // Extra fatigue
                // Visual effect
                ObjectAnimator slowDown = ObjectAnimator.ofFloat(carImageViews[carIndex], "alpha", 1.0f, 0.7f, 1.0f);
                slowDown.setDuration(800);
                slowDown.start();
                break;
                
            case 2: // Perfect line (temporary advantage)
                car.setInSlipstream(true); // Temporary boost
                // Visual effect
                ObjectAnimator perfectLine = ObjectAnimator.ofFloat(carImageViews[carIndex], "rotation", 0f, 5f, -5f, 0f);
                perfectLine.setDuration(600);
                perfectLine.start();
                break;
        }
    }
    
    private void finishRace() {
        raceHandler.removeCallbacks(raceRunnable);
        
        // Hide skip instruction and pause button
        tvSkipInstruction.setVisibility(View.GONE);
        btnPause.setVisibility(View.GONE);
        
        // Stop all engine sounds
        soundManager.stopAllEngineSounds();
        
        // Play race finish sound
        soundManager.playSound(SoundManager.SOUND_RACE_FINISH);
        
        // Add finishing animation
        ObjectAnimator winnerAnimation = ObjectAnimator.ofFloat(
            carImageViews[winnerCarId], "scaleY", 1.0f, 1.2f, 1.0f
        );
        winnerAnimation.setDuration(1000);
        winnerAnimation.setRepeatCount(2);
        winnerAnimation.start();
        
        // Play coin collect sound if player won
        if (winnerCarId == selectedCarId) {
            soundManager.playSound(SoundManager.SOUND_COIN_COLLECT);
        }
        
        // Show result after a delay
        raceHandler.postDelayed(this::showResult, 2000);
    }
    
    private void showResult() {
        Car winnerCar = racingCars.get(winnerCarId);
        
        if (isMultiBet) {
            showMultiBetResult(winnerCar);
        } else {
            showSingleBetResult(winnerCar);
        }
    }
    
    private void showSingleBetResult(Car winnerCar) {
        boolean playerWon = (winnerCarId == selectedCarId);
        
        int winnings = 0;
        int newBalance = playerBalance;
        
        if (playerWon) {
            winnings = (int) (betAmount * selectedCarOdds);
            newBalance = playerBalance - betAmount + winnings;
        } else {
            newBalance = playerBalance - betAmount;
        }
        
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("player_name", playerName);
        intent.putExtra("winner_car_name", winnerCar.getName());
        intent.putExtra("player_won", playerWon);
        intent.putExtra("bet_amount", betAmount);
        intent.putExtra("winnings", winnings);
        intent.putExtra("new_balance", newBalance);
        intent.putExtra("selected_car_name", selectedCarName);
        
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
    
    private void showMultiBetResult(Car winnerCar) {
        int totalBets = 0;
        int totalWinnings = 0;
        boolean hasWinningBet = false;
        
        // Calculate results for each bet
        for (int i = 0; i < betCarIds.size(); i++) {
            int carId = betCarIds.get(i);
            int betAmount = betAmounts.get(i);
            float odds = betOdds.get(i);
            totalBets += betAmount;
            
            if (carId == winnerCarId) {
                totalWinnings += (int) (betAmount * odds);
                hasWinningBet = true;
            }
        }
        
        int newBalance = playerBalance - totalBets + totalWinnings;
        
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("player_name", playerName);
        intent.putExtra("winner_car_name", winnerCar.getName());
        intent.putExtra("is_multi_bet", true);
        intent.putExtra("player_won", hasWinningBet);
        intent.putExtra("bet_amount", totalBets);
        intent.putExtra("winnings", totalWinnings);
        intent.putExtra("new_balance", newBalance);
        
        // Pass multi-bet details
        intent.putIntegerArrayListExtra("bet_car_ids", (ArrayList<Integer>) betCarIds);
        intent.putStringArrayListExtra("bet_car_names", (ArrayList<String>) betCarNames);
        intent.putIntegerArrayListExtra("bet_amounts", (ArrayList<Integer>) betAmounts);
        
        float[] oddsArray = new float[betOdds.size()];
        for (int i = 0; i < betOdds.size(); i++) {
            oddsArray[i] = betOdds.get(i);
        }
        intent.putExtra("bet_odds", oddsArray);
        intent.putExtra("winner_car_id", winnerCarId);
        
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
    
    // Enhanced racing helper methods
    private double getRaceProgress() {
        // Calculate average race progress
        double totalProgress = 0;
        for (Car car : racingCars) {
            totalProgress += (double) car.getCurrentPosition() / RACE_DISTANCE;
        }
        return totalProgress / racingCars.size();
    }
    
    private boolean isCarLeading(Car targetCar) {
        // Check if this car is in the leading group (top 2 positions)
        int leadingCount = 0;
        for (Car car : racingCars) {
            if (car.getCurrentPosition() > targetCar.getCurrentPosition()) {
                leadingCount++;
            }
        }
        return leadingCount <= 1; // Leading or second place
    }
    
    private void updateSlipstreamEffects() {
        // Reset slipstream for all cars
        for (Car car : racingCars) {
            car.setInSlipstream(false);
        }
        
        // Calculate slipstream effects (cars close behind get boost)
        for (int i = 0; i < racingCars.size(); i++) {
            Car car = racingCars.get(i);
            for (int j = 0; j < racingCars.size(); j++) {
                if (i != j) {
                    Car otherCar = racingCars.get(j);
                    int positionDiff = otherCar.getCurrentPosition() - car.getCurrentPosition();
                    // If car is 10-50 pixels behind another car, it gets slipstream
                    if (positionDiff > 10 && positionDiff < 50) {
                        car.setInSlipstream(true);
                        break;
                    }
                }
            }
        }
    }
    
    private int getRandomVariation(double raceProgress) {
        // Controlled randomness based on race stage
        if (raceProgress < 0.2) {
            return random.nextInt(3) - 1; // ±1 pixel early race
        } else if (raceProgress < 0.8) {
            return random.nextInt(5) - 2; // ±2 pixels mid race
        } else {
            return random.nextInt(7) - 3; // ±3 pixels final stretch
        }
    }
    
    private void playDynamicEngineSound() {
        // Play engine sound occasionally based on race progress
        if (random.nextInt(100) < 15) { // 15% chance each update cycle
            double raceProgress = getRaceProgress();
            
            // Determine engine intensity based on race progress
            int intensity;
            if (raceProgress < 0.3) {
                intensity = 1; // Early race - low intensity
            } else if (raceProgress < 0.7) {
                intensity = 2; // Mid race - medium intensity
            } else {
                intensity = 3; // Late race - high intensity
            }
            
            // audioManager.playRacingEngineSound(intensity); // DISABLED
        }
    }
    
    private void randomizeCarConditions() {
        // Add small random variations to base stats for each race
        // This simulates different track conditions, car setup, etc.
        for (Car car : racingCars) {
            // Reset fatigue
            car.setFatigue(0.0);
            
            // Add random condition modifier (±5% to base speed)
            int originalSpeed = car.getBaseSpeed();
            double conditionModifier = 0.95 + (random.nextDouble() * 0.1); // 0.95 to 1.05
            int modifiedSpeed = (int) (originalSpeed * conditionModifier);
            
            // Temporarily modify speed (will be reset next race)
            car.setBaseSpeed(Math.max(75, Math.min(90, modifiedSpeed))); // Keep within bounds
            
            // Debug log
            if (modifiedSpeed != originalSpeed) {
                ErrorHandler.logInfo(car.getName() + " condition: " + originalSpeed + " -> " + modifiedSpeed);
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (soundManager != null) {
            soundManager.resumeAllSounds();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (soundManager != null) {
            soundManager.stopAllSounds();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (raceHandler != null && raceRunnable != null) {
            raceHandler.removeCallbacks(raceRunnable);
        }
        if (soundManager != null) {
            soundManager.release();
        }
    }
    
    @Override
    public void onBackPressed() {
        // Handle back press based on current state
        if (racePaused) {
            resumeRace(); // Resume if paused
        } else if (raceStarted && !raceFinished && !raceSkipped) {
            pauseRace(); // Pause if racing
        } else if (raceFinished || !raceStarted) {
            super.onBackPressed(); // Allow back if race finished or not started
        }
        // Otherwise prevent going back during active race
    }
}
