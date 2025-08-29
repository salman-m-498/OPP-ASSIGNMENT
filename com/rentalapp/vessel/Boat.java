package com.rentalapp.vessel;

import java.time.Duration;

public class Boat extends Vessel {
    
    public Boat(String id, String category, String vesselType ,String location, String purpose, int capacity,Duration duration, double basePrice, boolean available) {
        super(id, category, vesselType, location, purpose, capacity,duration, basePrice, available);
    }
    
    @Override
    public String getVesselDetails() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        return String.format(
            "Boat Rental - %s (%s)\nLocation: %s | Purpose: %s\n" + "Capacity: %d passengers | Duration: %dh %dm\n" + "Base Price: RM%.2f\nStatus: %s",
            vesselType, location, purpose, capacity, hours, minutes, basePrice, available ? "Available" : "Rented"
        );
    }
}
