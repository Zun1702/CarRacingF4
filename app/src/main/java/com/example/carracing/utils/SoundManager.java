package com.example.carracing.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.os.Build;
import com.example.carracing.R;
import java.util.HashMap;
import java.util.Map;

/**
 * Sound Manager for Car Racing Game
 * Handles background music, car engine sounds, and sound effects
 */
public class SoundManager {
    
    private static SoundManager instance;
    private Context context;
    
    // Background Music
    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer racingMusicPlayer;
    
    // Sound Effects
    private SoundPool soundPool;
    private Map<String, Integer> soundMap;
    
    // Engine Sounds
    private MediaPlayer[] enginePlayers;
    private boolean[] enginePlaying;
    
    // Settings
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;
    private float musicVolume = 0.7f;
    private float soundVolume = 0.8f;
    
    // Sound IDs
    public static final String SOUND_BUTTON_CLICK = "button_click";
    public static final String SOUND_CAR_START = "car_start";
    public static final String SOUND_CAR_HORN = "car_horn";
    public static final String SOUND_RACE_START = "race_start";
    public static final String SOUND_RACE_FINISH = "race_finish";
    public static final String SOUND_CAR_CRASH = "car_crash";
    public static final String SOUND_COIN_COLLECT = "coin_collect";
    public static final String SOUND_TIRE_SCREECH = "tire_screech";
    
