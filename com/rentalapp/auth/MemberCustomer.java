package com.rentalapp.auth;

import java.time.LocalDateTime;

public class MemberCustomer extends Customer {
    private int loyaltyPoints;
    private String membershipId;
    private LocalDateTime membershipStartDate;
    private String membershipTier; // Bronze, Silver, Gold, Platinum
    private double memberDiscountRate;
    
    public MemberCustomer() {
        super();
        this.isMember = true;
        this.loyaltyPoints = 0;
        this.membershipStartDate = LocalDateTime.now();
        this.membershipTier = "Bronze";
        this.memberDiscountRate = 0.05; // 5% discount for members
    }
    
    public MemberCustomer(String username, String hashedPassword, String name, String email, String phone,
                         String customerId, String address, String drivingLicenseNumber, 
                         LocalDateTime licenseExpiryDate, String membershipId) {
        super(username, hashedPassword, name, email, phone, customerId, address, 
              drivingLicenseNumber, licenseExpiryDate, true);
        this.membershipId = membershipId;
        this.loyaltyPoints = 0;
        this.membershipStartDate = LocalDateTime.now();
        this.membershipTier = "Bronze";
        this.memberDiscountRate = 0.05;
    }
    
    // Getters
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public String getMembershipId() { return membershipId; }
    public LocalDateTime getMembershipStartDate() { return membershipStartDate; }
    public String getMembershipTier() { return membershipTier; }
    public double getMemberDiscountRate() { return memberDiscountRate; }
    
    // Setters
    public void setMembershipId(String membershipId) { this.membershipId = membershipId; }
    public void setMembershipTier(String membershipTier) { 
        this.membershipTier = membershipTier;
        updateDiscountRate();
    }
    
    @Override
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        updateMembershipTier();
    }
    
    private void updateMembershipTier() {
        if (loyaltyPoints >= 5000) {
            setMembershipTier("Platinum");
        } else if (loyaltyPoints >= 2000) {
            setMembershipTier("Gold");
        } else if (loyaltyPoints >= 500) {
            setMembershipTier("Silver");
        } else {
            setMembershipTier("Bronze");
        }
    }
    
    private void updateDiscountRate() {
        switch (membershipTier) {
            case "Platinum":
                this.memberDiscountRate = 0.20; // 20%
                break;
            case "Gold":
                this.memberDiscountRate = 0.15; // 15%
                break;
            case "Silver":
                this.memberDiscountRate = 0.10; // 10%
                break;
            default:
                this.memberDiscountRate = 0.05; // 5%
        }
    }
    
    @Override
    public boolean isEligibleForPromo() {
        return loyaltyPoints >= 100 && isLicenseValid();
    }
    
    @Override
    public double getDiscountRate() {
        return memberDiscountRate;
    }
    
    @Override
    public String toString() {
        return String.format("MemberCustomer{username='%s', name='%s', tier='%s', points=%d}", 
                           username, name, membershipTier, loyaltyPoints);
    }
}