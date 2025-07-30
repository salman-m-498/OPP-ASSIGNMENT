package com.rentalapp.vehicle;

public class LuxuryCar extends Vehicle {
    
    public LuxuryCar(String id, String category, String type, String model, 
                     int seats, int doors, String baggageCapacity, double dailyRate,
                     String fuelType, String transmissionType, boolean available) {
        super(id, category, type, model, seats, doors, baggageCapacity, 
              dailyRate, fuelType, transmissionType, available);
    }


    @Override
    public String getVehicleDetails() {
        return String.format("Luxury Vehicle - %s %s\nSeats: %d | Doors: %d | Baggage: %s\n" +
                           "Daily Rate: RM%.2f (+ 10%% luxury surcharge) | Fuel: %s | Transmission: %s\nStatus: %s",
                           model, type, seats, doors, baggageCapacity, dailyRate, 
                           fuelType, transmissionType, available ? "Available" : "Rented");
    }
}