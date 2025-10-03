package com.example.carracing;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaceHistoryManager {
    private static RaceHistoryManager instance;
    private Context context;
    private SharedPreferences preferences;
    private Gson gson;
    private List<RaceHistory> raceHistoryList;
    
    private static final String PREFS_NAME = "CarRacingPrefs";
    private static final String KEY_RACE_HISTORY = "race_history";
    private static final int MAX_HISTORY_SIZE = 100; // Limit to prevent storage bloat
    
    private RaceHistoryManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.raceHistoryList = new ArrayList<>();
        loadRaceHistory();
    }
    
    public static synchronized RaceHistoryManager getInstance(Context context) {
        if (instance == null) {
            instance = new RaceHistoryManager(context);
        }
        return instance;
    }
    
    // Add new race result to history
    public void addRaceResult(String playerName, String selectedCarName, String winnerCarName, 
                             int betAmount, int winnings, boolean playerWon, int playerBalanceAfter) {
        RaceHistory raceHistory = new RaceHistory(
            playerName, selectedCarName, winnerCarName, 
            betAmount, winnings, playerWon, playerBalanceAfter
        );
        
        raceHistoryList.add(0, raceHistory); // Add to beginning (most recent first)
        
        // Limit history size
        if (raceHistoryList.size() > MAX_HISTORY_SIZE) {
            raceHistoryList = raceHistoryList.subList(0, MAX_HISTORY_SIZE);
        }
        
        saveRaceHistory();
    }
    
    // Get all race history
    public List<RaceHistory> getAllHistory() {
        return new ArrayList<>(raceHistoryList);
    }
    
    // Get race history for specific player
    public List<RaceHistory> getPlayerHistory(String playerName) {
        List<RaceHistory> playerHistory = new ArrayList<>();
        for (RaceHistory history : raceHistoryList) {
            if (history.getPlayerName().equals(playerName)) {
                playerHistory.add(history);
            }
        }
        return playerHistory;
    }
    
    // Get recent races (last N races)
    public List<RaceHistory> getRecentRaces(int count) {
        if (raceHistoryList.size() <= count) {
            return new ArrayList<>(raceHistoryList);
        }
        return new ArrayList<>(raceHistoryList.subList(0, count));
    }
    
    // Get statistics for specific player
    public PlayerStatistics getPlayerStatistics(String playerName) {
        List<RaceHistory> playerHistory = getPlayerHistory(playerName);
        
        int totalRaces = playerHistory.size();
        int wins = 0;
        int totalBetAmount = 0;
        int totalWinnings = 0;
        int biggestWin = 0;
        int biggestLoss = 0;
        String favoriteCar = "";
        Map<String, Integer> carSelectionCount = new HashMap<>();
        
        for (RaceHistory history : playerHistory) {
            if (history.isPlayerWon()) {
                wins++;
                int netWin = history.getWinnings() - history.getBetAmount();
                if (netWin > biggestWin) {
                    biggestWin = netWin;
                }
                totalWinnings += history.getWinnings();
            } else {
                if (history.getBetAmount() > biggestLoss) {
                    biggestLoss = history.getBetAmount();
                }
            }
            
            totalBetAmount += history.getBetAmount();
            
            // Track car selection frequency
            String carName = history.getSelectedCarName();
            carSelectionCount.put(carName, carSelectionCount.getOrDefault(carName, 0) + 1);
        }
        
        // Find favorite car
        int maxSelections = 0;
        for (Map.Entry<String, Integer> entry : carSelectionCount.entrySet()) {
            if (entry.getValue() > maxSelections) {
                maxSelections = entry.getValue();
                favoriteCar = entry.getKey();
            }
        }
        
        double winRate = totalRaces > 0 ? (double) wins / totalRaces * 100 : 0;
        int netProfit = totalWinnings - totalBetAmount;
        
        return new PlayerStatistics(
            playerName, totalRaces, wins, winRate, totalBetAmount, 
            totalWinnings, netProfit, biggestWin, biggestLoss, favoriteCar
        );
    }
    
    // Clear all history (for testing or reset)
    public void clearHistory() {
        raceHistoryList.clear();
        saveRaceHistory();
    }
    
    // Clear history for specific player
    public void clearPlayerHistory(String playerName) {
        raceHistoryList.removeIf(history -> history.getPlayerName().equals(playerName));
        saveRaceHistory();
    }
    
    // Save to SharedPreferences
    private void saveRaceHistory() {
        try {
            String json = gson.toJson(raceHistoryList);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_RACE_HISTORY, json);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Load from SharedPreferences
    private void loadRaceHistory() {
        try {
            String json = preferences.getString(KEY_RACE_HISTORY, "");
            if (!json.isEmpty()) {
                Type listType = new TypeToken<List<RaceHistory>>(){}.getType();
                List<RaceHistory> loadedHistory = gson.fromJson(json, listType);
                if (loadedHistory != null) {
                    raceHistoryList = loadedHistory;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            raceHistoryList = new ArrayList<>();
        }
    }
    
    // Player Statistics inner class
    public static class PlayerStatistics {
        private String playerName;
        private int totalRaces;
        private int wins;
        private double winRate;
        private int totalBetAmount;
        private int totalWinnings;
        private int netProfit;
        private int biggestWin;
        private int biggestLoss;
        private String favoriteCar;
        
        public PlayerStatistics(String playerName, int totalRaces, int wins, double winRate,
                               int totalBetAmount, int totalWinnings, int netProfit,
                               int biggestWin, int biggestLoss, String favoriteCar) {
            this.playerName = playerName;
            this.totalRaces = totalRaces;
            this.wins = wins;
            this.winRate = winRate;
            this.totalBetAmount = totalBetAmount;
            this.totalWinnings = totalWinnings;
            this.netProfit = netProfit;
            this.biggestWin = biggestWin;
            this.biggestLoss = biggestLoss;
            this.favoriteCar = favoriteCar;
        }
        
        // Getters
        public String getPlayerName() { return playerName; }
        public int getTotalRaces() { return totalRaces; }
        public int getWins() { return wins; }
        public int getLosses() { return totalRaces - wins; }
        public double getWinRate() { return winRate; }
        public int getTotalBetAmount() { return totalBetAmount; }
        public int getTotalWinnings() { return totalWinnings; }
        public int getNetProfit() { return netProfit; }
        public int getBiggestWin() { return biggestWin; }
        public int getBiggestLoss() { return biggestLoss; }
        public String getFavoriteCar() { return favoriteCar; }
        
        public String getFormattedWinRate() {
            return String.format("%.1f%%", winRate);
        }
        
        public boolean isProfitable() {
            return netProfit > 0;
        }
    }
}