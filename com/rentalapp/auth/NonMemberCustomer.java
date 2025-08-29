package com.rentalapp.auth;

import java.time.LocalDateTime;

public class NonMemberCustomer extends Customer {
    private boolean eligibleForMembership;
    
    public NonMemberCustomer() {
        super();
        this.isMember = false;
        this.eligibleForMembership = true;
    }
    
    public NonMemberCustomer(String username, String hashedPassword, String name, String email, String phone,
                             String customerId, String address, String icNumber) {
        super(username, hashedPassword, name, email, phone, customerId, address, icNumber, false);
        this.eligibleForMembership = true;
    }

    
    // Getters
    public boolean isEligibleForMembership() { return eligibleForMembership; }
    
    // Setters
    public void setEligibleForMembership(boolean eligibleForMembership) { 
        this.eligibleForMembership = eligibleForMembership; 
    }
    
    @Override
    public void addLoyaltyPoints(int points) {
        // Non-members don't accumulate loyalty points
        // Could potentially track for membership eligibility
        return;
    }
    
    @Override
    public int getLoyaltyPoints() {
        return 0; // Non-members have no loyalty points
    }
    

    
    @Override
    public double getDiscountRate() {
        // Non-members might get small discounts for frequent use
        if (totalSpent > 1000) {
            return 0.02; // 2% for high spenders
        }
        return 0.0; // No discount
    }
    
    // Method to convert to member
    public MemberCustomer convertToMember(String membershipId) {
        if (!eligibleForMembership) {
            return null;
        }
        
        MemberCustomer memberCustomer = new MemberCustomer(
        this.username, this.hashedPassword, this.name, this.email, this.phone,
        this.customerId, this.address, this.icNumber, membershipId, "Standard"
    );
        
        // Transfer existing data
        memberCustomer.setTotalSpent(this.totalSpent);
        memberCustomer.setLastRental(this.lastRental);
        for (String record : this.rentalHistory) {
            memberCustomer.addRentalRecord(record);
        }
        
        return memberCustomer;
    }
    
    @Override
    public String toString() {
        return String.format("NonMemberCustomer{username='%s', name='%s', totalSpent=%.2f}", 
                           username, name, totalSpent);
    }
}