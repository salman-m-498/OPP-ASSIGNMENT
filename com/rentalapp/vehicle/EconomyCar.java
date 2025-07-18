package com.rentalapp.vehicle;

public class EconomyCar extends Vehicle {
    
    public EconomyCar(String id, String category, String type, String model, 
                      int seats, int doors, String baggageCapacity, double dailyRate,
                      String fuelType, String transmissionType, boolean available) {
        super(id, category, type, model, seats, doors, baggageCapacity, 
              dailyRate, fuelType, transmissionType, available);
    }
    
     @Override
    public double calculateRentalCost(int days) {
        double baseCost = dailyRate * days;
        // Economy cars have no additional charges
        return baseCost;
    }

    @Override
    public String getVehicleDetails() {
        return String.format("Economy Vehicle - %s %s\nSeats: %d | Doors: %d | Baggage: %s\n" +
                           "Daily Rate: RM%.2f | Fuel: %s | Transmission: %s\nStatus: %s",
                           model, type, seats, doors, baggageCapacity, dailyRate, 
                           fuelType, transmissionType, available ? "Available" : "Rented");
    }
}
