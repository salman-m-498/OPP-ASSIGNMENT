package com.rentalapp.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Customer extends User {
    protected String customerId;
    protected String address;
    protected String icNumber;  
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
                   String customerId, String address, String icNumber, boolean isMember) {
        super(username, hashedPassword, name, email, phone);
        this.customerId = customerId;
        this.address = address;
        this.icNumber = icNumber;
        this.isMember = isMember;
        this.rentalHistory = new ArrayList<>();
        this.totalSpent = 0.0;
    }
    
    // Getters
    public String getCustomerId() { return customerId; }
    public String getAddress() { return address; }
    public String getIcNumber() { return icNumber; }
    public List<String> getRentalHistory() { return rentalHistory; }
    public double getTotalSpent() { return totalSpent; }
    public LocalDateTime getLastRental() { return lastRental; }
    public boolean isMember() { return isMember; }
    
    // Setters
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setAddress(String address) { this.address = address; }
     public void setIcNumber(String icNumber) { this.icNumber = icNumber; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }
    public void setLastRental(LocalDateTime lastRental) { this.lastRental = lastRental; }
    public void setMember(boolean member) { this.isMember = member; }
    
    // Methods for rental history
    public void addRentalRecord(String rentalId) {
    if (!rentalHistory.contains(rentalId)) {
        rentalHistory.add(rentalId);
        this.lastRental = LocalDateTime.now();
        }
    }
    
    public void addToTotalSpent(double amount) {
        this.totalSpent += amount;
    }
    
    public int getCompletedRentals() {
    return rentalHistory.size();
    }

    // Abstract methods (loyalty)
    public abstract void addLoyaltyPoints(int points);
    public abstract int getLoyaltyPoints();
    public abstract double getDiscountRate();

    @Override
    public String getUserType() {
        return isMember ? "MEMBER_CUSTOMER" : "NON_MEMBER_CUSTOMER";
    }
}