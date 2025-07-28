package com.rentalapp.rental;

import java.time.LocalDate;

public class RentalRequest {
    private String customerId;
    private String vehicleId;
    private String pickupLocation;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private int driverAge;
    private int rentalDays;

    public RentalRequest(String customerId, String vehicleId, String pickupLocation,
                        LocalDate pickupDate, LocalDate returnDate, int driverAge) {
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.pickupLocation = pickupLocation;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.driverAge = driverAge;
        this.rentalDays = (int) java.time.temporal.ChronoUnit.DAYS.between(pickupDate, returnDate);
    }

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
    
    public int getDriverAge() { return driverAge; }
    public void setDriverAge(int driverAge) { this.driverAge = driverAge; }
    
    public int getRentalDays() { return rentalDays; }
    public void setRentalDays(int rentalDays) { this.rentalDays = rentalDays; }


}
