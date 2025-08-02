package com.rentalapp.payment;

import com.rentalapp.rental.RentalRecord;
import com.rentalapp.auth.Customer;
import com.rentalapp.loyalty.LoyaltyPointManager;
import java.time.LocalDateTime;
import java.util.*;

public class PaymentManager {
    private PaymentCalculator paymentCalculator;
    private LoyaltyPointManager loyaltyPointManager;
    private List<Receipt> receipts;
    private Map<String, PaymentSummary> paymentSummaries;
    private int receiptCounter;
    
    public PaymentManager(LoyaltyPointManager loyaltyPointManager) {
        this.paymentCalculator = new PaymentCalculator();
        this.loyaltyPointManager = loyaltyPointManager;
        this.receipts = new ArrayList<>();
        this.paymentSummaries = new HashMap<>();
        this.receiptCounter = 10000;
    }

    public Receipt processPayment(RentalRecord rental, Customer customer, String paymentMethod) {
        // Calculate payment details
        double baseAmount = paymentCalculator.calculateBaseAmount(rental);
        double memberDiscount = paymentCalculator.calculateMemberDiscount(customer, baseAmount, rental);
        double finalAmount = baseAmount - memberDiscount;
        
        // Calculate loyalty points to be earned
        int loyaltyPointsEarned = paymentCalculator.calculateLoyaltyPoints(rental);
        
        // Create receipt
        String receiptId = "REC" + (++receiptCounter);
        Receipt receipt = new Receipt(
            receiptId,
            rental.getRentalId(),
            customer.getCustomerId(),
            customer.getName(),
            rental.getVehicleModel(),
            rental.getRentalDays(),
            baseAmount,
            memberDiscount,
            finalAmount,
            paymentMethod,
            LocalDateTime.now(),
            loyaltyPointsEarned
        );

        // Process payment (simulate payment gateway)
        if (processPaymentGateway(finalAmount, paymentMethod)) {
            // Add loyalty points to customer
            loyaltyPointManager.addPoints(customer.getCustomerId(), loyaltyPointsEarned);
            
            // Store receipt
            receipts.add(receipt);
            
            // Update payment summary for customer
            updatePaymentSummary(customer.getCustomerId(), finalAmount, loyaltyPointsEarned);
            
            System.out.println("Payment processed successfully!");
            System.out.println("Receipt ID: " + receiptId);
            System.out.println("Amount Paid: RM " + String.format("%.2f", finalAmount));
            System.out.println("Loyalty Points Earned: " + loyaltyPointsEarned);
            
            return receipt;
        } else {
            System.out.println("Payment processing failed. Please try again.");
            return null;

}

}
    public Receipt processRefund(String rentalId, double refundAmount) {
        // Find original receipt
        Receipt originalReceipt = findReceiptByRentalId(rentalId);
        if (originalReceipt == null) {
            System.out.println("Original receipt not found for rental: " + rentalId);
            return null;
        }

        // Create refund receipt
        String receiptId = "REF" + (++receiptCounter);
        Receipt refundReceipt = new Receipt(
            receiptId,
            rentalId,
            originalReceipt.getCustomerId(),
            originalReceipt.getCustomerName(),
            originalReceipt.getVehicleModel(),
            0, // refund has 0 rental days
            0, // no base amount for refund
            0, // no discount for refund
            -refundAmount, // negative amount for refund
            "REFUND",
            LocalDateTime.now(),
            -originalReceipt.getLoyaltyPointsEarned() // deduct loyalty points
        );

        // Process refund
        if (processRefundGateway(refundAmount)) {
            // Deduct loyalty points
            loyaltyPointManager.deductPoints(originalReceipt.getCustomerId(), 
                                           originalReceipt.getLoyaltyPointsEarned());
            
            receipts.add(refundReceipt);
            
            System.out.println("Refund processed successfully!");
            System.out.println("Refund Amount: RM " + String.format("%.2f", refundAmount));
            
            return refundReceipt;
        } else {
            System.out.println("Refund processing failed.");
            return null;
        }
    }

    public List<Receipt> getCustomerReceipts(String customerId) {
        List<Receipt> customerReceipts = new ArrayList<>();
        for (Receipt receipt : receipts) {
            if (receipt.getCustomerId().equals(customerId)) {
                customerReceipts.add(receipt);
            }
        }
        return customerReceipts;
    }

    public Receipt getReceiptById(String receiptId) {
        for (Receipt receipt : receipts) {
            if (receipt.getReceiptId().equals(receiptId)) {
                return receipt;
            }
        }
        return null;
    }

    public PaymentSummary getPaymentSummary(String customerId) {
        return paymentSummaries.get(customerId);
    }

    public void printReceipt(String receiptId) {
        Receipt receipt = getReceiptById(receiptId);
        if (receipt != null) {
            receipt.printReceipt();
        } else {
            System.out.println("Receipt not found: " + receiptId);
        }
    }

