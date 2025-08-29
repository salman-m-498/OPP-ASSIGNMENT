package com.rentalapp.vessel;

import java.time.Duration;

public abstract class Vessel {
    protected String id;
    protected String category;
    protected String vesselType;  
    protected String location;
    protected String purpose;
    protected int capacity;
    protected Duration duration;    
    protected double basePrice;
    protected boolean available;
    protected int rentalCount = 0;

    public Vessel(String id, String category, String vesselType, String location, String purpose, int capacity, Duration duration, double basePrice, boolean available) {
        this.id = id;
        this.category = category;
        this.vesselType = vesselType;
        this.location = location;
        this.purpose = purpose;
        this.capacity = capacity;
        this.duration = duration; 
        this.basePrice = basePrice;
        this.available = available;
    }

    // ================= Getters =================
    public String getId() { return id; }
    public String getVesselCategory() { return category; }
    public String getVesselType() { return vesselType; }
    public String getLocation() { return location; }
    public String getPurpose() { return purpose; }
    public int getCapacity() { return capacity; }
    public Duration getDuration() { return duration; }      
    public double getBasePrice() { return basePrice; }
    public boolean isAvailable() { return available; }
    public int getRentalCount() { return rentalCount; }

    // ================= Setters =================
    public void setId(String id) { this.id = id; }
    public void setVesselCategory(String category) { this.category = category; }
    public void setVesselType(String vesselType) { this.vesselType = vesselType; }
    public void setLocation(String location) { this.location = location; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setDuration(Duration duration) { this.duration = duration; }           
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public void setAvailable(boolean available) { this.available = available; }

    public void incrementRentalCount() { this.rentalCount++; }
    public void resetRentalCount() { this.rentalCount = 0; }

    @Override
    public String toString() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        return String.format(
                "ID: %s | %s (%s) | %s | %d pax | RM%.2f / %dh %dm | %s | %s",
                id, vesselType, location, capacity, basePrice,hours, minutes, purpose, available ? "Available" : "Rented");
    }

    public abstract String getVesselDetails();
}
