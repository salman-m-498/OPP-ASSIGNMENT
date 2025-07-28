package com.rentalapp.loyalty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoyaltyTransaction {
    private String transactionId;
    private String customerId;
    private int pointsChange;
    private String transactionType;
    private String description;
    private LocalDateTime transactionDate;

    public LoyaltyTransaction(String transactionId, String customerId, int pointsChange,
                             String transactionType, String description, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.pointsChange = pointsChange;
        this.transactionType = transactionType;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    public boolean isPointsEarned() {
        return pointsChange > 0;
    }

    public boolean isPointsDeducted() {
        return pointsChange < 0;
    }

    public void printTransaction() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        System.out.println("\n================ LOYALTY TRANSACTION ================");
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Customer ID: " + customerId);
        System.out.println("Points Change: " + (pointsChange > 0 ? "+" : "") + pointsChange);
        System.out.println("Transaction Type: " + transactionType);
        System.out.println("Description: " + description);
        System.out.println("Date & Time: " + transactionDate.format(formatter));
        System.out.println("=====================================================\n");
    }

    public void printSummary() {
        String pointsStr = (pointsChange > 0 ? "+" : "") + pointsChange;
        System.out.println(transactionId + " | " + transactionDate.toLocalDate() + 
                          " | " + pointsStr + " pts | " + transactionType + " | " + description);
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getCustomerId() { return customerId; }
    public int getPointsChange() { return pointsChange; }
    public String getTransactionType() { return transactionType; }
    public String getDescription() { return description; }
    public LocalDateTime getTransactionDate() { return transactionDate; }

    // Setters
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setPointsChange(int pointsChange) { this.pointsChange = pointsChange; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public void setDescription(String description) { this.description = description; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    @Override
    public String toString() {
        return "LoyaltyTransaction{" +
                "transactionId='" + transactionId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", pointsChange=" + pointsChange +
                ", transactionType='" + transactionType + '\'' +
                ", description='" + description + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        LoyaltyTransaction that = (LoyaltyTransaction) obj;
        return transactionId != null ? transactionId.equals(that.transactionId) : that.transactionId == null;
    }

    @Override
    public int hashCode() {
        return transactionId != null ? transactionId.hashCode() : 0;
    }
}
