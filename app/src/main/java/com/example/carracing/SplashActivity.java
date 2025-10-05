package com.example.carracing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    
    private ImageView ivF1Logo, ivRacingCarLogo;
    private TextView tvGameTitle, tvSubtitle, tvLoadingText;
    private ProgressBar pbLoading;
    private Handler handler;
    
    private final int SPLASH_DURATION = 3000; // 3 seconds
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        initializeViews();
        startAnimations();
        startLoadingSimulation();
    }
    
    private void initializeViews() {
        ivF1Logo = findViewById(R.id.ivF1Logo);
        tvGameTitle = findViewById(R.id.tvGameTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvLoadingText = findViewById(R.id.tvLoadingText);
        pbLoading = findViewById(R.id.pbLoading);
        
        handler = new Handler();
    }
    
    private void startAnimations() {
        // F1 Logo fade in and scale animation
        if (ivF1Logo != null) {
            Animation f1FadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            f1FadeIn.setDuration(1000);
            ivF1Logo.startAnimation(f1FadeIn);
            ivF1Logo.setAlpha(1.0f);
        }
        
        // Racing Car Logo slide up animation with delay
        if (ivRacingCarLogo != null) {
            Animation racingCarSlideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
            racingCarSlideUp.setDuration(1200);
            racingCarSlideUp.setStartOffset(500);
            ivRacingCarLogo.startAnimation(racingCarSlideUp);
            ivRacingCarLogo.setAlpha(1.0f);
        }
        
        // Title fade in animation
        if (tvGameTitle != null) {
            Animation titleFadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            titleFadeIn.setDuration(1000);
            titleFadeIn.setStartOffset(1000);
            tvGameTitle.startAnimation(titleFadeIn);
            tvGameTitle.setAlpha(1.0f);
        }
        
        // Subtitle fade in animation
        if (tvSubtitle != null) {
            Animation subtitleFadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            subtitleFadeIn.setDuration(800);
            subtitleFadeIn.setStartOffset(1500);
            tvSubtitle.startAnimation(subtitleFadeIn);
            tvSubtitle.setAlpha(1.0f);
        }
        
        // Loading text blink animation
        if (tvLoadingText != null) {
            Animation blink = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            blink.setDuration(800);
            blink.setRepeatMode(Animation.REVERSE);
            blink.setRepeatCount(Animation.INFINITE);
            blink.setStartOffset(2000);
            tvLoadingText.startAnimation(blink);
        }
    }
    
    private void startLoadingSimulation() {
        // Simulate loading progress
        Thread loadingThread = new Thread(() -> {
            for (int i = 0; i <= 100; i += 2) {
                final int progress = i;
                
                handler.post(() -> {
                    pbLoading.setProgress(progress);
                    updateLoadingText(progress);
                });
                
                try {
                    Thread.sleep(SPLASH_DURATION / 50); // Smooth progress update
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // Navigate to MainActivity after loading
            handler.postDelayed(this::navigateToMainActivity, 500);
        });
        
        loadingThread.start();
    }
    
    private void updateLoadingText(int progress) {
        String loadingText;
        if (progress < 30) {
            loadingText = "Loading assets...";
        } else if (progress < 60) {
            loadingText = "Preparing race tracks...";
        } else if (progress < 90) {
            loadingText = "Starting engines...";
        } else {
            loadingText = "Ready to race!";
        }
        
        tvLoadingText.setText(loadingText);
    }
    
    private void navigateToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
    
    @Override
    public void onBackPressed() {
        // Disable back button during splash
    }
}