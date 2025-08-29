package com.rentalapp.vessel;

import java.time.Duration;

public class Yacht extends Vessel {
    
    public Yacht(String id, String category, String vesselType, String location, String purpose, int capacity, Duration duration, double basePrice, boolean available) {
        super(id, category, vesselType, location, purpose, capacity, duration, basePrice, available);
    }
    
    @Override
    public String getVesselDetails() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        String luxuryNote = "";
        if (vesselType.toLowerCase().contains("superyacht") || vesselType.toLowerCase().contains("luxury")) {
            luxuryNote = " (+ 15% luxury surcharge)";
        }
        
        return String.format("Yacht Charter - %s\nLocation: %s | Purpose: %s\n" + "Capacity: %d passengers | Duration: %dh %dm\n" + "Base Price: RM%.2f%s\nStatus: %s",
                           vesselType, location, purpose, capacity, hours, minutes, basePrice, luxuryNote, available ? "Available" : "Rented");
    }
}