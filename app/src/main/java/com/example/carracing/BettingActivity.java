package com.example.carracing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BettingActivity extends AppCompatActivity {
    
    private TextView tvPlayerName, tvPlayerBalance, tvPotentialWinnings;
    private RecyclerView rvCars;
    private EditText etBetAmount;
    private Button btnStartRace, btnAddCoins, btnBackToHome;
    private RadioGroup rgCarSelection;
    
    private SharedPreferences sharedPreferences;
    private GameAudioManager audioManager;
    private String playerName;
    private int playerBalance;
    private int selectedCarId = -1;
    private List<Car> carList;
    private CarAdapter carAdapter;
    
    private static final String PREFS_NAME = "CarRacingPrefs";
    private static final String KEY_PLAYER_NAME = "player_name";
    private static final String KEY_PLAYER_BALANCE = "player_balance";
    private static final int MIN_BET = 10;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_betting);
        
        initializeViews();
        setupPreferences();
        loadPlayerData();
        setupCarList();
        setupClickListeners();
        updateDisplay();
    }
    
    private void initializeViews() {
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvPlayerBalance = findViewById(R.id.tvPlayerBalance);
        tvPotentialWinnings = findViewById(R.id.tvPotentialWinnings);
        rvCars = findViewById(R.id.rvCars);
        etBetAmount = findViewById(R.id.etBetAmount);
        btnStartRace = findViewById(R.id.btnStartRace);
        btnAddCoins = findViewById(R.id.btnAddCoins);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        rgCarSelection = findViewById(R.id.rgCarSelection);
    }
    
    private void setupPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        audioManager = GameAudioManager.getInstance(this);
    }
    
    private void loadPlayerData() {
        playerName = sharedPreferences.getString(KEY_PLAYER_NAME, "Player");
        playerBalance = sharedPreferences.getInt(KEY_PLAYER_BALANCE, 1000);
    }
    
    private void setupCarList() {
        carList = new ArrayList<>();
        // Rebalanced cars with similar base speeds for fair racing
        carList.add(new Car(0, "Red Thunder", R.drawable.car_red, R.color.car_red, 82, 2.1f));
        carList.add(new Car(1, "Blue Lightning", R.drawable.car_blue, R.color.car_blue, 84, 2.0f));
        carList.add(new Car(2, "Green Machine", R.drawable.car_green, R.color.car_green, 81, 2.2f));
        carList.add(new Car(3, "Yellow Bullet", R.drawable.car_yellow, R.color.car_yellow, 83, 2.0f));
        carList.add(new Car(4, "Purple Phantom", R.drawable.car_purple, R.color.car_purple, 80, 2.3f));
        
        // Setup RecyclerView
        carAdapter = new CarAdapter(carList, this::onCarSelected);
        rvCars.setLayoutManager(new LinearLayoutManager(this));
        rvCars.setAdapter(carAdapter);
        
        // Setup RadioGroup
        setupRadioGroup();
    }
    
    private void setupRadioGroup() {
        rgCarSelection.removeAllViews();
        
        for (Car car : carList) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(car.getId());
            radioButton.setText(car.getName() + " (Odds: " + car.getOdds() + "x)");
            radioButton.setTextColor(getResources().getColor(R.color.white));
            radioButton.setTextSize(16);
            radioButton.setPadding(16, 12, 16, 12);
            
            rgCarSelection.addView(radioButton);
        }
        
        rgCarSelection.setOnCheckedChangeListener((group, checkedId) -> {
            selectedCarId = checkedId;
            calculatePotentialWinnings();
        });
    }
    
    private void onCarSelected(int carId) {
        audioManager.playButtonClick();
        selectedCarId = carId;
        rgCarSelection.check(carId);
        calculatePotentialWinnings();
        
        // Play car preview animation
        playCarPreviewAnimation(carId);
    }
    
    private void playCarPreviewAnimation(int carId) {
        // Find the selected car from list
        Car selectedCar = null;
        for (Car car : carList) {
            if (car.getId() == carId) {
                selectedCar = car;
                break;
            }
        }
        
        if (selectedCar != null) {
            // Show brief info about selected car
            String carInfo = String.format("Selected: %s\nSpeed: %d | Odds: %.1fx", 
                selectedCar.getName(), selectedCar.getBaseSpeed(), selectedCar.getOdds());
            Toast.makeText(this, carInfo, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupClickListeners() {
        btnStartRace.setOnClickListener(v -> {
            audioManager.playButtonClick();
            if (validateBet()) {
                showBettingConfirmationDialog();
            }
        });
        
        btnAddCoins.setOnClickListener(v -> {
            audioManager.playButtonClick();
            showAddCoinsDialog();
        });
        
        btnBackToHome.setOnClickListener(v -> {
            audioManager.playButtonClick();
            finish(); // Go back to MainActivity
        });
        
        // Update potential winnings when bet amount changes
        etBetAmount.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculatePotentialWinnings();
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
    
    private void calculatePotentialWinnings() {
        if (selectedCarId == -1) {
            tvPotentialWinnings.setText("Select a car first");
            return;
        }
        
        String betAmountStr = etBetAmount.getText().toString().trim();
        if (betAmountStr.isEmpty()) {
            tvPotentialWinnings.setText("Enter bet amount");
            return;
        }
        
        try {
            int betAmount = Integer.parseInt(betAmountStr);
            Car selectedCar = getCarById(selectedCarId);
            if (selectedCar != null) {
                int potentialWinnings = (int) (betAmount * selectedCar.getOdds());
                tvPotentialWinnings.setText("Potential Winnings: " + potentialWinnings + " coins");
                tvPotentialWinnings.setTextColor(getResources().getColor(R.color.win_green));
            }
        } catch (NumberFormatException e) {
            tvPotentialWinnings.setText("Invalid bet amount");
            tvPotentialWinnings.setTextColor(getResources().getColor(R.color.lose_red));
        }
    }
    
    private Car getCarById(int id) {
        for (Car car : carList) {
            if (car.getId() == id) {
                return car;
            }
        }
        return null;
    }
    
    private boolean validateBet() {
        if (selectedCarId == -1) {
            Toast.makeText(this, getString(R.string.error_no_car_selected), Toast.LENGTH_SHORT).show();
            return false;
        }
        
        String betAmountStr = etBetAmount.getText().toString().trim();
        if (betAmountStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_invalid_bet), Toast.LENGTH_SHORT).show();
            etBetAmount.requestFocus();
            return false;
        }
        
        try {
            int betAmount = Integer.parseInt(betAmountStr);
            
            if (betAmount < MIN_BET) {
                Toast.makeText(this, "Minimum bet is " + MIN_BET + " coins", Toast.LENGTH_SHORT).show();
                etBetAmount.requestFocus();
                return false;
            }
            
            if (betAmount > playerBalance) {
                Toast.makeText(this, getString(R.string.error_insufficient_funds), Toast.LENGTH_SHORT).show();
                etBetAmount.requestFocus();
                return false;
            }
            
            return true;
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.error_invalid_bet), Toast.LENGTH_SHORT).show();
            etBetAmount.requestFocus();
            return false;
        }
    }
    
    private void showBettingConfirmationDialog() {
        int betAmount = Integer.parseInt(etBetAmount.getText().toString().trim());
        Car selectedCar = getCarById(selectedCarId);
        int potentialWinnings = (int) (betAmount * selectedCar.getOdds());
        
        String message = String.format(
            "🏎️ Selected Car: %s\n" +
            "💰 Bet Amount: %d coins\n" +
            "🎯 Car Odds: %.1fx\n" +
            "💎 Potential Winnings: %d coins\n" +
            "💳 Your Balance: %d coins\n\n" +
            "Are you ready to race?",
            selectedCar.getName(),
            betAmount,
            selectedCar.getOdds(),
            potentialWinnings,
            playerBalance
        );
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("🏁 Confirm Your Bet")
               .setMessage(message)
               .setPositiveButton("🚀 Start Race!", (dialog, which) -> {
                   audioManager.playButtonClick();
                   audioManager.playRaceStart();
                   startRace();
               })
               .setNegativeButton("❌ Cancel", (dialog, which) -> {
                   audioManager.playButtonClick();
                   dialog.dismiss();
               })
               .setCancelable(false);
        
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
        
        // Style the dialog buttons
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent_yellow));
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.lose_red));
    }
    
    private void startRace() {
        int betAmount = Integer.parseInt(etBetAmount.getText().toString().trim());
        Car selectedCar = getCarById(selectedCarId);
        
        Intent intent = new Intent(this, RacingActivity.class);
        intent.putExtra("player_name", playerName);
        intent.putExtra("player_balance", playerBalance);
        intent.putExtra("bet_amount", betAmount);
        intent.putExtra("selected_car_id", selectedCarId);
        intent.putExtra("selected_car_name", selectedCar.getName());
        intent.putExtra("selected_car_odds", selectedCar.getOdds());
        
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    
    private void updateDisplay() {
        tvPlayerName.setText("Welcome, " + playerName + "!");
        tvPlayerBalance.setText(String.format(getString(R.string.player_balance), playerBalance));
        etBetAmount.setHint("Min: " + MIN_BET + " | Max: " + playerBalance);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (audioManager != null) {
            audioManager.onResume();
        }
        // Refresh player data in case it changed
        loadPlayerData();
        updateDisplay();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (audioManager != null) {
            audioManager.onPause();
        }
    }
    
    private void showAddCoinsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Coins");
        
        // Create input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter amount to add");
        
        // Set margins for better appearance
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 0, 50, 0);
        input.setLayoutParams(lp);
        
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(input);
        
        builder.setView(container);
        
        builder.setPositiveButton("Add", (dialog, which) -> {
            String inputText = input.getText().toString().trim();
            if (!inputText.isEmpty()) {
                try {
                    int coinsToAdd = Integer.parseInt(inputText);
                    if (coinsToAdd > 0) {
                        addCoins(coinsToAdd);
                    } else {
                        Toast.makeText(this, "Please enter a positive amount", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    private void addCoins(int amount) {
        playerBalance += amount;
        
        // Save updated balance to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("player_balance", playerBalance);
        editor.apply();
        
        // Update display
        updateDisplay();
        
        // Show confirmation
        Toast.makeText(this, "Added " + amount + " coins!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        audioManager.playButtonClick();
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}