    public double calculateTotalRevenue() {
        double totalRevenue = 0;
        for (Receipt receipt : receipts) {
            if (receipt.getFinalAmount() > 0) { // Only count positive amounts (not refunds)
                totalRevenue += receipt.getFinalAmount();
            }
        }
        return totalRevenue;
    }

    public List<Receipt> getReceiptsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Receipt> filteredReceipts = new ArrayList<>();
        for (Receipt receipt : receipts) {
            if (!receipt.getPaymentDateTime().isBefore(startDate) && 
                !receipt.getPaymentDateTime().isAfter(endDate)) {
                filteredReceipts.add(receipt);
            }
        }
        return filteredReceipts;
    }

    // Private helper methods
    private Receipt findReceiptByRentalId(String rentalId) {
        for (Receipt receipt : receipts) {
            if (receipt.getRentalId().equals(rentalId)) {
                return receipt;
            }
        }
        return null;
    }

    private boolean processPaymentGateway(double amount, String paymentMethod) {
        // Simulate payment gateway processing
        System.out.println("Processing payment of RM " + String.format("%.2f", amount) + 
                          " via " + paymentMethod + "...");
        
        return true;
    }

    private boolean processRefundGateway(double amount) {
        // Simulate refund processing
        System.out.println("Processing refund of RM " + String.format("%.2f", amount) + "...");
        
        // always succeed
        return true;
    }

    private void updatePaymentSummary(String customerId, double amount, int loyaltyPoints) {
        PaymentSummary summary = paymentSummaries.get(customerId);
        if (summary == null) {
            summary = new PaymentSummary(customerId);
            paymentSummaries.put(customerId, summary);
        }
        summary.addPayment(amount, loyaltyPoints);
    }

    public void generateMonthlyReport() {
        System.out.println("\n==================== MONTHLY PAYMENT REPORT ====================");
        System.out.println("Total Receipts: " + receipts.size());
        System.out.println("Total Revenue: RM " + String.format("%.2f", calculateTotalRevenue()));
        
        Map<String, Integer> paymentMethodCount = new HashMap<>();
        for (Receipt receipt : receipts) {
            paymentMethodCount.put(receipt.getPaymentMethod(), 
                paymentMethodCount.getOrDefault(receipt.getPaymentMethod(), 0) + 1);
        }
        
        System.out.println("\nPayment Methods:");
        for (Map.Entry<String, Integer> entry : paymentMethodCount.entrySet()) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue() + " transactions");
        }
        System.out.println("===============================================================\n");
    }
    
    /**
     * Process late fee payment
     */
    public Receipt processLateFeePayment(Customer customer, double lateFee) {
        // Create a special receipt for late fee
        String receiptId = "LATE" + (++receiptCounter);
        Receipt lateFeeReceipt = new Receipt(
            receiptId,
            "LATE_FEE",  // No specific rental ID for late fees
            customer.getCustomerId(),
            customer.getName(),
            "Late Fee Payment",
            0, // no rental days
            lateFee,
            0, // no discount for late fees
            lateFee,
            "Credit Card", // Default payment method
            LocalDateTime.now(),
            0 // no loyalty points for late fees
        );
        
        // Process payment
        if (processPaymentGateway(lateFee, "Credit Card")) {
            receipts.add(lateFeeReceipt);
            System.out.println("Late fee payment processed successfully!");
            System.out.println("Late Fee: RM " + String.format("%.2f", lateFee));
            return lateFeeReceipt;
        } else {
            System.out.println("Late fee payment failed. Please try again.");
            return null;
        }
    }
    
    /**
     * Process extension payment
     */
    public Receipt processExtensionPayment(Customer customer, double extensionCost) {
        // Create a special receipt for extension
        String receiptId = "EXT" + (++receiptCounter);
        Receipt extensionReceipt = new Receipt(
            receiptId,
            "EXTENSION",  // Extension payment
            customer.getCustomerId(),
            customer.getName(),
            "Rental Extension",
            0, // no rental days
            extensionCost,
            0, // no discount for extensions
            extensionCost,
            "Credit Card", // Default payment method
            LocalDateTime.now(),
            (int) (extensionCost / 10) // 1 point per RM10 spent
        );
        
        // Process payment
        if (processPaymentGateway(extensionCost, "Credit Card")) {
            receipts.add(extensionReceipt);
            
            // Add loyalty points for extension
            loyaltyPointManager.addPoints(customer.getCustomerId(), extensionReceipt.getLoyaltyPointsEarned());
            
            System.out.println("Extension payment processed successfully!");
            System.out.println("Extension Cost: RM " + String.format("%.2f", extensionCost));
            System.out.println("Loyalty Points Earned: " + extensionReceipt.getLoyaltyPointsEarned());
            return extensionReceipt;
        } else {
            System.out.println("Extension payment failed. Please try again.");
            return null;
        }
    }

}
