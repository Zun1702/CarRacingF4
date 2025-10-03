package com.example.carracing;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RacingActivity extends AppCompatActivity {
    
    private TextView tvRaceTitle, tvCountdown, tvRaceInfo;
    private ImageView[] carImageViews;
    private ProgressBar[] progressBars;
    private TextView[] positionTexts;
    
    private String playerName;
    private int playerBalance;
    private int betAmount;
    private int selectedCarId;
    private String selectedCarName;
    private float selectedCarOdds;
    
    private List<Car> racingCars;
    private Handler raceHandler;
    private Runnable raceRunnable;
    private Random random;
    private GameAudioManager audioManager;
    
    private boolean raceFinished = false;
    private int winnerCarId = -1;
    private final int RACE_DISTANCE = 1000; // pixels
    private final int RACE_UPDATE_INTERVAL = 30; // milliseconds - faster updates for 30s race
    private final int COLLISION_THRESHOLD = 20; // pixels for collision detection
    private final int CAR_LENGTH = 30; // pixels
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_racing);
        
        getIntentData();
        initializeViews();
        setupRacingCars();
        startRaceCountdown();
    }
    
    private void getIntentData() {
        Intent intent = getIntent();
        playerName = intent.getStringExtra("player_name");
        playerBalance = intent.getIntExtra("player_balance", 1000);
        betAmount = intent.getIntExtra("bet_amount", 0);
        selectedCarId = intent.getIntExtra("selected_car_id", 0);
        selectedCarName = intent.getStringExtra("selected_car_name");
        selectedCarOdds = intent.getFloatExtra("selected_car_odds", 2.0f);
    }
    
    private void initializeViews() {
        tvRaceTitle = findViewById(R.id.tvRaceTitle);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvRaceInfo = findViewById(R.id.tvRaceInfo);
        
        // Initialize car ImageViews
        carImageViews = new ImageView[5];
        carImageViews[0] = findViewById(R.id.ivCar1);
        carImageViews[1] = findViewById(R.id.ivCar2);
        carImageViews[2] = findViewById(R.id.ivCar3);
        carImageViews[3] = findViewById(R.id.ivCar4);
        carImageViews[4] = findViewById(R.id.ivCar5);
        
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
        tvRaceInfo.setText("Your Bet: " + betAmount + " coins on " + selectedCarName);
        
        random = new Random();
        raceHandler = new Handler();
        audioManager = GameAudioManager.getInstance(this);
    }
    
    private void setupRacingCars() {
        racingCars = new ArrayList<>();
        // Use EXACT same balanced stats as BettingActivity
        racingCars.add(new Car(0, "Red Thunder", R.drawable.car_red, R.color.car_red, 82, 2.1f));
        racingCars.add(new Car(1, "Blue Lightning", R.drawable.car_blue, R.color.car_blue, 84, 2.0f));
        racingCars.add(new Car(2, "Green Machine", R.drawable.car_green, R.color.car_green, 81, 2.2f));
        racingCars.add(new Car(3, "Yellow Bullet", R.drawable.car_yellow, R.color.car_yellow, 83, 2.0f));
        racingCars.add(new Car(4, "Purple Phantom", R.drawable.car_purple, R.color.car_purple, 80, 2.3f));
        
        // Set car images
        for (int i = 0; i < racingCars.size(); i++) {
            carImageViews[i].setImageResource(racingCars.get(i).getDrawableResource());
            progressBars[i].setMax(RACE_DISTANCE);
            progressBars[i].setProgress(0);
            positionTexts[i].setText(racingCars.get(i).getName());
            
            // Highlight selected car
            if (racingCars.get(i).getId() == selectedCarId) {
                positionTexts[i].setTextColor(getResources().getColor(R.color.accent_yellow));
                positionTexts[i].setText("★ " + racingCars.get(i).getName());
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
                    audioManager.playCountdown();
                    tvCountdown.setText(String.valueOf(countdown[0]));
                    animateCountdown();
                    countdown[0]--;
                    countdownHandler.postDelayed(this, 1000);
                } else {
                    audioManager.playRaceStart();
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
        
        // Start engine sounds
        audioManager.playEngineSound();
        
        raceRunnable = new Runnable() {
            @Override
            public void run() {
                if (!raceFinished) {
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
        audioManager.playCrashSound();
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
        
        // Stop engine sounds
        audioManager.stopEngineSound();
        
        // Play audience cheering
        audioManager.playAudienceCheering();
        
        // Add finishing animation
        ObjectAnimator winnerAnimation = ObjectAnimator.ofFloat(
            carImageViews[winnerCarId], "scaleY", 1.0f, 1.2f, 1.0f
        );
        winnerAnimation.setDuration(1000);
        winnerAnimation.setRepeatCount(2);
        winnerAnimation.start();
        
        // Play victory sound if player won
        if (winnerCarId == selectedCarId) {
            audioManager.playVictory();
        }
        
        // Show result after a delay
        raceHandler.postDelayed(this::showResult, 2000);
    }
    
    private void showResult() {
        Car winnerCar = racingCars.get(winnerCarId);
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
            
            audioManager.playRacingEngineSound(intensity);
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
    protected void onDestroy() {
        super.onDestroy();
        if (raceHandler != null && raceRunnable != null) {
            raceHandler.removeCallbacks(raceRunnable);
        }
        if (audioManager != null) {
            audioManager.stopEngineSound();
        }
    }
    
    @Override
    public void onBackPressed() {
        // Prevent going back during race
        if (raceFinished) {
            super.onBackPressed();
        }
    }
}