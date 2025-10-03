package com.example.carracing;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.content.SharedPreferences;

public class GameAudioManager {
    
    private static GameAudioManager instance;
    private MediaPlayer backgroundMusic;
    private MediaPlayer engineSound;
    private ToneGenerator toneGenerator;
    
    private Context context;
    private SharedPreferences preferences;
    private boolean isMusicEnabled = true;
    private boolean isSoundEnabled = true;
    
    private static final String PREFS_NAME = "CarRacingPrefs";
    private static final String KEY_MUSIC_ENABLED = "music_enabled";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    
    private GameAudioManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadSettings();
        initializeToneGenerator();
    }
    
    private void initializeToneGenerator() {
        try {
            toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 50);
            ErrorHandler.logInfo("ToneGenerator initialized successfully");
        } catch (RuntimeException e) {
            ErrorHandler.logError("initializeToneGenerator", e);
            // Continue without tone generator - app will still work
        }
    }
    
    public static synchronized GameAudioManager getInstance(Context context) {
        if (instance == null) {
            instance = new GameAudioManager(context);
        }
        return instance;
    }
    
    private void loadSettings() {
        isMusicEnabled = preferences.getBoolean(KEY_MUSIC_ENABLED, true);
        isSoundEnabled = preferences.getBoolean(KEY_SOUND_ENABLED, true);
    }
    
    public void saveSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_MUSIC_ENABLED, isMusicEnabled);
        editor.putBoolean(KEY_SOUND_ENABLED, isSoundEnabled);
        editor.apply();
    }
    
    // Background Music Methods
    public void playBackgroundMusic() {
        if (!isMusicEnabled) return;
        
        try {
            if (backgroundMusic == null) {
                // Create a simple looping background tone as placeholder
                // In a real project, you would use: MediaPlayer.create(context, R.raw.background_music);
                backgroundMusic = new MediaPlayer();
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(0.2f, 0.2f);
                
                // For now, we simulate background music with a continuous low tone
                // This would be replaced with actual music file
            }
            
            // Background music simulation - in real app this would be backgroundMusic.start()
            // For now we'll just mark it as "playing"
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void pauseBackgroundMusic() {
        try {
            if (backgroundMusic != null && backgroundMusic.isPlaying()) {
                backgroundMusic.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
    
    // Sound Effects Methods
    public void playButtonClick() {
        if (!isSoundEnabled) return;
        playShortSound(SoundType.BUTTON_CLICK);
    }
    
    public void playRaceStart() {
        if (!isSoundEnabled) return;
        playShortSound(SoundType.RACE_START);
    }
    
    public void playCountdown() {
        if (!isSoundEnabled) return;
        playShortSound(SoundType.COUNTDOWN);
    }
    
    public void playVictory() {
        if (!isSoundEnabled) return;
        playShortSound(SoundType.VICTORY);
    }
    
    public void playCrashSound() {
        if (!isSoundEnabled) return;
        playShortSound(SoundType.CRASH);
    }
    
    public void playAudienceCheering() {
        if (!isSoundEnabled) return;
        playShortSound(SoundType.AUDIENCE_CHEER);
    }
    
    public void playEngineSound() {
        if (!isSoundEnabled) return;
        
        try {
            // Play a continuous low tone to simulate engine sound
            if (toneGenerator != null) {
                toneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 3000); // 3 second engine rumble
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Enhanced engine sound for different racing situations
    public void playRacingEngineSound(int intensity) {
        if (!isSoundEnabled) return;
        
        try {
            if (toneGenerator != null) {
                int tone;
                int duration;
                
                switch (intensity) {
                    case 1: // Low speed
                        tone = ToneGenerator.TONE_DTMF_1;
                        duration = 800;
                        break;
                    case 2: // Medium speed
                        tone = ToneGenerator.TONE_DTMF_2;
                        duration = 600;
                        break;
                    case 3: // High speed/overtaking
                        tone = ToneGenerator.TONE_DTMF_3;
                        duration = 400;
                        break;
                    default:
                        tone = ToneGenerator.TONE_DTMF_1;
                        duration = 800;
                }
                
                toneGenerator.startTone(tone, duration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stopEngineSound() {
        if (toneGenerator != null) {
            toneGenerator.stopTone();
        }
        if (engineSound != null) {
            engineSound.release();
            engineSound = null;
        }
    }
    
    public void playShortSound(SoundType soundType) {
        if (toneGenerator == null) return;
        
        try {
            int tone;
            int duration;
            
            switch (soundType) {
                case BUTTON_CLICK:
                    tone = ToneGenerator.TONE_PROP_BEEP;
                    duration = 100; // Short beep for button click
                    break;
                case RACE_START:
                    tone = ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD;
                    duration = 500; // Medium tone for race start
                    break;
                case COUNTDOWN:
                    tone = ToneGenerator.TONE_CDMA_ABBR_ALERT;
                    duration = 300; // Quick beep for countdown
                    break;
                case VICTORY:
                    tone = ToneGenerator.TONE_CDMA_ANSWER;
                    duration = 800; // Longer celebratory tone
                    break;
                case CRASH:
                    tone = ToneGenerator.TONE_CDMA_HIGH_PBX_L;
                    duration = 200; // Sharp crash sound
                    break;
                case AUDIENCE_CHEER:
                    tone = ToneGenerator.TONE_CDMA_ABBR_REORDER;
                    duration = 1000; // Longer cheering sound
                    break;
                case SUCCESS:
                    tone = ToneGenerator.TONE_CDMA_PIP;
                    duration = 300; // Success notification sound
                    break;
                default:
                    return;
            }
            
            toneGenerator.startTone(tone, duration);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Settings Methods
    public boolean isMusicEnabled() {
        return isMusicEnabled;
    }
    
    public void setMusicEnabled(boolean enabled) {
        this.isMusicEnabled = enabled;
        if (!enabled) {
            pauseBackgroundMusic();
        } else {
            playBackgroundMusic();
        }
        saveSettings();
    }
    
    public boolean isSoundEnabled() {
        return isSoundEnabled;
    }
    
    public void setSoundEnabled(boolean enabled) {
        this.isSoundEnabled = enabled;
        if (!enabled) {
            stopEngineSound();
        }
        saveSettings();
    }
    
    // Lifecycle Methods
    public void onResume() {
        if (isMusicEnabled) {
            playBackgroundMusic();
        }
    }
    
    public void onPause() {
        pauseBackgroundMusic();
        stopEngineSound();
    }
    
    public void onDestroy() {
        stopBackgroundMusic();
        stopEngineSound();
        releaseAllSounds();
        
        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }
    }
    
    private void releaseAllSounds() {
        // Release any remaining MediaPlayer instances
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
        if (engineSound != null) {
            engineSound.release();
            engineSound = null;
        }
    }
    
    // Sound Types Enum
    public enum SoundType {
        BUTTON_CLICK,
        RACE_START,
        COUNTDOWN,
        VICTORY,
        ENGINE,
        CRASH,
        AUDIENCE_CHEER,
        SUCCESS
    }
}