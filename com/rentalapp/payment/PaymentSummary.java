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

    // Add payment with loyalty points and optional method
    public void addPayment(double amount, int loyaltyPoints, String paymentMethod, boolean isNewRental) {
    if (amount < 0) amount = 0; // Ensure refunds don't reduce total spent

    this.totalSpent += amount;

    // Only increment rentals when this is a new rental payment
    if (isNewRental) {
        this.totalRentals++;
    }

    this.totalLoyaltyPointsEarned += loyaltyPoints;
    this.lastPaymentDate = LocalDateTime.now();
    this.paymentHistory.add(amount);

    // Update payment method breakdown
    if (paymentMethod != null && !paymentMethod.isEmpty()) {
        paymentMethodBreakdown.put(paymentMethod,
            paymentMethodBreakdown.getOrDefault(paymentMethod, 0.0) + amount);
        updatePreferredPaymentMethod();
    }

    // Update average rental cost 
    this.averageRentalCost = totalRentals > 0 ? totalSpent / totalRentals : 0;
    }

    public void addLoyaltyPoints(int points) {
    this.totalLoyaltyPointsEarned += points;
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

            double maxPayment = Collections.max(paymentHistory);
            double minPayment = Collections.min(paymentHistory);

            System.out.println("\nPayment Statistics:");
            System.out.println("Highest Payment: RM " + String.format("%.2f", maxPayment));
            System.out.println("Lowest Payment: RM " + String.format("%.2f", minPayment));
        }
    }

    public boolean isHighValueCustomer() {
        return totalSpent >= 2000 || totalRentals >= 10;
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
        this.averageRentalCost = totalRentals > 0 ? totalSpent / totalRentals : 0;
    }
    public void setTotalRentals(int totalRentals) {
        this.totalRentals = totalRentals;
        this.averageRentalCost = totalRentals > 0 ? totalSpent / totalRentals : 0;
    }
    public void setTotalLoyaltyPointsEarned(int points) { this.totalLoyaltyPointsEarned = points; }
    public void setLastPaymentDate(LocalDateTime date) { this.lastPaymentDate = date; }

    @Override
    public String toString() {
        return "PaymentSummary{" +
                "customerId='" + customerId + '\'' +
                ", totalSpent=" + totalSpent +
                ", totalRentals=" + totalRentals +
                ", averageRentalCost=" + averageRentalCost +
                ", totalLoyaltyPointsEarned=" + totalLoyaltyPointsEarned +
                ", preferredPaymentMethod='" + preferredPaymentMethod + '\'' +
                '}';
    }
}
