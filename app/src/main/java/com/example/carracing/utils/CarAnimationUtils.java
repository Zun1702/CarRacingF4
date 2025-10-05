package com.example.carracing.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.core.content.ContextCompat;
import com.example.carracing.R;

/**
 * Utility class for managing car animations
 */
public class CarAnimationUtils {
    
    /**
     * Set PNG racing red car to ImageView
     * @param context Application context
     * @param imageView Target ImageView
     */
    public static void setPngRacingRedCar(Context context, ImageView imageView) {
        imageView.setImageResource(R.drawable.racingcar_red);
    }
    
    /**
     * Set PNG racing green car to ImageView
     * @param context Application context
     * @param imageView Target ImageView
     */
    public static void setPngRacingGreenCar(Context context, ImageView imageView) {
        imageView.setImageResource(R.drawable.racingcar_green);
    }
    
    /**
     * Set PNG racing blue car to ImageView
     * @param context Application context
     * @param imageView Target ImageView
     */
    public static void setPngRacingBlueCar(Context context, ImageView imageView) {
        imageView.setImageResource(R.drawable.racingcar_blue);
    }
    
    /**
     * Set PNG racing orange car to ImageView
     * @param context Application context
     * @param imageView Target ImageView
     */
    public static void setPngRacingOrangeCar(Context context, ImageView imageView) {
        imageView.setImageResource(R.drawable.racingcar_orange);
    }
    
    /**
     * Set PNG racing purple car to ImageView
     * @param context Application context
     * @param imageView Target ImageView
     */
    public static void setPngRacingPurpleCar(Context context, ImageView imageView) {
        imageView.setImageResource(R.drawable.racingcar_purple);
    }
    
