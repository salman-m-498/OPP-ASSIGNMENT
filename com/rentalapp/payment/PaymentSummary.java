package com.rentalapp.payment;

import java.time.LocalDateTime;
import java.util.*;

public class PaymentSummary {
    private String customerId;
    private double totalSpent;
    private int totalRentals;
    private int totalLoyaltyPointsEarned;
    private LocalDateTime lastPaymentDate;
    private Map<String, Double> paymentMethodBreakdown;
    private List<Double> paymentHistory;
    private double averageRentalCost;
    private String preferredPaymentMethod;

    public PaymentSummary(String customerId) {
        this.customerId = customerId;
        this.totalSpent = 0.0;
        this.totalRentals = 0;
        this.totalLoyaltyPointsEarned = 0;
        this.paymentMethodBreakdown = new HashMap<>();
        this.paymentHistory = new ArrayList<>();
        this.averageRentalCost = 0.0;
        this.preferredPaymentMethod = "";
    }

    public void addPayment(double amount, int loyaltyPoints) {
        this.totalSpent += amount;
        this.totalRentals++;
        this.totalLoyaltyPointsEarned += loyaltyPoints;
        this.lastPaymentDate = LocalDateTime.now();
        this.paymentHistory.add(amount);
        
        // Recalculate average
        this.averageRentalCost = this.totalSpent / this.totalRentals;
    }

    public void addPaymentWithMethod(double amount, int loyaltyPoints, String paymentMethod) {
        addPayment(amount, loyaltyPoints);
        
        // Track payment method usage
        paymentMethodBreakdown.put(paymentMethod, 
            paymentMethodBreakdown.getOrDefault(paymentMethod, 0.0) + amount);
        
        // Update preferred payment method (most used by amount)
        updatePreferredPaymentMethod();
    }

    private void updatePreferredPaymentMethod() {
        if (!paymentMethodBreakdown.isEmpty()) {
            this.preferredPaymentMethod = paymentMethodBreakdown.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
        }
    }

    public void printSummary() {
        System.out.println("\n==================== PAYMENT SUMMARY ====================");
        System.out.println("Customer ID: " + customerId);
        System.out.println("Total Rentals: " + totalRentals);
        System.out.println("Total Spent: RM " + String.format("%.2f", totalSpent));
        System.out.println("Average Cost per Rental: RM " + String.format("%.2f", averageRentalCost));
        System.out.println("Total Loyalty Points Earned: " + totalLoyaltyPointsEarned);
        
        if (lastPaymentDate != null) {
            System.out.println("Last Payment Date: " + lastPaymentDate.toLocalDate());
        }
        
        if (!preferredPaymentMethod.isEmpty()) {
            System.out.println("Preferred Payment Method: " + preferredPaymentMethod);
        }
        
        if (!paymentMethodBreakdown.isEmpty()) {
            System.out.println("\nPayment Method Breakdown:");
            paymentMethodBreakdown.forEach((method, amount) -> 
                System.out.println("- " + method + ": RM " + String.format("%.2f", amount)));
        }
        
        System.out.println("========================================================\n");
    }

    public void printDetailedSummary() {
        printSummary();
        
        if (!paymentHistory.isEmpty()) {
            System.out.println("Payment History (Last 10):");
            List<Double> recentPayments = paymentHistory.size() > 10 
                ? paymentHistory.subList(paymentHistory.size() - 10, paymentHistory.size())
                : paymentHistory;
            
            for (int i = 0; i < recentPayments.size(); i++) {
                System.out.println((i + 1) + ". RM " + String.format("%.2f", recentPayments.get(i)));
            }
            
            // Calculate statistics
            double maxPayment = Collections.max(paymentHistory);
            double minPayment = Collections.min(paymentHistory);
            
            System.out.println("\nPayment Statistics:");
            System.out.println("Highest Payment: RM " + String.format("%.2f", maxPayment));
            System.out.println("Lowest Payment: RM " + String.format("%.2f", minPayment));
            System.out.println("Standard Deviation: RM " + String.format("%.2f", calculateStandardDeviation()));
        }
    }

    private double calculateStandardDeviation() {
        if (paymentHistory.size() < 2) return 0.0;
        
        double sum = paymentHistory.stream().mapToDouble(Double::doubleValue).sum();
        double mean = sum / paymentHistory.size();
        
        double variance = paymentHistory.stream()
            .mapToDouble(payment -> Math.pow(payment - mean, 2))
            .average()
            .orElse(0.0);
        
        return Math.sqrt(variance);
    }

    public String getSpendingTier() {
        if (totalSpent >= 5000) {
            return "PLATINUM";
        } else if (totalSpent >= 2000) {
            return "GOLD";
        } else if (totalSpent >= 500) {
            return "SILVER";
        } else {
            return "BRONZE";
        }
    }

    public boolean isHighValueCustomer() {
        return totalSpent >= 2000 || totalRentals >= 10;
    }

    public double getMonthlyAverageSpending() {
        if (lastPaymentDate == null || totalRentals == 0) return 0.0;
        
        // Approximate calculation based on total spending and time since first rental
        // This is simplified - in a real system, you'd track actual dates
        return totalSpent / Math.max(1, totalRentals); // Simplified calculation
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public double getTotalSpent() { return totalSpent; }
    public int getTotalRentals() { return totalRentals; }
    public int getTotalLoyaltyPointsEarned() { return totalLoyaltyPointsEarned; }
    public LocalDateTime getLastPaymentDate() { return lastPaymentDate; }
    public Map<String, Double> getPaymentMethodBreakdown() { return new HashMap<>(paymentMethodBreakdown); }
    public double getAverageRentalCost() { return averageRentalCost; }
    public String getPreferredPaymentMethod() { return preferredPaymentMethod; }
    public List<Double> getPaymentHistory() { return new ArrayList<>(paymentHistory); }

    // Setters
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setTotalSpent(double totalSpent) { 
        this.totalSpent = totalSpent; 
        if (totalRentals > 0) {
            this.averageRentalCost = this.totalSpent / this.totalRentals;
        }
    }
    public void setTotalRentals(int totalRentals) { 
        this.totalRentals = totalRentals; 
        if (totalRentals > 0) {
            this.averageRentalCost = this.totalSpent / this.totalRentals;
        }
    }
    public void setTotalLoyaltyPointsEarned(int totalLoyaltyPointsEarned) { this.totalLoyaltyPointsEarned = totalLoyaltyPointsEarned; }
    public void setLastPaymentDate(LocalDateTime lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }

    @Override
    public String toString() {
        return "PaymentSummary{" +
                "customerId='" + customerId + '\'' +
                ", totalSpent=" + totalSpent +
                ", totalRentals=" + totalRentals +
                ", averageRentalCost=" + averageRentalCost +
                ", totalLoyaltyPointsEarned=" + totalLoyaltyPointsEarned +
                ", spendingTier='" + getSpendingTier() + '\'' +
                '}';
    }
}
