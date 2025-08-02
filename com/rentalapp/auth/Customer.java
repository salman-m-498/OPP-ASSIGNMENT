package com.rentalapp.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Customer extends User {
    protected String customerId;
    protected String address;
    protected String drivingLicenseNumber;
    protected LocalDateTime licenseExpiryDate;
    protected List<String> rentalHistory;
    protected double totalSpent;
    protected LocalDateTime lastRental;
    protected boolean isMember;
    
    public Customer() {
        super();
        this.rentalHistory = new ArrayList<>();
        this.totalSpent = 0.0;
    }
    
    public Customer(String username, String hashedPassword, String name, String email, String phone, 
                   String customerId, String address, String drivingLicenseNumber, 
                   LocalDateTime licenseExpiryDate, boolean isMember) {
        super(username, hashedPassword, name, email, phone);
        this.customerId = customerId;
        this.address = address;
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.licenseExpiryDate = licenseExpiryDate;
        this.isMember = isMember;
        this.rentalHistory = new ArrayList<>();
        this.totalSpent = 0.0;
    }
    
    // Getters
    public String getCustomerId() { return customerId; }
    public String getAddress() { return address; }
    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }
    public LocalDateTime getLicenseExpiryDate() { return licenseExpiryDate; }
    public List<String> getRentalHistory() { return rentalHistory; }
    public double getTotalSpent() { return totalSpent; }
    public LocalDateTime getLastRental() { return lastRental; }
    public boolean isMember() { return isMember; }
    
    // Setters
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setAddress(String address) { this.address = address; }
    public void setDrivingLicenseNumber(String drivingLicenseNumber) { this.drivingLicenseNumber = drivingLicenseNumber; }
    public void setLicenseExpiryDate(LocalDateTime licenseExpiryDate) { this.licenseExpiryDate = licenseExpiryDate; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }
    public void setLastRental(LocalDateTime lastRental) { this.lastRental = lastRental; }
    public void setMember(boolean member) { this.isMember = member; }
    
    // Methods for rental history
    public void addRentalRecord(String rentalRecord) {
        this.rentalHistory.add(rentalRecord);
        this.lastRental = LocalDateTime.now();
    }
    
    public void addToTotalSpent(double amount) {
        this.totalSpent += amount;
    }
    
    // License validation
    public boolean isLicenseValid() {
        return licenseExpiryDate != null && licenseExpiryDate.isAfter(LocalDateTime.now());
    }
    
    // Abstract methods
    public abstract void addLoyaltyPoints(int points);
    public abstract boolean isEligibleForPromo();
    public abstract int getLoyaltyPoints();
    public abstract double getDiscountRate();
    
    @Override
    public String getUserType() {
        return isMember ? "MEMBER_CUSTOMER" : "NON_MEMBER_CUSTOMER";
    }
}