    /**
     * Set animated red car drawable to ImageView
     * @param context Application context
     * @param imageView Target ImageView
     * @param startAnimation Whether to start animation immediately
     */
    public static void setAnimatedRedCar(Context context, ImageView imageView, boolean startAnimation) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.car_red_animated);
        imageView.setImageDrawable(drawable);
        
        if (startAnimation && drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }
    
    /**
     * Set PNG racing car with animation effects
     * @param context Application context
     * @param imageView Target ImageView
     * @param enableEffects Whether to add rotation/shake effects
     */
    public static void setPngRacingWithEffects(Context context, ImageView imageView, boolean enableEffects) {
        imageView.setImageResource(R.drawable.racingcar_red);
        
        if (enableEffects) {
            // Add subtle shake/vibration effect to simulate engine
            ObjectAnimator shakeX = ObjectAnimator.ofFloat(imageView, "translationX", 0f, 2f, -2f, 0f);
            shakeX.setDuration(200);
            shakeX.setRepeatCount(ObjectAnimator.INFINITE);
            shakeX.start();
            
            ObjectAnimator shakeY = ObjectAnimator.ofFloat(imageView, "translationY", 0f, 1f, -1f, 0f);
            shakeY.setDuration(150);
            shakeY.setRepeatCount(ObjectAnimator.INFINITE);
            shakeY.start();
        }
    }

    /**
     * Set PNG racing car with racing-safe effects (no translationX interference)
     * @param context Application context
     * @param imageView Target ImageView
     * @param enableEffects Whether to add racing-safe effects
     * @param carColor Car color (0=red, 1=blue, 2=green, 3=orange, 4=purple)
     */
    public static void setPngRacingForRace(Context context, ImageView imageView, boolean enableEffects, int carColor) {
        // Set the appropriate car resource based on color
        switch (carColor) {
            case 0:
                imageView.setImageResource(R.drawable.racingcar_red);
                break;
            case 1:
                imageView.setImageResource(R.drawable.racingcar_blue);
                break;
            case 2:
                imageView.setImageResource(R.drawable.racingcar_green);
                break;
            case 3:
                imageView.setImageResource(R.drawable.racingcar_orange);
                break;
            case 4:
                imageView.setImageResource(R.drawable.racingcar_purple);
                break;
            default:
                imageView.setImageResource(R.drawable.racingcar_red);
                break;
        }
        
        if (enableEffects) {
            // Only Y-axis vibration to not interfere with race translationX
            ObjectAnimator shakeY = ObjectAnimator.ofFloat(imageView, "translationY", 0f, 1.5f, -1.5f, 0f);
            shakeY.setDuration(100);
            shakeY.setRepeatCount(ObjectAnimator.INFINITE);
            shakeY.start();
            
            // Scale pulsing for "engine power" effect (safe)
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 1.0f, 1.08f, 1.0f);
            scaleX.setDuration(600);
            scaleX.setRepeatCount(ObjectAnimator.INFINITE);
            scaleX.start();
            
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 1.0f, 1.08f, 1.0f);
            scaleY.setDuration(600);
            scaleY.setRepeatCount(ObjectAnimator.INFINITE);
            scaleY.start();
            
            // Rotation for dynamic effect (safe)
            ObjectAnimator rotation = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 2f, 0f, -2f, 0f);
            rotation.setDuration(300);
            rotation.setRepeatCount(ObjectAnimator.INFINITE);
            rotation.start();
        }
    }

    /**
     * Set PNG racing car with racing-safe effects for red car (backward compatibility)
     * @param context Application context
     * @param imageView Target ImageView
     * @param enableEffects Whether to add racing-safe effects
     */
    public static void setPngRacingForRace(Context context, ImageView imageView, boolean enableEffects) {
        setPngRacingForRace(context, imageView, enableEffects, 0); // Default to red car
    }
    
    /**
     * Add exhaust smoke effect behind the car
     * @param context Application context
     * @param carImageView The car ImageView
     */
    private static void addExhaustSmokeEffect(Context context, ImageView carImageView) {
        try {
            ViewGroup parent = (ViewGroup) carImageView.getParent();
            if (parent instanceof RelativeLayout || parent instanceof ViewGroup) {
                ImageView smokeView = new ImageView(context);
                smokeView.setImageResource(R.drawable.exhaust_smoke_animated);
                
                // Position smoke behind the car
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                );
                smokeView.setLayoutParams(params);
                
                // Make smoke smaller and more transparent
                smokeView.setAlpha(0.6f);
                smokeView.setScaleX(0.8f);
                smokeView.setScaleY(0.8f);
                
                // Add to parent
                parent.addView(smokeView, 0); // Add behind car
                
                // Position relative to car
                smokeView.setX(carImageView.getX() - 30);
                smokeView.setY(carImageView.getY() + 5);
                
                // Start smoke animation
                Drawable smokeDrawable = smokeView.getDrawable();
                if (smokeDrawable instanceof Animatable) {
                    ((Animatable) smokeDrawable).start();
                }
            }
        } catch (Exception e) {
            // Ignore if can't add smoke effect
        }
    }
    
    /**
     * Set static F1 red car drawable to ImageView
     * @param context Application context
     * @param imageView Target ImageView
     */
    public static void setF1RedCar(Context context, ImageView imageView) {
        imageView.setImageResource(R.drawable.car_red_f1);
    }
    
    /**
     * Set static red car drawable to ImageView (original design)
     * @param context Application context
     * @param imageView Target ImageView
     */
    public static void setStaticRedCar(Context context, ImageView imageView) {
        imageView.setImageResource(R.drawable.car_red);
    }
    
    /**
     * Start animation if drawable is animatable
     * @param imageView Target ImageView
     */
    public static void startCarAnimation(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }
    
    /**
     * Stop animation if drawable is animatable
     * @param imageView Target ImageView
     */
    public static void stopCarAnimation(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).stop();
        }
    }
    
    /**
     * Check if current drawable is animated
     * @param imageView Target ImageView
     * @return true if animated, false otherwise
     */
    public static boolean isAnimated(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        return drawable instanceof Animatable;
    }
    
    /**
     * Toggle between different car rendering options
     * @param context Application context
     * @param imageView Target ImageView
     * @param carType 0=static, 1=vector_animated, 2=png_racing, 3=png_with_effects, 4=png_racing_effects
     */
    public static void setCarType(Context context, ImageView imageView, int carType) {
        switch (carType) {
            case 0:
                setStaticRedCar(context, imageView);
                break;
            case 1:
                setAnimatedRedCar(context, imageView, true);
                break;
            case 2:
                setPngRacingRedCar(context, imageView);
                break;
            case 3:
                setPngRacingWithEffects(context, imageView, true);
                break;
            case 4:
                setPngRacingForRace(context, imageView, true);
                break;
            default:
                setPngRacingForRace(context, imageView, true); // Default to racing-safe effects
                break;
        }
    }
    
    /**
     * Set car for racing (safe mode - no translationX interference)
     * @param context Application context
     * @param imageView Target ImageView
     * @param useAnimation Whether to use animated version
     * @param carColor Car color (0=red, 1=blue, 2=green, 3=orange, 4=purple)
     */
    public static void setCarForRacing(Context context, ImageView imageView, boolean useAnimation, int carColor) {
        if (useAnimation) {
            setPngRacingForRace(context, imageView, true, carColor); // Racing-safe effects
        } else {
            // Set static PNG based on color
            switch (carColor) {
                case 0:
                    setPngRacingRedCar(context, imageView);
                    break;
                case 1:
                    setPngRacingBlueCar(context, imageView);
                    break;
                case 2:
                    setPngRacingGreenCar(context, imageView);
                    break;
                case 3:
                    setPngRacingOrangeCar(context, imageView);
                    break;
                case 4:
                    setPngRacingPurpleCar(context, imageView);
                    break;
                default:
                    setPngRacingRedCar(context, imageView);
                    break;
            }
        }
    }

    /**
     * Set car for racing (safe mode - no translationX interference) - backward compatibility
     * @param context Application context
     * @param imageView Target ImageView
     * @param useAnimation Whether to use animated version
     */
    public static void setCarForRacing(Context context, ImageView imageView, boolean useAnimation) {
        setCarForRacing(context, imageView, useAnimation, 0); // Default to red car
    }
    
    /**
     * Toggle between animated and static car (for display, not racing) - support all car colors
     * @param context Application context
     * @param imageView Target ImageView
     * @param useAnimation Whether to use animated version
     * @param carColor Car color (0=red, 1=blue, 2=green, 3=orange, 4=purple)
     */
    public static void toggleCarAnimation(Context context, ImageView imageView, boolean useAnimation, int carColor) {
        if (useAnimation) {
            // Use display effects (full effects including translationX - safe for non-racing display)
            switch (carColor) {
                case 0:
                    setPngRacingWithEffects(context, imageView, true);
                    break;
                case 1:
                    setPngRacingBlueCar(context, imageView);
                    addDisplayEffects(imageView);
                    break;
                case 2:
                    setPngRacingGreenCar(context, imageView);
                    addDisplayEffects(imageView);
                    break;
                case 3:
                    setPngRacingOrangeCar(context, imageView);
                    addDisplayEffects(imageView);
                    break;
                case 4:
                    setPngRacingPurpleCar(context, imageView);
                    addDisplayEffects(imageView);
                    break;
                default:
                    setPngRacingWithEffects(context, imageView, true);
                    break;
            }
        } else {
            // Static PNG based on color
            switch (carColor) {
                case 0:
                    setPngRacingRedCar(context, imageView);
                    break;
                case 1:
                    setPngRacingBlueCar(context, imageView);
                    break;
                case 2:
                    setPngRacingGreenCar(context, imageView);
                    break;
                case 3:
                    setPngRacingOrangeCar(context, imageView);
                    break;
                case 4:
                    setPngRacingPurpleCar(context, imageView);
                    break;
                default:
                    setPngRacingRedCar(context, imageView);
                    break;
            }
        }
    }

    /**
     * Add display effects to any car (for betting screen display)
     */
    private static void addDisplayEffects(ImageView imageView) {
        // Add display effects including translationX (safe for non-racing screens)
        ObjectAnimator shakeX = ObjectAnimator.ofFloat(imageView, "translationX", 0f, 2f, -2f, 0f);
        shakeX.setDuration(200);
        shakeX.setRepeatCount(ObjectAnimator.INFINITE);
        shakeX.start();
        
        ObjectAnimator shakeY = ObjectAnimator.ofFloat(imageView, "translationY", 0f, 1f, -1f, 0f);
        shakeY.setDuration(150);
        shakeY.setRepeatCount(ObjectAnimator.INFINITE);
        shakeY.start();
    }

    /**
     * Toggle between animated and static car (for display, not racing) - backward compatibility
     * @param context Application context
     * @param imageView Target ImageView
     * @param useAnimation Whether to use animated version
     */
    public static void toggleCarAnimation(Context context, ImageView imageView, boolean useAnimation) {
        toggleCarAnimation(context, imageView, useAnimation, 0); // Default to red car
    }
}