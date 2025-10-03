package com.example.carracing;

import java.util.Date;

public class RaceHistory {
    private long id;
    private String playerName;
    private String selectedCarName;
    private String winnerCarName;
    private int betAmount;
    private int winnings;
    private boolean playerWon;
    private long timestamp;
    private int playerBalanceAfter;
    
    public RaceHistory(String playerName, String selectedCarName, String winnerCarName, 
                      int betAmount, int winnings, boolean playerWon, int playerBalanceAfter) {
        this.playerName = playerName;
        this.selectedCarName = selectedCarName;
        this.winnerCarName = winnerCarName;
        this.betAmount = betAmount;
        this.winnings = winnings;
        this.playerWon = playerWon;
        this.playerBalanceAfter = playerBalanceAfter;
        this.timestamp = System.currentTimeMillis();
        this.id = timestamp; // Simple ID using timestamp
    }
    
    // Constructor for loading from storage
    public RaceHistory(long id, String playerName, String selectedCarName, String winnerCarName, 
                      int betAmount, int winnings, boolean playerWon, long timestamp, int playerBalanceAfter) {
        this.id = id;
        this.playerName = playerName;
        this.selectedCarName = selectedCarName;
        this.winnerCarName = winnerCarName;
        this.betAmount = betAmount;
        this.winnings = winnings;
        this.playerWon = playerWon;
        this.timestamp = timestamp;
        this.playerBalanceAfter = playerBalanceAfter;
    }
    
    // Getters and setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public String getSelectedCarName() {
        return selectedCarName;
    }
    
    public void setSelectedCarName(String selectedCarName) {
        this.selectedCarName = selectedCarName;
    }
    
    public String getWinnerCarName() {
        return winnerCarName;
    }
    
    public void setWinnerCarName(String winnerCarName) {
        this.winnerCarName = winnerCarName;
    }
    
    public int getBetAmount() {
        return betAmount;
    }
    
    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }
    
    public int getWinnings() {
        return winnings;
    }
    
    public void setWinnings(int winnings) {
        this.winnings = winnings;
    }
    
    public boolean isPlayerWon() {
        return playerWon;
    }
    
    public void setPlayerWon(boolean playerWon) {
        this.playerWon = playerWon;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public int getPlayerBalanceAfter() {
        return playerBalanceAfter;
    }
    
    public void setPlayerBalanceAfter(int playerBalanceAfter) {
        this.playerBalanceAfter = playerBalanceAfter;
    }
    
    // Utility methods
    public Date getDate() {
        return new Date(timestamp);
    }
    
    public String getFormattedDate() {
        return android.text.format.DateFormat.format("MMM dd, yyyy HH:mm", timestamp).toString();
    }
    
    public int getNetResult() {
        return playerWon ? (winnings - betAmount) : (-betAmount);
    }
    
    @Override
    public String toString() {
        return "RaceHistory{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", selectedCarName='" + selectedCarName + '\'' +
                ", winnerCarName='" + winnerCarName + '\'' +
                ", betAmount=" + betAmount +
                ", winnings=" + winnings +
                ", playerWon=" + playerWon +
                ", timestamp=" + timestamp +
                ", playerBalanceAfter=" + playerBalanceAfter +
                '}';
    }
}