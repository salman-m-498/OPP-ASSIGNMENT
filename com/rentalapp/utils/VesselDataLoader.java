package com.rentalapp.utils;

import com.rentalapp.vessel.*;
import java.time.Duration;
import java.util.*;

public class VesselDataLoader {
        private static final String[] VESSEL_FILES = {
        "com/rentalapp/data/Yachts.csv",
        "com/rentalapp/data/Boats.csv",
        "com/rentalapp/data/Pontoons.csv",
        "com/rentalapp/data/JetSkis.csv",
        "com/rentalapp/data/FishingCharters.csv"
    };

    public static List<Vessel> loadAllVessels() {
         List<Vessel> vessels = new ArrayList<>();
        for (String file : VESSEL_FILES) {
            vessels.addAll(loadVesselsFromCSV(file));
        }
        return vessels;
    }

    private static List<Vessel> loadVesselsFromCSV(String filePath) {
        List<Vessel> vessels = new ArrayList<>();
        List<String[]> data = FileReader.readCSV(filePath);

        for (String[] row : data) {
            try {
                String id = row[0];
                String category = row[1];
                String vesselType = row[2];
                String location = row[3];
                String purpose = row[4];
                int capacity = Integer.parseInt(row[5]);
                Duration duration = parseDuration(row[6]);
                double basePrice = Double.parseDouble(row[7]);
                boolean available = Boolean.parseBoolean(row[8]);

                Vessel vessel = new Vessel(id, category, vesselType, location, purpose,
                                           capacity, duration, basePrice, available);
                vessels.add(vessel);
            } catch (Exception e) {
                System.err.println("Error parsing vessel data from " + filePath + ": " + e.getMessage());
            }
        }
        return vessels;
    }

    public static Duration parseDuration(String durationStr) {
    durationStr = durationStr.toLowerCase().trim();
    if (durationStr.endsWith("min")) {
        long minutes = Long.parseLong(durationStr.replace("min", "").trim());
        return Duration.ofMinutes(minutes);
    } else if (durationStr.endsWith("h")) {
        double hours = Double.parseDouble(durationStr.replace("h", "").trim());
        long totalMinutes = (long)(hours * 60);
        return Duration.ofMinutes(totalMinutes);
    } else {
        throw new IllegalArgumentException("Unknown duration format: " + durationStr);
    }
}


}