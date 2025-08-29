package com.rentalapp.vessel;

import java.time.Duration;

public class FishingCharter extends Vessel {
    
    public FishingCharter(String id, String category, String vesselType, String location, String purpose, int capacity, Duration duration, double basePrice, boolean available) {
        super(id, category, vesselType, location, purpose, capacity, duration, basePrice, available);
    }
    
    @Override
    public String getVesselDetails() {

        String experienceLevel = "";
        if (purpose.toLowerCase().contains("family") || purpose.toLowerCase().contains("try-out")) {
            experienceLevel = " (Beginner Friendly)";
        } else if (purpose.toLowerCase().contains("deep-sea") || purpose.toLowerCase().contains("offshore")) {
            experienceLevel = " (Advanced)";
        }
        
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        return String.format(
            "Fishing Charter - %s \nLocation: %s | Purpose: %s%s\n" + "Capacity: %d anglers | Duration: %dh %dm\n" + "Base Price: RM%.2f (Equipment included)\nStatus: %s",
            vesselType, location, purpose, experienceLevel, capacity, hours, minutes, basePrice, available ? "Available" : "Rented"
        );
    }
}
