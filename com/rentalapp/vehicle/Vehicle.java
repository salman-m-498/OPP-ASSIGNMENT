package com.rentalapp.vehicle;

public abstract class Vehicle {
    protected String id;
    protected String category;
    protected String type;
    protected String model;
    protected int seats;
    protected int doors;
    protected String baggageCapacity;
    protected double dailyRate;
    protected String fuelType;
    protected String transmissionType;
    protected boolean available;

    public Vehicle(String id, String category, String type, String model, 
                   int seats, int doors, String baggageCapacity, double dailyRate,
                   String fuelType, String transmissionType, boolean available) {
        this.id = id;
        this.category = category;
        this.type = type;
        this.model = model;
        this.seats = seats;
        this.doors = doors;
        this.baggageCapacity = baggageCapacity;
        this.dailyRate = dailyRate;
        this.fuelType = fuelType;
        this.transmissionType = transmissionType;
        this.available = available;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }
    
    public int getDoors() { return doors; }
    public void setDoors(int doors) { this.doors = doors; }
    
    public String getBaggageCapacity() { return baggageCapacity; }
    public void setBaggageCapacity(String baggageCapacity) { this.baggageCapacity = baggageCapacity; }
    
    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }
    
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    
    public String getTransmissionType() { return transmissionType; }
    public void setTransmissionType(String transmissionType) { this.transmissionType = transmissionType; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("ID: %s | %s %s | %d seats | RM%.2f/day | %s | %s | %s",
                id, model, type, seats, dailyRate, fuelType, transmissionType, 
                available ? "Available" : "Rented");
    }

    public abstract double calculateRentalCost(int days);
    public abstract String getVehicleDetails();
}
