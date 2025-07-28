package com.rentalapp.rental;

import java.time.LocalDate;

public class RentalRecord {
    private String rentalId;
    private String customerId;
    private String vehicleId;
    private String pickupLocation;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private int rentalDays;
    private double totalCost;
    private RentalStatus status;
    private String vehicleModel;
    private String customerName;

    public RentalRecord(String rentalId, String customerId, String vehicleId,
                       String pickupLocation, LocalDate pickupDate, LocalDate returnDate,
                       int rentalDays, double totalCost, String vehicleModel, String customerName) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.pickupLocation = pickupLocation;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.rentalDays = rentalDays;
        this.totalCost = totalCost;
        this.status = RentalStatus.ACTIVE;
        this.vehicleModel = vehicleModel;
        this.customerName = customerName;
    }

    public String getRentalId() { return rentalId; }
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    
    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }
    
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    
    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(LocalDate actualReturnDate) { this.actualReturnDate = actualReturnDate; }
    
    public int getRentalDays() { return rentalDays; }
    public void setRentalDays(int rentalDays) { this.rentalDays = rentalDays; }
    
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    
    public RentalStatus getStatus() { return status; }
    public void setStatus(RentalStatus status) { this.status = status; }
    
    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
