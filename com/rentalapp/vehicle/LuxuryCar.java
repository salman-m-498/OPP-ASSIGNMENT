package com.rentalapp.vehicle;

public class LuxuryCar extends Vehicle {
    private static final double LUXURY_SURCHARGE = 0.1; // 10% surcharge
    
    public LuxuryCar(String id, String category, String type, String model, 
                     int seats, int doors, String baggageCapacity, double dailyRate,
                     String fuelType, String transmissionType, boolean available) {
        super(id, category, type, model, seats, doors, baggageCapacity, 
              dailyRate, fuelType, transmissionType, available);
    }

     @Override
    public double calculateRentalCost(int days) {
        double baseCost = dailyRate * days;
        double luxurySurcharge = baseCost * LUXURY_SURCHARGE;
        return baseCost + luxurySurcharge;
    } 

    @Override
    public String getVehicleDetails() {
        return String.format("Luxury Vehicle - %s %s\nSeats: %d | Doors: %d | Baggage: %s\n" +
                           "Daily Rate: RM%.2f (+ 10%% luxury surcharge) | Fuel: %s | Transmission: %s\nStatus: %s",
                           model, type, seats, doors, baggageCapacity, dailyRate, 
                           fuelType, transmissionType, available ? "Available" : "Rented");
    }
}