    private SoundManager(Context context) {
        this.context = context.getApplicationContext();
        initializeSoundPool();
        initializeEnginePlayers();
        loadSounds();
    }
    
    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }
    
    /**
     * Initialize SoundPool for sound effects
     */
    private void initializeSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
        
        soundMap = new HashMap<>();
    }
    
    /**
     * Initialize engine sound players for 5 cars
     */
    private void initializeEnginePlayers() {
        enginePlayers = new MediaPlayer[5];
        enginePlaying = new boolean[5];
        
        for (int i = 0; i < 5; i++) {
            enginePlayers[i] = new MediaPlayer();
            enginePlaying[i] = false;
        }
    }
    
    /**
     * Load all sound effects
     */
    private void loadSounds() {
        try {
            // Load sound effects from MP3 files
            // Note: Uncomment these lines when MP3 files are added to /res/raw/
            
            // soundMap.put(SOUND_BUTTON_CLICK, soundPool.load(context, R.raw.button_click, 1));
            // soundMap.put(SOUND_CAR_START, soundPool.load(context, R.raw.car_start, 1));
            // soundMap.put(SOUND_CAR_HORN, soundPool.load(context, R.raw.car_horn, 1));
            // soundMap.put(SOUND_RACE_START, soundPool.load(context, R.raw.race_start, 1));
            // soundMap.put(SOUND_RACE_FINISH, soundPool.load(context, R.raw.race_finish, 1));
            // soundMap.put(SOUND_CAR_CRASH, soundPool.load(context, R.raw.car_crash, 1));
            // soundMap.put(SOUND_COIN_COLLECT, soundPool.load(context, R.raw.coin_collect, 1));
            // soundMap.put(SOUND_TIRE_SCREECH, soundPool.load(context, R.raw.tire_screech, 1));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Play background music for main menu
     */
    public void playBackgroundMusic() {
        android.util.Log.d("SoundManager", "playBackgroundMusic called. Music enabled: " + musicEnabled);
        
        if (!musicEnabled) {
            android.util.Log.d("SoundManager", "Background music disabled, not playing");
            return;
        }
        
        try {
            if (backgroundMusicPlayer == null) {
                android.util.Log.d("SoundManager", "Creating MediaPlayer for soundmainacitvity.mp3");
                backgroundMusicPlayer = MediaPlayer.create(context, R.raw.soundmainacitvity);
                android.util.Log.d("SoundManager", "Background MediaPlayer created: " + (backgroundMusicPlayer != null));
                
                if (backgroundMusicPlayer != null) {
                    // Set up seamless looping
                    backgroundMusicPlayer.setLooping(true);
                    
                    // Set up listener for better loop handling
                    backgroundMusicPlayer.setOnCompletionListener(mp -> {
                        android.util.Log.d("SoundManager", "Background music completed, restarting...");
                        if (musicEnabled) {
                            mp.start();
                        }
                    });
                    
                    backgroundMusicPlayer.setOnErrorListener((mp, what, extra) -> {
                        android.util.Log.e("SoundManager", "Background MediaPlayer error: " + what + ", extra: " + extra);
                        return false;
                    });
                }
            }
            
            if (backgroundMusicPlayer != null && !backgroundMusicPlayer.isPlaying()) {
                backgroundMusicPlayer.setVolume(musicVolume, musicVolume);
                backgroundMusicPlayer.start();
                android.util.Log.d("SoundManager", "Background music started successfully");
            } else {
                android.util.Log.d("SoundManager", "Background music player null or already playing");
            }
        } catch (Exception e) {
            android.util.Log.e("SoundManager", "Error playing background music", e);
            e.printStackTrace();
        }
    }
    
    /**
     * Play racing music during races
     */
    public void playRacingMusic() {
        android.util.Log.d("SoundManager", "playRacingMusic called. Music enabled: " + musicEnabled);
        
        if (!musicEnabled) {
            android.util.Log.d("SoundManager", "Music disabled, not playing");
            return;
        }
        
        stopBackgroundMusic();
        
        try {
            if (racingMusicPlayer == null) {
                android.util.Log.d("SoundManager", "Creating MediaPlayer for soundrace.mp3");
                racingMusicPlayer = MediaPlayer.create(context, R.raw.soundrace);
                android.util.Log.d("SoundManager", "MediaPlayer created: " + (racingMusicPlayer != null));
                
                if (racingMusicPlayer != null) {
                    // Set up seamless looping
                    racingMusicPlayer.setLooping(true);
                    
                    // Set up listener for better loop handling
                    racingMusicPlayer.setOnCompletionListener(mp -> {
                        android.util.Log.d("SoundManager", "Music completed, restarting...");
                        if (musicEnabled) {
                            mp.start();
                        }
                    });
                    
                    racingMusicPlayer.setOnErrorListener((mp, what, extra) -> {
                        android.util.Log.e("SoundManager", "MediaPlayer error: " + what + ", extra: " + extra);
                        return false;
                    });
                }
            }
            
            if (racingMusicPlayer != null && !racingMusicPlayer.isPlaying()) {
                racingMusicPlayer.setVolume(musicVolume * 0.7f, musicVolume * 0.7f); // Quieter for better experience
                racingMusicPlayer.start();
                android.util.Log.d("SoundManager", "Racing music started successfully");
            } else {
                android.util.Log.d("SoundManager", "Music player null or already playing");
            }
        } catch (Exception e) {
            android.util.Log.e("SoundManager", "Error playing racing music", e);
            e.printStackTrace();
        }
    }
    
    /**
     * Start engine sound for specific car
     * @param carIndex Car index (0-4)
     * @param rpm Engine RPM (affects pitch and volume)
     */
    public void startEngineSound(int carIndex, float rpm) {
        if (!soundEnabled || carIndex < 0 || carIndex >= 5) return;
        
        try {
            MediaPlayer player = enginePlayers[carIndex];
            
            if (!enginePlaying[carIndex]) {
                // player = MediaPlayer.create(context, R.raw.engine_sound);
                // For now, create silent player until MP3 file is added
                if (player == null) {
                    player = new MediaPlayer();
                    enginePlayers[carIndex] = player;
                }
                
                player.setLooping(true);
                enginePlaying[carIndex] = true;
            }
            
            // Adjust volume based on RPM
            float volume = Math.min(1.0f, (rpm / 100.0f) * soundVolume);
            player.setVolume(volume, volume);
            
            // Note: Pitch adjustment requires more complex implementation
            // Can be done with audio effects or different sound files
            
            if (!player.isPlaying()) {
                // player.start(); // Uncomment when MP3 is added
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Stop engine sound for specific car
     */
    public void stopEngineSound(int carIndex) {
        if (carIndex < 0 || carIndex >= 5) return;
        
        try {
            MediaPlayer player = enginePlayers[carIndex];
            if (player != null && player.isPlaying()) {
                player.pause();
                enginePlaying[carIndex] = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Update engine sound based on car speed
     * @param carIndex Car index
     * @param speed Car speed (0-100)
     */
    public void updateEngineSound(int carIndex, float speed) {
        if (!soundEnabled) return;
        
        float rpm = 30 + (speed * 0.7f); // Base RPM + speed factor
        startEngineSound(carIndex, rpm);
    }
    
    /**
     * Play sound effect
     */
    public void playSound(String soundId) {
        android.util.Log.d("SoundManager", "playSound called: " + soundId + ", enabled: " + soundEnabled);
        
        if (!soundEnabled) return;
        
        // Create nice button click and countdown sounds
        if (SOUND_BUTTON_CLICK.equals(soundId) || SOUND_RACE_START.equals(soundId)) {
            try {
                // Create pleasant beep sounds
                android.media.ToneGenerator toneGen = new android.media.ToneGenerator(
                    android.media.AudioManager.STREAM_MUSIC, 
                    80  // Reduced volume for pleasant sound
                );
                if (SOUND_RACE_START.equals(soundId)) {
                    // Different tone for race start
                    toneGen.startTone(android.media.ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
                } else {
                    // Pleasant button click sound
                    toneGen.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 100);
                }
                android.util.Log.d("SoundManager", "Button/countdown sound played for: " + soundId);
            } catch (Exception e) {
                android.util.Log.e("SoundManager", "Error playing sound", e);
            }
            return;
        }
        
        Integer soundResId = soundMap.get(soundId);
        if (soundResId != null) {
            soundPool.play(soundResId, soundVolume, soundVolume, 1, 0, 1.0f);
        } else {
            android.util.Log.d("SoundManager", "Sound not found in map: " + soundId);
        }
    }
    
    /**
     * Play sound effect with custom volume
     */
    public void playSound(String soundId, float volume) {
        if (!soundEnabled) return;
        
        Integer soundResId = soundMap.get(soundId);
        if (soundResId != null) {
            float adjustedVolume = volume * soundVolume;
            soundPool.play(soundResId, adjustedVolume, adjustedVolume, 1, 0, 1.0f);
        }
    }
    
    /**
     * Stop background music
     */
    public void stopBackgroundMusic() {
        android.util.Log.d("SoundManager", "stopBackgroundMusic called");
        if (backgroundMusicPlayer != null && backgroundMusicPlayer.isPlaying()) {
            backgroundMusicPlayer.pause();
            android.util.Log.d("SoundManager", "Background music stopped");
        }
    }
    
    /**
     * Stop racing music
     */
    public void stopRacingMusic() {
        android.util.Log.d("SoundManager", "stopRacingMusic called");
        if (racingMusicPlayer != null && racingMusicPlayer.isPlaying()) {
            racingMusicPlayer.pause();
            android.util.Log.d("SoundManager", "Racing music stopped");
        }
    }
    
    /**
     * Stop all engine sounds
     */
    public void stopAllEngineSounds() {
        for (int i = 0; i < 5; i++) {
            stopEngineSound(i);
        }
    }
    
    /**
     * Stop all sounds
     */
    public void stopAllSounds() {
        stopBackgroundMusic();
        stopRacingMusic();
        stopAllEngineSounds();
        if (soundPool != null) {
            soundPool.autoPause();
        }
    }
    
    /**
     * Resume all sounds
     */
    public void resumeAllSounds() {
        if (soundPool != null) {
            soundPool.autoResume();
        }
    }
    
    /**
     * Release all resources
     */
    public void release() {
        try {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.release();
                backgroundMusicPlayer = null;
            }
            
            if (racingMusicPlayer != null) {
                racingMusicPlayer.release();
                racingMusicPlayer = null;
            }
            
            for (int i = 0; i < enginePlayers.length; i++) {
                if (enginePlayers[i] != null) {
                    enginePlayers[i].release();
                    enginePlayers[i] = null;
                }
            }
            
            if (soundPool != null) {
                soundPool.release();
                soundPool = null;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Getters and Setters
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
        if (!musicEnabled) {
            stopBackgroundMusic();
            stopRacingMusic();
        }
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        if (!soundEnabled) {
            stopAllEngineSounds();
        }
    }
    
    public float getMusicVolume() {
        return musicVolume;
    }
    
    public void setMusicVolume(float musicVolume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, musicVolume));
        
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setVolume(this.musicVolume, this.musicVolume);
        }
        if (racingMusicPlayer != null) {
            racingMusicPlayer.setVolume(this.musicVolume * 0.8f, this.musicVolume * 0.8f);
        }
    }
    
    public float getSoundVolume() {
        return soundVolume;
    }
    
    public void setSoundVolume(float soundVolume) {
        this.soundVolume = Math.max(0.0f, Math.min(1.0f, soundVolume));
    }
    
    /**
     * Enable audio after MP3 files are added
     * Call this method to activate all audio features
     */
    public void enableAudioFiles() {
        try {
            // Enable background music (when available)
            // if (backgroundMusicPlayer != null) {
            //     backgroundMusicPlayer.release();
            //     backgroundMusicPlayer = MediaPlayer.create(context, R.raw.background_music);
            // }
            
            // Enable racing music with soundrace.mp3
            if (racingMusicPlayer != null) {
                racingMusicPlayer.release();
                racingMusicPlayer = MediaPlayer.create(context, R.raw.soundrace);
            }
            
            // Load sound effects (when available)
            // soundMap.clear();
            // soundMap.put(SOUND_BUTTON_CLICK, soundPool.load(context, R.raw.button_click, 1));
            // soundMap.put(SOUND_CAR_START, soundPool.load(context, R.raw.car_start, 1));
            // soundMap.put(SOUND_CAR_HORN, soundPool.load(context, R.raw.car_horn, 1));
            // soundMap.put(SOUND_RACE_START, soundPool.load(context, R.raw.race_start, 1));
            // soundMap.put(SOUND_RACE_FINISH, soundPool.load(context, R.raw.race_finish, 1));
            // soundMap.put(SOUND_CAR_CRASH, soundPool.load(context, R.raw.car_crash, 1));
            // soundMap.put(SOUND_COIN_COLLECT, soundPool.load(context, R.raw.coin_collect, 1));
            // soundMap.put(SOUND_TIRE_SCREECH, soundPool.load(context, R.raw.tire_screech, 1));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}