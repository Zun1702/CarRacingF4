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
    private Button btnStartRace, btnAddCoins, btnClearAllBets;
    private android.widget.ImageButton btnBackToHome;
    
    private SharedPreferences sharedPreferences;
    private GameAudioManager audioManager;
    private String playerName;
    private int playerBalance;
    private int selectedCarId = -1;
    private List<Car> carList;
    private CarAdapter carAdapter;
    
    // Multi-betting support (always enabled)
    private List<Integer> multiBetCarIds = new ArrayList<>();
    private List<Integer> multiBetAmounts = new ArrayList<>();
    private List<String> multiBetCarNames = new ArrayList<>();
    private List<Float> multiBetOdds = new ArrayList<>();
    
    private static final String PREFS_NAME = "CarRacingPrefs";
    private static final String KEY_PLAYER_NAME = "player_name";
    private static final String KEY_PLAYER_BALANCE = "player_balance";
    private static final int MIN_BET = 10;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_betting);
            
            initializeViews();
            setupPreferences();
            loadPlayerData();
            setupCarList();
            setupClickListeners();
            updateDisplay();
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("BettingActivity", "Error in onCreate: " + e.getMessage());
            finish(); // Close activity if there's an error
        }
    }
    
    private void initializeViews() {
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvPlayerBalance = findViewById(R.id.tvPlayerBalance);
        tvPotentialWinnings = findViewById(R.id.tvPotentialWinnings);
        rvCars = findViewById(R.id.rvCars);
        btnStartRace = findViewById(R.id.btnStartRace);
        btnAddCoins = findViewById(R.id.btnAddCoins);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnClearAllBets = findViewById(R.id.btnClearAllBets);
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
        carAdapter.setMultiBettingMode(true); // Always enable multi-betting mode
        rvCars.setLayoutManager(new LinearLayoutManager(this));
        rvCars.setAdapter(carAdapter);
    }
    
    private void onCarSelected(int carId) {
        audioManager.playButtonClick();
        showMultiBetDialog(carId);
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
        
        btnClearAllBets.setOnClickListener(v -> {
            audioManager.playButtonClick();
            clearAllBets();
        });
    }
    
    private void calculatePotentialWinnings() {
        calculateMultiBetPotentialWinnings();
    }
    
    private void calculateMultiBetPotentialWinnings() {
        if (multiBetCarIds.isEmpty()) {
            tvPotentialWinnings.setText("�️ Tap cars to place your bets");
            tvPotentialWinnings.setTextColor(getResources().getColor(R.color.white));
            return;
        }
        
        int totalBetAmount = 0;
        int maxPotentialWinnings = 0;
        
        StringBuilder betsInfo = new StringBuilder();
        betsInfo.append("🏁 Your Multi-Bets:\n");
        
        for (int i = 0; i < multiBetCarIds.size(); i++) {
            int betAmount = multiBetAmounts.get(i);
            float odds = multiBetOdds.get(i);
            String carName = multiBetCarNames.get(i);
            int potentialWin = (int) (betAmount * odds);
            
            totalBetAmount += betAmount;
            maxPotentialWinnings = Math.max(maxPotentialWinnings, potentialWin);
            
            betsInfo.append(String.format("🏎️ %s: %d coins (%.1fx) = %d\n", 
                carName, betAmount, odds, potentialWin));
        }
        
        betsInfo.append(String.format("\n💰 Total Bet: %d coins", totalBetAmount));
        betsInfo.append(String.format("\n🏆 Max Win: %d coins", maxPotentialWinnings));
        
        tvPotentialWinnings.setText(betsInfo.toString());
        tvPotentialWinnings.setTextColor(getResources().getColor(R.color.accent_yellow));
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
        return validateMultiBets();
    }
    
    private boolean validateMultiBets() {
        if (multiBetCarIds.isEmpty()) {
            Toast.makeText(this, "🎯 Please select at least one car to bet on!", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        int totalBetAmount = 0;
        for (int amount : multiBetAmounts) {
            totalBetAmount += amount;
        }
        
        if (totalBetAmount > playerBalance) {
            Toast.makeText(this, "💰 Total bets exceed your balance!\nTotal: " + totalBetAmount + " | Balance: " + playerBalance, Toast.LENGTH_LONG).show();
            return false;
        }
        
        return true;
    }
    
    private void showBettingConfirmationDialog() {
        showMultiBettingConfirmationDialog();
    }
    
    private void showMultiBettingConfirmationDialog() {
        int totalBetAmount = 0;
        int maxPotentialWinnings = 0;
        
        StringBuilder message = new StringBuilder();
        message.append("🏆 BET CONFIRMATION\n\n");
        message.append("🏁 Your Bets:\n");
        
        for (int i = 0; i < multiBetCarIds.size(); i++) {
            String carName = multiBetCarNames.get(i);
            int betAmount = multiBetAmounts.get(i);
            float odds = multiBetOdds.get(i);
            int potentialWin = (int) (betAmount * odds);
            
            totalBetAmount += betAmount;
            maxPotentialWinnings = Math.max(maxPotentialWinnings, potentialWin);
            
            message.append(String.format("🏎️ %s: %d coins (%.1fx)\n", carName, betAmount, odds));
        }
        
        message.append(String.format("\n💰 Total Bet: %d coins", totalBetAmount));
        message.append(String.format("\n🏆 Max Possible Win: %d coins", maxPotentialWinnings));
        message.append(String.format("\n💳 Your Balance: %d coins", playerBalance));
        message.append("\n\n🎯 You win if ANY of your selected cars wins!");
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("🏁 Confirm Your Bets")
               .setMessage(message.toString())
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
        Intent intent = new Intent(this, RacingActivity.class);
        intent.putExtra("player_name", playerName);
        intent.putExtra("player_balance", playerBalance);
        intent.putExtra("is_multi_bet", true);
        
        // Convert lists to arrays for intent
        intent.putIntegerArrayListExtra("bet_car_ids", new ArrayList<>(multiBetCarIds));
        intent.putStringArrayListExtra("bet_car_names", new ArrayList<>(multiBetCarNames));
        intent.putIntegerArrayListExtra("bet_amounts", new ArrayList<>(multiBetAmounts));
        
        float[] oddsArray = new float[multiBetOdds.size()];
        for (int i = 0; i < multiBetOdds.size(); i++) {
            oddsArray[i] = multiBetOdds.get(i);
        }
        intent.putExtra("bet_odds", oddsArray);
        
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    
    private void updateDisplay() {
        tvPlayerName.setText("Welcome, " + playerName + "!");
        tvPlayerBalance.setText(String.format(getString(R.string.player_balance), playerBalance));
        calculatePotentialWinnings(); // Show initial betting instructions
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
    
    // ====== BETTING METHODS ======
    
    private void showMultiBetDialog(int carId) {
        Car selectedCar = getCarById(carId);
        if (selectedCar == null) return;
        
        // Check if car already has a bet
        int existingBetIndex = multiBetCarIds.indexOf(carId);
        boolean isExistingBet = existingBetIndex != -1;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_multi_bet, null);
        builder.setView(dialogView);
        
        TextView tvCarName = dialogView.findViewById(R.id.tvCarName);
        TextView tvCarInfo = dialogView.findViewById(R.id.tvCarInfo);
        EditText etDialogBetAmount = dialogView.findViewById(R.id.etDialogBetAmount);
        Button btnRemoveBet = dialogView.findViewById(R.id.btnRemoveBet);
        
        tvCarName.setText(selectedCar.getName());
        tvCarInfo.setText(String.format("Speed: %d | Odds: %.1fx", selectedCar.getBaseSpeed(), selectedCar.getOdds()));
        
        if (isExistingBet) {
            etDialogBetAmount.setText(String.valueOf(multiBetAmounts.get(existingBetIndex)));
            btnRemoveBet.setVisibility(View.VISIBLE);
        } else {
            btnRemoveBet.setVisibility(View.GONE);
        }
        
        AlertDialog dialog = builder.create();
        
        dialogView.findViewById(R.id.btnConfirmBet).setOnClickListener(v -> {
            String betAmountStr = etDialogBetAmount.getText().toString().trim();
            if (betAmountStr.isEmpty()) {
                Toast.makeText(this, "Please enter bet amount", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                int betAmount = Integer.parseInt(betAmountStr);
                if (betAmount < MIN_BET) {
                    Toast.makeText(this, "Minimum bet is " + MIN_BET + " coins", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Calculate total bet amount excluding current car (if updating existing bet)
                int totalOtherBets = 0;
                for (int i = 0; i < multiBetCarIds.size(); i++) {
                    if (multiBetCarIds.get(i) != carId) {
                        totalOtherBets += multiBetAmounts.get(i);
                    }
                }
                
                if (totalOtherBets + betAmount > playerBalance) {
                    Toast.makeText(this, "Total bets would exceed balance!", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Add or update bet
                if (isExistingBet) {
                    multiBetAmounts.set(existingBetIndex, betAmount);
                } else {
                    multiBetCarIds.add(carId);
                    multiBetAmounts.add(betAmount);
                    multiBetCarNames.add(selectedCar.getName());
                    multiBetOdds.add(selectedCar.getOdds());
                }
                
                updateMultiBetDisplay();
                calculatePotentialWinnings();
                dialog.dismiss();
                
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnRemoveBet.setOnClickListener(v -> {
            multiBetCarIds.remove(existingBetIndex);
            multiBetAmounts.remove(existingBetIndex);
            multiBetCarNames.remove(existingBetIndex);
            multiBetOdds.remove(existingBetIndex);
            
            updateMultiBetDisplay();
            calculatePotentialWinnings();
            dialog.dismiss();
        });
        
        dialogView.findViewById(R.id.btnCancelBet).setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private void updateMultiBetDisplay() {
        if (carAdapter != null) {
            carAdapter.setMultiBetData(multiBetCarIds);
            carAdapter.notifyDataSetChanged();
        }
    }
    
    private void clearAllBets() {
        multiBetCarIds.clear();
        multiBetAmounts.clear();
        multiBetCarNames.clear();
        multiBetOdds.clear();
        updateMultiBetDisplay();
        calculatePotentialWinnings();
    }

    @Override
    public void onBackPressed() {
        audioManager.playButtonClick();
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}