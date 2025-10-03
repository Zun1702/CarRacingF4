package com.example.carracing;

import java.util.Random;

public class Car {
    private int id;
    private String name;
    private int drawableResource;
    private int colorResource;
    private int baseSpeed;
    private float odds;
    private int currentPosition;
    private boolean isSelected;
    
    // Enhanced racing attributes
    private double currentSpeed;
    private double momentum;
    private int acceleration;
    private int handling;
    private double fatigue; // Cars get tired over time
    private boolean inSlipstream; // Drafting behind another car
    private Random carRandom; // Individual random generator for each car
    
    public Car(int id, String name, int drawableResource, int colorResource, int baseSpeed, float odds) {
        this.id = id;
        this.name = name;
        this.drawableResource = drawableResource;
        this.colorResource = colorResource;
        this.baseSpeed = baseSpeed;
        this.odds = odds;
        this.currentPosition = 0;
        this.isSelected = false;
        
        // Initialize enhanced attributes with unique random seed per car
        this.carRandom = new Random(System.currentTimeMillis() + id * 1000); // Unique seed per car
        this.currentSpeed = baseSpeed;
        this.momentum = 1.0;
        this.acceleration = 15 + carRandom.nextInt(10); // 15-25
        this.handling = 10 + carRandom.nextInt(15); // 10-25
        this.fatigue = 0.0;
        this.inSlipstream = false;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getDrawableResource() {
        return drawableResource;
    }
    
    public void setDrawableResource(int drawableResource) {
        this.drawableResource = drawableResource;
    }
    
    public int getColorResource() {
        return colorResource;
    }
    
    public void setColorResource(int colorResource) {
        this.colorResource = colorResource;
    }
    
    public int getBaseSpeed() {
        return baseSpeed;
    }
    
    public void setBaseSpeed(int baseSpeed) {
        this.baseSpeed = baseSpeed;
    }
    
    public float getOdds() {
        return odds;
    }
    
    public void setOdds(float odds) {
        this.odds = odds;
    }
    
    public int getCurrentPosition() {
        return currentPosition;
    }
    
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    
    // Enhanced realistic speed calculation
    public int calculateRaceSpeed(double raceProgress, boolean isLeading) {
        // Base calculation with realistic factors
        double speedModifier = 1.0;
        
        // Early race: all cars relatively close
        if (raceProgress < 0.2) {
            speedModifier = 0.85 + (carRandom.nextDouble() * 0.3); // 0.85-1.15
        }
        // Mid race: more variation, leaders may slow, others catch up
        else if (raceProgress < 0.7) {
            if (isLeading) {
                // Leaders may have slight disadvantage (pressure, tire wear)
                speedModifier = 0.8 + (carRandom.nextDouble() * 0.35); // 0.8-1.15
            } else {
                // Followers get slight boost (slipstream, less pressure)
                speedModifier = 0.9 + (carRandom.nextDouble() * 0.35); // 0.9-1.25
            }
        }
        // Final stretch: high variance for exciting finishes
        else {
            speedModifier = 0.7 + (carRandom.nextDouble() * 0.6); // 0.7-1.3
        }
        
        // Apply fatigue (cars slow down slightly over time)
        fatigue += 0.001;
        speedModifier *= (1.0 - Math.min(fatigue, 0.15)); // Maximum 15% fatigue
        
        // Slipstream effect
        if (inSlipstream) {
            speedModifier *= 1.1; // 10% boost
        }
        
        // Apply momentum (smooth speed changes)
        double targetSpeed = baseSpeed * speedModifier;
        currentSpeed = (currentSpeed * 0.7) + (targetSpeed * 0.3); // Smooth transition
        
        return Math.max(1, (int) currentSpeed); // Never go below 1
    }
    
    // Getters for new attributes
    public double getCurrentSpeed() { return currentSpeed; }
    public void setCurrentSpeed(double currentSpeed) { this.currentSpeed = currentSpeed; }
    public double getMomentum() { return momentum; }
    public void setMomentum(double momentum) { this.momentum = momentum; }
    public int getAcceleration() { return acceleration; }
    public int getHandling() { return handling; }
    public double getFatigue() { return fatigue; }
    public void setFatigue(double fatigue) { this.fatigue = fatigue; }
    public boolean isInSlipstream() { return inSlipstream; }
    public void setInSlipstream(boolean inSlipstream) { this.inSlipstream = inSlipstream; }
    
    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", baseSpeed=" + baseSpeed +
                ", odds=" + odds +
                ", currentPosition=" + currentPosition +
                '}';
    }
}