package com.rentalapp.payment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.Duration;
import com.rentalapp.rental.AddOn;

public class Receipt {
    private String receiptId;
    private String rentalId;
    private String customerId;
    private String customerName;
    private String vesselId;
    private String vesselType;
    private String vesselCategory;
    private Duration duration;           
    private double baseAmount;
    private double addOnsAmount;
    private double memberDiscount;
    private double finalAmount;
    private String paymentMethod;
    private LocalDateTime paymentDateTime;
    private int loyaltyPointsEarned;
    private List<AddOn> addOns;
    private String maskedCardNumber;
    private String eWalletPhoneNumber;

      // Unified constructor (works for both card & e-wallet)
public Receipt(String receiptId, String rentalId, String customerId, String customerName,
               String vesselId, String vesselType, String vesselCategory, Duration duration,
               double baseAmount, double addOnsAmount, double memberDiscount, double finalAmount,
               String paymentMethod, String maskedCardNumber, String eWalletPhoneNumber,
               LocalDateTime paymentDateTime, int loyaltyPointsEarned, List<AddOn> addOns) {
    this.receiptId = receiptId;
    this.rentalId = rentalId;
    this.customerId = customerId;
    this.customerName = customerName;
    this.vesselId = vesselId;
    this.vesselType = vesselType;
    this.vesselCategory = vesselCategory;
    this.duration = duration;
    this.baseAmount = baseAmount;
    this.addOnsAmount = addOnsAmount;
    this.memberDiscount = memberDiscount;
    this.finalAmount = finalAmount;
    this.paymentMethod = paymentMethod;
    this.maskedCardNumber = maskedCardNumber;
    this.eWalletPhoneNumber = eWalletPhoneNumber;
    this.paymentDateTime = paymentDateTime;
    this.loyaltyPointsEarned = loyaltyPointsEarned;
    this.addOns = addOns;
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
        System.out.println("Vessel ID: " + vesselId);
        System.out.println("Vessel Type: " + vesselType);
        System.out.println("Vessel Category: " + vesselCategory);

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        System.out.println("Duration: " + hours + "h " + minutes + "m");
        System.out.println("-".repeat(50));
        System.out.println("Base Amount(include tax): " + formatCurrency(baseAmount));
        
         if (addOns != null && !addOns.isEmpty()) {
            System.out.println("Add-Ons:");
            for (AddOn addon : addOns) {
                System.out.println("  - " + addon.getName() + ": " + formatCurrency(addon.getPrice()));
            }
            System.out.println("Add-Ons Total: " + formatCurrency(addOnsAmount));
        }

        if (memberDiscount > 0) {
            System.out.println("Member Discount: -" + formatCurrency(memberDiscount));
        }

        if (maskedCardNumber != null && !maskedCardNumber.isEmpty()) {
            System.out.println("Card Used: " + maskedCardNumber);
        }

        if (eWalletPhoneNumber != null && !eWalletPhoneNumber.isEmpty()) {
             System.out.println("E-Wallet Phone: " + eWalletPhoneNumber);
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
        System.out.println(receiptId + " | " + customerName + " | " + vesselType +
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
    public String getVesselId() { return vesselId; }
    public String getVesselType() { return vesselType; }
    public String getVesselCategory() { return vesselCategory; }
    public Duration getDuration() { return duration; }    
    public double getBaseAmount() { return baseAmount; }
    public double getAddOnsAmount() { return addOnsAmount; }
    public double getMemberDiscount() { return memberDiscount; }
    public double getFinalAmount() { return finalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getPaymentDateTime() { return paymentDateTime; }
    public int getLoyaltyPointsEarned() { return loyaltyPointsEarned; }
    public List<AddOn> getAddOns() { return addOns; }
    public String getMaskedCardNumber() { return maskedCardNumber; }
    public String getEWalletPhoneNumber() { return eWalletPhoneNumber; }

    // Setters
    public void setReceiptId(String receiptId) { this.receiptId = receiptId; }
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setVesselId(String vesselId) { this.vesselId = vesselId; }
    public void setVesselType(String vesselType) { this.vesselType = vesselType; }
    public void setVesselCategory(String vesselCategory) { this.vesselCategory = vesselCategory; }
    public void setDuration(Duration duration) { this.duration = duration; }
    public void setBaseAmount(double baseAmount) { this.baseAmount = baseAmount; }
     public void setAddOnsAmount(double addOnsAmount) { this.addOnsAmount = addOnsAmount; }   
    public void setMemberDiscount(double memberDiscount) { this.memberDiscount = memberDiscount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentDateTime(LocalDateTime paymentDateTime) { this.paymentDateTime = paymentDateTime; }
    public void setLoyaltyPointsEarned(int loyaltyPointsEarned) { this.loyaltyPointsEarned = loyaltyPointsEarned; }
    public void setAddOns(List<AddOn> addOns) { this.addOns = addOns; }
    public void setMaskedCardNumber(String maskedCardNumber) { this.maskedCardNumber = maskedCardNumber; }
    public void setEWalletPhoneNumber(String eWalletPhoneNumber) { this.eWalletPhoneNumber = eWalletPhoneNumber; }

    @Override
    public String toString() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return "Receipt{" +
                "receiptId='" + receiptId + '\'' +
                ", rentalId='" + rentalId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", vesselId='" + vesselId + '\'' +
                ", vesselType='" + vesselType + '\'' +
                ", vesselCategory='" + vesselCategory + '\'' +
                ", duration=" + hours + "h " + minutes + "m" +
                ", finalAmount=" + finalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentDateTime=" + paymentDateTime +
                ", addOns=" + addOns +
                '}';
    }
}
