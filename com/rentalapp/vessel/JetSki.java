
package com.rentalapp.vessel;

import java.time.Duration;

public class JetSki extends Vessel {
    
    public JetSki(String id, String category, String vesselType, String location, String purpose, int capacity, Duration duration, double basePrice, boolean available) {
        super(id, category, vesselType, location, purpose, capacity, duration, basePrice, available);
    }
    
    @Override
    public String getVesselDetails() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        return String.format("Jet Ski Rental - %s\nLocation: %s | Purpose: %s\n" + "Capacity: %d riders | Duration: %dh %dm\n"  + "Base Price: RM%.2f\nStatus: %s",
                           vesselType, location, purpose, capacity, hours, minutes, basePrice, available ? "Available" : "Rented");
    }
}