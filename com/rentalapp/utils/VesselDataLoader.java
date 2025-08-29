package com.rentalapp.utils;

import com.rentalapp.vessel.*;
import java.time.Duration;
import java.util.*;

public class VesselDataLoader {
    private static final String YACHTS_FILE = "com/rentalapp/data/Yachts.csv";
    private static final String BOATS_FILE = "com/rentalapp/data/Boats.csv";
    private static final String PONTOONS_FILE = "com/rentalapp/data/Pontoons.csv";
    private static final String JETSKIS_FILE = "com/rentalapp/data/JetSkis.csv";
    private static final String FISHING_CHARTERS_FILE = "com/rentalapp/data/FishingCharters.csv";

    public static List<Vessel> loadAllVessels() {
        List<Vessel> vessels = new ArrayList<>();
        vessels.addAll(loadYachts());
        vessels.addAll(loadBoats());
        vessels.addAll(loadPontoons());
        vessels.addAll(loadJetSkis());
        vessels.addAll(loadFishingCharters());
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

    
    public static List<Yacht> loadYachts() {
        List<Yacht> yachts = new ArrayList<>();
        List<String[]> data = FileReader.readCSV(YACHTS_FILE);

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
                
                Yacht yacht = new Yacht(id, category, vesselType, location, purpose, 
                                       capacity, duration, basePrice, available);
                yachts.add(yacht);
            } catch (Exception e) {
                System.err.println("Error parsing yacht data: " + e.getMessage());
            }
        }
        return yachts;
    }
    
    public static List<Boat> loadBoats() {
        List<Boat> boats = new ArrayList<>();
        List<String[]> data = FileReader.readCSV(BOATS_FILE);
        
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
                
                Boat boat = new Boat(id, category, vesselType, location, purpose, 
                                    capacity, duration, basePrice, available);
                boats.add(boat);
            } catch (Exception e) {
                System.err.println("Error parsing boat data: " + e.getMessage());
            }
        }
        return boats;
    }
    
    public static List<Pontoon> loadPontoons() {
        List<Pontoon> pontoons = new ArrayList<>();
        List<String[]> data = FileReader.readCSV(PONTOONS_FILE);
        
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
                
                Pontoon pontoon = new Pontoon(id, category, vesselType, location, purpose, 
                                             capacity, duration, basePrice, available);
                pontoons.add(pontoon);
            } catch (Exception e) {
                System.err.println("Error parsing pontoon data: " + e.getMessage());
            }
        }
        return pontoons;
    }
    
    public static List<JetSki> loadJetSkis() {
        List<JetSki> jetSkis = new ArrayList<>();
        List<String[]> data = FileReader.readCSV(JETSKIS_FILE);
        
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
                
                JetSki jetSki = new JetSki(id, category, vesselType, location, purpose, 
                                          capacity, duration, basePrice, available);
                jetSkis.add(jetSki);
            } catch (Exception e) {
                System.err.println("Error parsing jet ski data: " + e.getMessage());
            }
        }
        return jetSkis;
    }
    
    public static List<FishingCharter> loadFishingCharters() {
        List<FishingCharter> fishingCharters = new ArrayList<>();
        List<String[]> data = FileReader.readCSV(FISHING_CHARTERS_FILE);
        
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
                
                FishingCharter charter = new FishingCharter(id, category, vesselType, location, purpose, 
                                                           capacity, duration, basePrice, available);
                fishingCharters.add(charter);
            } catch (Exception e) {
                System.err.println("Error parsing fishing charter data: " + e.getMessage());
            }
        }
        return fishingCharters;
    }
}