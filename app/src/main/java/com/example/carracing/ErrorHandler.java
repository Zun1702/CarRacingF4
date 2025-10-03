package com.example.carracing;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;
import android.util.Log;

public class ErrorHandler {
    
    private static final String TAG = "CarRacingGame";
    
    public static void showErrorDialog(Context context, String title, String message) {
        if (context == null) return;
        
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("⚠️ " + title)
                   .setMessage(message)
                   .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                   .setCancelable(false)
                   .show();
        } catch (Exception e) {
            // Fallback to toast if dialog fails
            showErrorToast(context, message);
            logError("showErrorDialog", e);
        }
    }
    
    public static void showErrorToast(Context context, String message) {
        if (context == null) return;
        
        try {
            Toast.makeText(context, "❌ " + message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            logError("showErrorToast", e);
        }
    }
    
    public static void showSuccessToast(Context context, String message) {
        if (context == null) return;
        
        try {
            Toast.makeText(context, "✅ " + message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            logError("showSuccessToast", e);
        }
    }
    
    public static void showInfoDialog(Context context, String title, String message) {
        if (context == null) return;
        
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("ℹ️ " + title)
                   .setMessage(message)
                   .setPositiveButton("Got it!", (dialog, which) -> dialog.dismiss())
                   .show();
        } catch (Exception e) {
            showErrorToast(context, message);
            logError("showInfoDialog", e);
        }
    }
    
    public static void handleAudioError(Context context, Exception e) {
        logError("AudioError", e);
        showErrorToast(context, "Audio system error. Please check your device's sound settings.");
    }
    
    public static void handleStorageError(Context context, Exception e) {
        logError("StorageError", e);
        showErrorToast(context, "Unable to save data. Please check storage permissions.");
    }
    
    public static void handleNetworkError(Context context, Exception e) {
        logError("NetworkError", e);
        showErrorToast(context, "Network connection issue. Some features may be limited.");
    }
    
    public static void logError(String source, Exception e) {
        Log.e(TAG, "Error in " + source + ": " + e.getMessage(), e);
    }
    
    public static void logInfo(String message) {
        Log.i(TAG, message);
    }
    
    public static void logDebug(String message) {
        Log.d(TAG, message);
    }
    
    // Validation helpers
    public static boolean validatePlayerName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() >= 2;
    }
    
    public static boolean validateBetAmount(int amount, int balance) {
        return amount > 0 && amount <= balance && amount >= 10; // Minimum bet 10 coins
    }
    
    public static String getValidationErrorMessage(String fieldName, String value) {
        if (value == null || value.trim().isEmpty()) {
            return fieldName + " cannot be empty";
        }
        
        if (fieldName.equals("Player Name") && value.trim().length() < 2) {
            return "Player name must be at least 2 characters long";
        }
        
        return null;
    }
}