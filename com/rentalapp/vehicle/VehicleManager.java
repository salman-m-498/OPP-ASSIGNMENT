package com.rentalapp.vehicle;

import com.rentalapp.utils.VehicleDataLoader;
import java.util.*;
import java.util.stream.Collectors;

public class VehicleManager {
    private List<Vehicle> vehicles;
    
    public VehicleManager() {
        this.vehicles = VehicleDataLoader.loadAllVehicles();
    }
     public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles);
    }
    
    public List<Vehicle> getAvailableVehicles() {
        return vehicles.stream()
                      .filter(Vehicle::isAvailable)
                      .collect(Collectors.toList());
    }
    
    public List<Vehicle> getVehiclesByCategory(String category) {
        return vehicles.stream()
                      .filter(v -> v.getCategory().equalsIgnoreCase(category))
                      .collect(Collectors.toList());
    }
    
    public List<Vehicle> getVehiclesByType(String type) {
        return vehicles.stream()
                      .filter(v -> v.getType().equalsIgnoreCase(type))
                      .collect(Collectors.toList());
    }
    
    public List<Vehicle> getVehiclesBySeats(int minSeats) {
        return vehicles.stream()
                      .filter(v -> v.getSeats() >= minSeats)
                      .collect(Collectors.toList());
    }
    
    public List<Vehicle> getVehiclesByPriceRange(double minPrice, double maxPrice) {
        return vehicles.stream()
                      .filter(v -> v.getDailyRate() >= minPrice && v.getDailyRate() <= maxPrice)
                      .collect(Collectors.toList());
    }
    
    public Vehicle getVehicleById(String id) {
        return vehicles.stream()
                      .filter(v -> v.getId().equals(id))
                      .findFirst()
                      .orElse(null);
    }
    
    public boolean rentVehicle(String vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        if (vehicle != null && vehicle.isAvailable()) {
            vehicle.setAvailable(false);
            return true;
        }
        return false;
    }
    
    public boolean returnVehicle(String vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        if (vehicle != null && !vehicle.isAvailable()) {
            vehicle.setAvailable(true);
            vehicle.incrementRentalCount();  
            return true;
        }
        return false;
    }
    
    public void displayVehicles(List<Vehicle> vehicleList) {
        if (vehicleList.isEmpty()) {
            System.out.println("No vehicles found.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(100));
        System.out.printf("%-6s %-25s %-15s %-6s %-8s %-12s %-8s %-10s%n",
                         "ID", "Model", "Type", "Seats", "Rate", "Fuel", "Trans", "Status");
        System.out.println("=".repeat(100));
        
        for (Vehicle vehicle : vehicleList) {
            System.out.printf("%-6s %-25s %-15s %-6d RM%-6.2f %-12s %-8s %-10s%n",
                             vehicle.getId(),
                             vehicle.getModel(),
                             vehicle.getType(),
                             vehicle.getSeats(),
                             vehicle.getDailyRate(),
                             vehicle.getFuelType(),
                             vehicle.getTransmissionType(),
                             vehicle.isAvailable() ? "Available" : "Rented");
        }
        System.out.println("=".repeat(100));
    }
}
