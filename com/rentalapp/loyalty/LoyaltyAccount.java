package com.rentalapp.loyalty;

import java.time.LocalDateTime;

public class LoyaltyAccount {
    private String customerId;
    private String customerName;
    private int currentPoints;
    private int lifetimePoints;
    private boolean vipMember;
    private LocalDateTime accountCreatedDate;
    private LocalDateTime lastPointsEarned;
    private LocalDateTime vipUpgradeDate;
    private int totalRentals;

    public LoyaltyAccount(String customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.currentPoints = 0;
        this.lifetimePoints = 0;
        this.vipMember = false;
        this.accountCreatedDate = LocalDateTime.now();
        this.totalRentals = 0;
    }

    public void addPoints(int points) {
        if (points > 0) {
            this.currentPoints += points;
            this.lifetimePoints += points;
            this.lastPointsEarned = LocalDateTime.now();
        }
    }

    public boolean deductPoints(int points) {
        if (points > 0 && this.currentPoints >= points) {
            this.currentPoints -= points;
            return true;
        }
        return false;
    }

    public void upgradeToVip() {
        if (!this.vipMember) {
            this.vipMember = true;
            this.vipUpgradeDate = LocalDateTime.now();
        }
    }

    public void incrementRentalCount() {
        this.totalRentals++;
    }

    public String getMembershipTier() {
        if (vipMember) {
            return "VIP";
        } else if (lifetimePoints >= 250) {
            return "SILVER";
        } else if (lifetimePoints >= 100) {
            return "BRONZE";
        } else {
            return "STANDARD";
        }
    }

    public boolean canUpgradeToVip() {
        return !vipMember && (currentPoints >= 500 || totalRentals >= 5);
    }

    public int getPointsToNextTier() {
        if (vipMember) {
            return 0; // Already at top tier
        } else if (currentPoints < 500) {
            return 500 - currentPoints;
        }
        return 0;
    }

    public double getPointsEarningRate() {
        if (totalRentals == 0) return 0.0;
        return (double) lifetimePoints / totalRentals;
    }

    public void printAccountSummary() {
        System.out.println("\n================ LOYALTY ACCOUNT SUMMARY ================");
        System.out.println("Customer: " + customerName);
        System.out.println("Customer ID: " + customerId);
        System.out.println("Current Points: " + currentPoints);
        System.out.println("Lifetime Points: " + lifetimePoints);
        System.out.println("Membership Tier: " + getMembershipTier());
        System.out.println("Total Rentals: " + totalRentals);
        System.out.println("Account Created: " + accountCreatedDate.toLocalDate());
        
        if (vipMember && vipUpgradeDate != null) {
            System.out.println("VIP Since: " + vipUpgradeDate.toLocalDate());
        }
        
        if (lastPointsEarned != null) {
            System.out.println("Last Points Earned: " + lastPointsEarned.toLocalDate());
        }
        
        if (!vipMember) {
            System.out.println("Points to VIP: " + getPointsToNextTier());
        }
        
        System.out.println("Points Earning Rate: " + String.format("%.1f", getPointsEarningRate()) + " pts/rental");
        System.out.println("=========================================================\n");
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public int getCurrentPoints() { return currentPoints; }
    public int getLifetimePoints() { return lifetimePoints; }
    public boolean isVipMember() { return vipMember; }
    public LocalDateTime getAccountCreatedDate() { return accountCreatedDate; }
    public LocalDateTime getLastPointsEarned() { return lastPointsEarned; }
    public LocalDateTime getVipUpgradeDate() { return vipUpgradeDate; }
    public int getTotalRentals() { return totalRentals; }

    // Setters
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCurrentPoints(int currentPoints) { this.currentPoints = currentPoints; }
    public void setLifetimePoints(int lifetimePoints) { this.lifetimePoints = lifetimePoints; }
    public void setVipMember(boolean vipMember) { this.vipMember = vipMember; }
    public void setTotalRentals(int totalRentals) { this.totalRentals = totalRentals; }

    @Override
    public String toString() {
        return "LoyaltyAccount{" +
                "customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", currentPoints=" + currentPoints +
                ", lifetimePoints=" + lifetimePoints +
                ", vipMember=" + vipMember +
                ", totalRentals=" + totalRentals +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        LoyaltyAccount that = (LoyaltyAccount) obj;
        return customerId != null ? customerId.equals(that.customerId) : that.customerId == null;
    }

    @Override
    public int hashCode() {
        return customerId != null ? customerId.hashCode() : 0;
    }

}
