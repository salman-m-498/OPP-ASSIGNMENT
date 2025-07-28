package com.rentalapp.payment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Receipt {
    private String receiptId;
    private String rentalId;
    private String customerId;
    private String customerName;
    private String vehicleModel;
    private int rentalDays;
    private double baseAmount;
    private double memberDiscount;
    private double finalAmount;
    private String paymentMethod;
    private LocalDateTime paymentDateTime;
    private int loyaltyPointsEarned;

     public Receipt(String receiptId, String rentalId, String customerId, String customerName,
                  String vehicleModel, int rentalDays, double baseAmount, double memberDiscount,
                  double finalAmount, String paymentMethod, LocalDateTime paymentDateTime,
                  int loyaltyPointsEarned) {
        this.receiptId = receiptId;
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.vehicleModel = vehicleModel;
        this.rentalDays = rentalDays;
        this.baseAmount = baseAmount;
        this.memberDiscount = memberDiscount;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentDateTime = paymentDateTime;
        this.loyaltyPointsEarned = loyaltyPointsEarned;
    }

    public void printReceipt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              RENTAL RECEIPT");
        System.out.println("=".repeat(50));
        System.out.println("Receipt ID: " + receiptId);
        System.out.println("Rental ID: " + rentalId);
        System.out.println("Date & Time: " + paymentDateTime.format(formatter));
        System.out.println("-".repeat(50));
        System.out.println("Customer: " + customerName);
        System.out.println("Customer ID: " + customerId);
        System.out.println("-".repeat(50));
        System.out.println("Vehicle: " + vehicleModel);
        System.out.println("Rental Days: " + rentalDays);
        System.out.println("-".repeat(50));
        System.out.println("Base Amount: " + formatCurrency(baseAmount));
        
        if (memberDiscount > 0) {
            System.out.println("Member Discount: -" + formatCurrency(memberDiscount));
        }
        
        System.out.println("-".repeat(50));
        System.out.println("TOTAL AMOUNT: " + formatCurrency(finalAmount));
        System.out.println("Payment Method: " + paymentMethod);
        
        if (loyaltyPointsEarned > 0) {
            System.out.println("Loyalty Points Earned: " + loyaltyPointsEarned);
        } else if (loyaltyPointsEarned < 0) {
            System.out.println("Loyalty Points Deducted: " + Math.abs(loyaltyPointsEarned));
        }
        
        System.out.println("-".repeat(50));
        System.out.println("Thank you for choosing our rental service!");
        System.out.println("=".repeat(50) + "\n");
    }

    public void printSummaryReceipt() {
        System.out.println(receiptId + " | " + customerName + " | " + vehicleModel + 
                          " | " + formatCurrency(finalAmount) + " | " + paymentMethod);
    }

    private String formatCurrency(double amount) {
        if (amount < 0) {
            return "-RM " + String.format("%.2f", Math.abs(amount));
        }
        return "RM " + String.format("%.2f", amount);
    }

    // Getters
    public String getReceiptId() { return receiptId; }
    public String getRentalId() { return rentalId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getVehicleModel() { return vehicleModel; }
    public int getRentalDays() { return rentalDays; }
    public double getBaseAmount() { return baseAmount; }
    public double getMemberDiscount() { return memberDiscount; }
    public double getFinalAmount() { return finalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getPaymentDateTime() { return paymentDateTime; }
    public int getLoyaltyPointsEarned() { return loyaltyPointsEarned; }

    // Setters
    public void setReceiptId(String receiptId) { this.receiptId = receiptId; }
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public void setRentalDays(int rentalDays) { this.rentalDays = rentalDays; }
    public void setBaseAmount(double baseAmount) { this.baseAmount = baseAmount; }
    public void setMemberDiscount(double memberDiscount) { this.memberDiscount = memberDiscount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentDateTime(LocalDateTime paymentDateTime) { this.paymentDateTime = paymentDateTime; }
    public void setLoyaltyPointsEarned(int loyaltyPointsEarned) { this.loyaltyPointsEarned = loyaltyPointsEarned; }

    @Override
    public String toString() {
        return "Receipt{" +
                "receiptId='" + receiptId + '\'' +
                ", rentalId='" + rentalId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", finalAmount=" + finalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentDateTime=" + paymentDateTime +
                '}';
    }
}
