package com.rentalapp.utils;

import com.rentalapp.vehicle.*;
import java.util.*;

public class VehicleDataLoader {
    private static final String ECONOMY_FILE = "com/rentalapp/data/economy_vehicles.csv";
    private static final String LUXURY_FILE = "com/rentalapp/data/luxury_vehicles.csv";

     public static List<Vehicle> loadAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.addAll(loadEconomyVehicles());
        vehicles.addAll(loadLuxuryVehicles());
        return vehicles;
    }
    
    public static List<EconomyCar> loadEconomyVehicles() {
        List<EconomyCar> economyCars = new ArrayList<>();
        List<String[]> data = FileReader.readCSV(ECONOMY_FILE);

        for (String[] row : data) {
            try {
                String id = row[0];
                String category = row[1];
                String type = row[2];
                String model = row[3];
                int seats = Integer.parseInt(row[4]);
                int doors = Integer.parseInt(row[5]);
                String baggageCapacity = row[6];
                double dailyRate = Double.parseDouble(row[7]);
                String fuelType = row[8];
                String transmissionType = row[9];
                boolean available = Boolean.parseBoolean(row[10]);
                
                EconomyCar car = new EconomyCar(id, category, type, model, seats, doors, 
                                              baggageCapacity, dailyRate, fuelType, 
                                              transmissionType, available);
                economyCars.add(car);
            } catch (Exception e) {
                System.err.println("Error parsing economy vehicle data: " + e.getMessage());
            }
        }
        return economyCars;
    }
    
     public static List<LuxuryCar> loadLuxuryVehicles() {
        List<LuxuryCar> luxuryCars = new ArrayList<>();
        List<String[]> data = FileReader.readCSV(LUXURY_FILE);
        
        for (String[] row : data) {
            try {
                String id = row[0];
                String category = row[1];
                String type = row[2];
                String model = row[3];
                int seats = Integer.parseInt(row[4]);
                int doors = Integer.parseInt(row[5]);
                String baggageCapacity = row[6];
                double dailyRate = Double.parseDouble(row[7]);
                String fuelType = row[8];
                String transmissionType = row[9];
                boolean available = Boolean.parseBoolean(row[10]);
                
                LuxuryCar car = new LuxuryCar(id, category, type, model, seats, doors, 
                                            baggageCapacity, dailyRate, fuelType, 
                                            transmissionType, available);
                luxuryCars.add(car);
            } catch (Exception e) {
                System.err.println("Error parsing luxury vehicle data: " + e.getMessage());
            }
        }
        return luxuryCars;
    }
}
