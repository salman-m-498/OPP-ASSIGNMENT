package com.rentalapp.auth;

import java.time.LocalDateTime;

public class MemberCustomer extends Customer {
    private int loyaltyPoints;
    private String membershipId;
    private LocalDateTime membershipStartDate;
    private String membershipTier; // Standard or VIP
    private double discountRate;
    
    public MemberCustomer() {
        super();
        this.isMember = true;
        this.loyaltyPoints = 0;
        this.membershipStartDate = LocalDateTime.now();
        this.membershipTier = "Standard"; 
        this.discountRate = 0.08; // 8% default for standard
    }
    
     public MemberCustomer(String username, String hashedPassword, String name, String email, String phone,
                         String customerId, String address, String icNumber, String membershipId,
                         String membershipTier) {
        super(username, hashedPassword, name, email, phone, customerId, address, icNumber, true);
        this.membershipId = membershipId;
        this.loyaltyPoints = 0;
        this.membershipStartDate = LocalDateTime.now();
        setMembershipTier(membershipTier); // will assign correct discountRate
    }
    
    // Getters
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public String getMembershipId() { return membershipId; }
    public LocalDateTime getMembershipStartDate() { return membershipStartDate; }
    public String getMembershipTier() { return membershipTier; }
    
    // Setters
    public void setMembershipId(String membershipId) { this.membershipId = membershipId; }
    public void setMembershipTier(String membershipTier) { 
        if (!membershipTier.equalsIgnoreCase("Standard") && 
            !membershipTier.equalsIgnoreCase("VIP")) {
            throw new IllegalArgumentException("Membership tier must be either Standard or VIP.");
        }
        this.membershipTier = membershipTier;
        updateDiscountRate();
    }
    
    @Override
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }
    

    private void updateDiscountRate() {
        if ("VIP".equalsIgnoreCase(this.membershipTier)) {
            this.discountRate = 0.15; // 15% for VIP
        } else {
            this.discountRate = 0.08; // 8% for Standard
        }
    }


    @Override
    public double getDiscountRate() {
        return discountRate;
    }

    @Override
    public String toString() {
        return String.format("MemberCustomer{username='%s', name='%s', type='%s', points=%d, discount=%.0f%%}", 
                           username, name, membershipTier, loyaltyPoints, discountRate * 100);
    }
}
    