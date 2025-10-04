package com.example.carracing;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchievementManager {
    private static AchievementManager instance;
    private Context context;
    private SharedPreferences preferences;
    private Gson gson;
    private List<Achievement> achievements;
    private Map<Integer, Achievement> achievementMap;
    
    private static final String PREFS_NAME = "CarRacingPrefs";
    private static final String KEY_ACHIEVEMENTS = "achievements";
    
    // Achievement IDs
    private static final int FIRST_RACE = 1;
    private static final int VETERAN_RACER = 2;
    private static final int RACING_LEGEND = 3;
    private static final int FIRST_WIN = 4;
    private static final int CHAMPION = 5;
    private static final int UNSTOPPABLE = 6;
    private static final int WIN_STREAK_3 = 7;
    private static final int WIN_STREAK_5 = 8;
    private static final int WIN_STREAK_10 = 9;
    private static final int BIG_WINNER = 10;
    private static final int JACKPOT = 11;
    private static final int MILLIONAIRE = 12;
    private static final int PROFITABLE = 13;
    private static final int RICH = 14;
    private static final int CAR_LOVER = 15;
    private static final int LUCKY_WINNER = 16;
    private static final int COMEBACK_KID = 17;
    
    private AchievementManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.achievements = new ArrayList<>();
        this.achievementMap = new HashMap<>();
        
        initializeAchievements();
        loadAchievements();
    }
    
    public static synchronized AchievementManager getInstance(Context context) {
        if (instance == null) {
            instance = new AchievementManager(context);
        }
        return instance;
    }
    
    private void initializeAchievements() {
        // Race count achievements
        achievements.add(new Achievement(FIRST_RACE, "First Race", "Complete your first race", 
            Achievement.Type.RACE_COUNT, Achievement.Tier.BRONZE, 1, 50));
        achievements.add(new Achievement(VETERAN_RACER, "Veteran Racer", "Complete 50 races", 
            Achievement.Type.RACE_COUNT, Achievement.Tier.SILVER, 50, 200));
        achievements.add(new Achievement(RACING_LEGEND, "Racing Legend", "Complete 200 races", 
            Achievement.Type.RACE_COUNT, Achievement.Tier.LEGENDARY, 200, 1000));
            
        // Win count achievements
        achievements.add(new Achievement(FIRST_WIN, "First Victory", "Win your first race", 
            Achievement.Type.WIN_COUNT, Achievement.Tier.BRONZE, 1, 100));
        achievements.add(new Achievement(CHAMPION, "Champion", "Win 25 races", 
            Achievement.Type.WIN_COUNT, Achievement.Tier.GOLD, 25, 500));
        achievements.add(new Achievement(UNSTOPPABLE, "Unstoppable", "Win 100 races", 
            Achievement.Type.WIN_COUNT, Achievement.Tier.PLATINUM, 100, 2000));
            
        // Win streak achievements
        achievements.add(new Achievement(WIN_STREAK_3, "Hot Streak", "Win 3 races in a row", 
            Achievement.Type.WIN_STREAK, Achievement.Tier.SILVER, 3, 150));
        achievements.add(new Achievement(WIN_STREAK_5, "On Fire", "Win 5 races in a row", 
            Achievement.Type.WIN_STREAK, Achievement.Tier.GOLD, 5, 300));
        achievements.add(new Achievement(WIN_STREAK_10, "Legendary Streak", "Win 10 races in a row", 
            Achievement.Type.WIN_STREAK, Achievement.Tier.LEGENDARY, 10, 1500));
            
        // Big win achievements
        achievements.add(new Achievement(BIG_WINNER, "Big Winner", "Win 500+ coins in a single race", 
            Achievement.Type.BIG_WIN, Achievement.Tier.SILVER, 500, 250));
        achievements.add(new Achievement(JACKPOT, "Jackpot!", "Win 1000+ coins in a single race", 
            Achievement.Type.BIG_WIN, Achievement.Tier.PLATINUM, 1000, 750));
            
        // Balance achievements  
        achievements.add(new Achievement(MILLIONAIRE, "Millionaire", "Reach 10,000 coins balance", 
            Achievement.Type.BALANCE, Achievement.Tier.LEGENDARY, 10000, 2500));
            
        // Profit achievements
        achievements.add(new Achievement(PROFITABLE, "Profitable", "Earn 1000+ coins net profit", 
            Achievement.Type.PROFIT, Achievement.Tier.GOLD, 1000, 400));
        achievements.add(new Achievement(RICH, "Rich Racer", "Earn 5000+ coins net profit", 
            Achievement.Type.PROFIT, Achievement.Tier.PLATINUM, 5000, 1200));
            
        // Special achievements
        achievements.add(new Achievement(CAR_LOVER, "Car Enthusiast", "Use the same car 20 times", 
            Achievement.Type.CAR_LOYALTY, Achievement.Tier.GOLD, 20, 300));
        achievements.add(new Achievement(LUCKY_WINNER, "Against All Odds", "Win with a car that has 2.5x+ odds", 
            Achievement.Type.LUCKY, Achievement.Tier.PLATINUM, 1, 800));
        achievements.add(new Achievement(COMEBACK_KID, "Comeback Kid", "Win after losing 500+ coins", 
            Achievement.Type.COMEBACK, Achievement.Tier.GOLD, 1, 600));
            
        // Build map for quick access
        for (Achievement achievement : achievements) {
            achievementMap.put(achievement.getId(), achievement);
        }
    }
    
    // Process race result and update achievements
    public List<Achievement> processRaceResult(RaceHistoryManager.PlayerStatistics stats, 
                                             RaceHistory lastRace, List<RaceHistory> recentHistory) {
        List<Achievement> newlyUnlocked = new ArrayList<>();
        
        // Race count achievements
        updateAchievement(FIRST_RACE, stats.getTotalRaces(), newlyUnlocked);
        updateAchievement(VETERAN_RACER, stats.getTotalRaces(), newlyUnlocked);
        updateAchievement(RACING_LEGEND, stats.getTotalRaces(), newlyUnlocked);
        
        // Win count achievements
        updateAchievement(FIRST_WIN, stats.getWins(), newlyUnlocked);
        updateAchievement(CHAMPION, stats.getWins(), newlyUnlocked);
        updateAchievement(UNSTOPPABLE, stats.getWins(), newlyUnlocked);
        
        // Win streak
        int currentStreak = calculateCurrentWinStreak(recentHistory);
        updateAchievement(WIN_STREAK_3, currentStreak, newlyUnlocked);
        updateAchievement(WIN_STREAK_5, currentStreak, newlyUnlocked);
        updateAchievement(WIN_STREAK_10, currentStreak, newlyUnlocked);
        
        // Big win achievements
        if (lastRace.isPlayerWon()) {
            int netWin = lastRace.getWinnings() - lastRace.getBetAmount();
            updateAchievement(BIG_WINNER, netWin, newlyUnlocked);
            updateAchievement(JACKPOT, netWin, newlyUnlocked);
        }
        
        // Balance achievements
        updateAchievement(MILLIONAIRE, lastRace.getPlayerBalanceAfter(), newlyUnlocked);
        
        // Profit achievements
        updateAchievement(PROFITABLE, stats.getNetProfit(), newlyUnlocked);
        updateAchievement(RICH, stats.getNetProfit(), newlyUnlocked);
        
        // Car loyalty achievement
        updateCarLoyaltyAchievement(recentHistory, newlyUnlocked);
        
        // Special achievements
        checkSpecialAchievements(lastRace, recentHistory, newlyUnlocked);
        
        if (!newlyUnlocked.isEmpty()) {
            saveAchievements();
        }
        
        return newlyUnlocked;
    }
    
    private void updateAchievement(int id, int value, List<Achievement> newlyUnlocked) {
        Achievement achievement = achievementMap.get(id);
        if (achievement != null && !achievement.isUnlocked()) {
            achievement.updateProgress(value);
            if (achievement.isUnlocked()) {
                newlyUnlocked.add(achievement);
            }
        }
    }
    
    private int calculateCurrentWinStreak(List<RaceHistory> recentHistory) {
        int streak = 0;
        for (RaceHistory race : recentHistory) {
            if (race.isPlayerWon()) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }
    
    private void updateCarLoyaltyAchievement(List<RaceHistory> recentHistory, List<Achievement> newlyUnlocked) {
        Map<String, Integer> carCounts = new HashMap<>();
        for (RaceHistory race : recentHistory) {
            String car = race.getSelectedCarName();
            carCounts.put(car, carCounts.getOrDefault(car, 0) + 1);
        }
        
        int maxCount = 0;
        for (int count : carCounts.values()) {
            maxCount = Math.max(maxCount, count);
        }
        
        updateAchievement(CAR_LOVER, maxCount, newlyUnlocked);
    }
    
    private void checkSpecialAchievements(RaceHistory lastRace, List<RaceHistory> recentHistory, 
                                        List<Achievement> newlyUnlocked) {
        // Lucky winner - win with high odds (assuming odds > 2.5 is high)
        if (lastRace.isPlayerWon()) {
            // This would need odds info from Car class - for now we'll skip
            // updateAchievement(LUCKY_WINNER, 1, newlyUnlocked);
        }
        
        // Comeback kid - win after big loss
        if (lastRace.isPlayerWon() && recentHistory.size() > 1) {
            RaceHistory previousRace = recentHistory.get(1); // Second most recent
            if (!previousRace.isPlayerWon() && previousRace.getBetAmount() >= 500) {
                updateAchievement(COMEBACK_KID, 1, newlyUnlocked);
            }
        }
    }
    
    // Get all achievements
    public List<Achievement> getAllAchievements() {
        return new ArrayList<>(achievements);
    }
    
    // Get unlocked achievements
    public List<Achievement> getUnlockedAchievements() {
        List<Achievement> unlocked = new ArrayList<>();
        for (Achievement achievement : achievements) {
            if (achievement.isUnlocked()) {
                unlocked.add(achievement);
            }
        }
        return unlocked;
    }
    
    // Get locked achievements  
    public List<Achievement> getLockedAchievements() {
        List<Achievement> locked = new ArrayList<>();
        for (Achievement achievement : achievements) {
            if (!achievement.isUnlocked()) {
                locked.add(achievement);
            }
        }
        return locked;
    }
    
    // Get achievement by ID
    public Achievement getAchievement(int id) {
        return achievementMap.get(id);
    }
    
    // Calculate total rewards from unlocked achievements
    public int getTotalRewards() {
        int total = 0;
        for (Achievement achievement : achievements) {
            if (achievement.isUnlocked()) {
                total += achievement.getRewardCoins();
            }
        }
        return total;
    }
    
    // Get achievement stats
    public AchievementStats getStats() {
        int total = achievements.size();
        int unlocked = getUnlockedAchievements().size();
        int totalRewards = getTotalRewards();
        
        return new AchievementStats(total, unlocked, totalRewards);
    }
    
    // Save achievements to SharedPreferences
    private void saveAchievements() {
        try {
            String json = gson.toJson(achievements);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_ACHIEVEMENTS, json);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Load achievements from SharedPreferences
    private void loadAchievements() {
        try {
            String json = preferences.getString(KEY_ACHIEVEMENTS, "");
            if (!json.isEmpty()) {
                Type listType = new TypeToken<List<Achievement>>(){}.getType();
                List<Achievement> loadedAchievements = gson.fromJson(json, listType);
                if (loadedAchievements != null) {
                    // Update existing achievements with loaded progress
                    for (Achievement loaded : loadedAchievements) {
                        Achievement existing = achievementMap.get(loaded.getId());
                        if (existing != null) {
                            existing.setCurrentValue(loaded.getCurrentValue());
                            existing.setUnlocked(loaded.isUnlocked());
                            existing.setUnlockedTimestamp(loaded.getUnlockedTimestamp());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Reset all achievements (for game reset functionality)
    public void resetAllAchievements() {
        try {
            // Reset all achievements to initial state
            for (Achievement achievement : achievements) {
                achievement.setCurrentValue(0);
                achievement.setUnlocked(false);
                achievement.setUnlockedTimestamp(0);
            }
            
            // Clear achievements from SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(KEY_ACHIEVEMENTS);
            editor.apply();
            
            // Save the reset state
            saveAchievements();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Achievement Stats inner class
    public static class AchievementStats {
        private int totalAchievements;
        private int unlockedAchievements;
        private int totalRewards;
        
        public AchievementStats(int totalAchievements, int unlockedAchievements, int totalRewards) {
            this.totalAchievements = totalAchievements;
            this.unlockedAchievements = unlockedAchievements;
            this.totalRewards = totalRewards;
        }
        
        public int getTotalAchievements() { return totalAchievements; }
        public int getUnlockedAchievements() { return unlockedAchievements; }
        public int getLockedAchievements() { return totalAchievements - unlockedAchievements; }
        public int getTotalRewards() { return totalRewards; }
        
        public double getCompletionPercentage() {
            if (totalAchievements == 0) return 0;
            return (double) unlockedAchievements / totalAchievements * 100;
        }
        
        public String getFormattedCompletion() {
            return String.format("%.1f%%", getCompletionPercentage());
        }
    }
}