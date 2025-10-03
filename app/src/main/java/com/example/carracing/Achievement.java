package com.example.carracing;

public class Achievement {
    public enum Type {
        RACE_COUNT,      // Total races completed
        WIN_COUNT,       // Total wins
        WIN_STREAK,      // Consecutive wins
        BIG_WIN,         // Single big win amount
        PROFIT,          // Total profit accumulated
        CAR_LOYALTY,     // Races with same car
        BALANCE,         // Balance milestone
        LUCKY,           // Win against odds
        COMEBACK        // Win after big loss
    }
    
    public enum Tier {
        BRONZE(1, "🥉"),
        SILVER(2, "🥈"), 
        GOLD(3, "🥇"),
        PLATINUM(4, "💎"),
        LEGENDARY(5, "👑");
        
        private final int level;
        private final String emoji;
        
        Tier(int level, String emoji) {
            this.level = level;
            this.emoji = emoji;
        }
        
        public int getLevel() { return level; }
        public String getEmoji() { return emoji; }
    }
    
    private int id;
    private String name;
    private String description;
    private Type type;
    private Tier tier;
    private int targetValue;
    private int currentValue;
    private boolean isUnlocked;
    private long unlockedTimestamp;
    private int rewardCoins;
    
    public Achievement(int id, String name, String description, Type type, Tier tier, 
                      int targetValue, int rewardCoins) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.tier = tier;
        this.targetValue = targetValue;
        this.rewardCoins = rewardCoins;
        this.currentValue = 0;
        this.isUnlocked = false;
        this.unlockedTimestamp = 0;
    }
    
    // Constructor for loading from storage
    public Achievement(int id, String name, String description, Type type, Tier tier, 
                      int targetValue, int currentValue, boolean isUnlocked, 
                      long unlockedTimestamp, int rewardCoins) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.tier = tier;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
        this.isUnlocked = isUnlocked;
        this.unlockedTimestamp = unlockedTimestamp;
        this.rewardCoins = rewardCoins;
    }
    
    // Progress management
    public void updateProgress(int value) {
        if (!isUnlocked) {
            this.currentValue = Math.max(this.currentValue, value);
            if (this.currentValue >= targetValue) {
                unlock();
            }
        }
    }
    
    public void incrementProgress(int amount) {
        if (!isUnlocked) {
            this.currentValue += amount;
            if (this.currentValue >= targetValue) {
                unlock();
            }
        }
    }
    
    private void unlock() {
        this.isUnlocked = true;
        this.unlockedTimestamp = System.currentTimeMillis();
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    
    public Tier getTier() { return tier; }
    public void setTier(Tier tier) { this.tier = tier; }
    
    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }
    
    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int currentValue) { this.currentValue = currentValue; }
    
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { this.isUnlocked = unlocked; }
    
    public long getUnlockedTimestamp() { return unlockedTimestamp; }
    public void setUnlockedTimestamp(long unlockedTimestamp) { this.unlockedTimestamp = unlockedTimestamp; }
    
    public int getRewardCoins() { return rewardCoins; }
    public void setRewardCoins(int rewardCoins) { this.rewardCoins = rewardCoins; }
    
    // Utility methods
    public double getProgressPercentage() {
        if (targetValue == 0) return 0;
        return Math.min(100.0, (double) currentValue / targetValue * 100);
    }
    
    public String getProgressText() {
        return currentValue + "/" + targetValue;
    }
    
    public String getFormattedUnlockDate() {
        if (!isUnlocked) return "";
        return android.text.format.DateFormat.format("MMM dd, yyyy", unlockedTimestamp).toString();
    }
    
    public String getDisplayName() {
        return tier.getEmoji() + " " + name;
    }
    
    public String getFullDescription() {
        String baseDesc = description;
        if (rewardCoins > 0) {
            baseDesc += "\nReward: " + rewardCoins + " coins";
        }
        return baseDesc;
    }
    
    @Override
    public String toString() {
        return "Achievement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", tier=" + tier +
                ", currentValue=" + currentValue +
                ", targetValue=" + targetValue +
                ", isUnlocked=" + isUnlocked +
                '}';
    }
}