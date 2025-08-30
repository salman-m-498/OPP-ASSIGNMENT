package com.rentalapp.payment;

import com.rentalapp.rental.AddOn;
import com.rentalapp.rental.RentalRecord;
import com.rentalapp.auth.Customer;
import com.rentalapp.auth.MemberCustomer;
import com.rentalapp.loyalty.LoyaltyAccount;
import com.rentalapp.loyalty.LoyaltyPointManager;
import java.time.LocalDateTime;
import java.time.Duration;
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

public Receipt processPayment(RentalRecord rental, Customer customer,
                              String paymentMethod, String cardNumber, String eWalletPhone) {

    double finalAmount = rental.getTotalCost();

     double addOnsAmount = 0.0;
    if (rental.getAddOns() != null) {
        addOnsAmount = rental.getAddOns().stream().mapToDouble(AddOn::getTotalPrice).sum();
    }

    double baseAmount = finalAmount - addOnsAmount;
    if (baseAmount < 0) baseAmount = 0.0;

   
    int loyaltyPointsEarned = 0;
    if (customer instanceof MemberCustomer) {
        loyaltyPointsEarned = loyaltyPointManager.getPointsForVessel(rental.getVesselCategory());
        LoyaltyAccount acct = loyaltyPointManager.getLoyaltyAccount(customer.getCustomerId());
        if (acct != null && acct.isVipMember()) {
            loyaltyPointsEarned = (int) Math.round(loyaltyPointsEarned * 1.15);
        }
    }

    String receiptId = "REC" + (++receiptCounter);
    Receipt receipt = new Receipt(
            receiptId,
            rental.getRentalId(),
            customer.getCustomerId(),
            customer.getName(),
            rental.getVesselId(),
            rental.getVesselType(),
            rental.getVesselCategory(),
            rental.getDuration(),
            baseAmount,
            addOnsAmount,
            0.0,               
            finalAmount,
            paymentMethod,
            cardNumber,
            eWalletPhone,
            LocalDateTime.now(),
            loyaltyPointsEarned, 
            rental.getAddOns()
    );

    // Use the finalAmount 
    if (processPaymentGateway(finalAmount, paymentMethod)) {
        customer.addToTotalSpent(finalAmount);
        receipts.add(receipt);
        updatePaymentSummary(customer.getCustomerId(), finalAmount, 0, paymentMethod, true);

        System.out.println(paymentMethod + " payment processed successfully!");
        return receipt;
    } else {
        System.out.println(paymentMethod + " payment failed.");
        return null;
    }
}

   public Receipt processRefund(String rentalId, double refundAmount) {
    if (refundAmount <= 0) {
        System.out.println("No refund applicable as per policy.");
        return null;
    }

    // Find original receipt
    Receipt originalReceipt = findReceiptByRentalId(rentalId);
    if (originalReceipt == null) {
        System.out.println("Original receipt not found for rental: " + rentalId);
        return null;
    }

    // Create refund receipt
    String receiptId = "REF" + (++receiptCounter);

    String paymentMethod = originalReceipt.getPaymentMethod();
    String maskedCardNumber = originalReceipt.getMaskedCardNumber();
    String eWalletPhoneNumber = originalReceipt.getEWalletPhoneNumber();

    Receipt refundReceipt = new Receipt(
        receiptId,
        originalReceipt.getRentalId(),
        originalReceipt.getCustomerId(),
        originalReceipt.getCustomerName(),
        originalReceipt.getVesselId(),
        originalReceipt.getVesselType(),
        originalReceipt.getVesselCategory(),
        originalReceipt.getDuration(),
        0,                          
        0,                          
        0,                          
        -refundAmount,              
        paymentMethod,
        maskedCardNumber,
        eWalletPhoneNumber,
        LocalDateTime.now(),
        -originalReceipt.getLoyaltyPointsEarned(), 
        new ArrayList<>()           
    );

    // Process refund through gateway
    if (processRefundGateway(refundAmount)) {
        // Deduct loyalty points
        loyaltyPointManager.deductPoints(
            originalReceipt.getCustomerId(),
            originalReceipt.getLoyaltyPointsEarned()
        );

        receipts.add(refundReceipt);

        System.out.println("Refund processed successfully!");
        System.out.println("Refund Amount: RM " + String.format("%.2f", refundAmount));
        System.out.println("Refunded via: " + paymentMethod);

        return refundReceipt;
    } else {
        System.out.println("Refund processing failed.");
        return null;
    }
}
    

public Receipt processCustomPayment(
        RentalRecord rental,
        Customer customer,
        double amount,
        String paymentMethod,
        String maskedCard,
        String eWalletPhone
) {
    if (amount <= 0) {
        System.out.println("Invalid payment amount.");
        return null;
    }

    String receiptId = "CUST" + (++receiptCounter);

    // If rental == null → membership upgrade or other custom fee
    String rentalId, vesselId, vesselType, vesselCategory;
    Duration duration;
    String description;

    if (rental != null) {
        rentalId = rental.getRentalId();
        vesselId = rental.getVesselId();
        vesselType = rental.getVesselType();
        vesselCategory = rental.getVesselCategory();
        duration = rental.getDuration();
        description = "Rental Payment";
    } else {
        rentalId = "-";
        vesselId = "-";
        vesselType = "Membership Upgrade";
        vesselCategory = "-";
        duration = Duration.ZERO;
        description = "Membership Upgrade Fee";
    }

   Receipt receipt = new Receipt(
    receiptId,
    rentalId,           // use "-" if membership
    customer.getCustomerId(),
    customer.getName(),
    vesselId,           // "-" if membership
    vesselType,         // "Membership Upgrade" if membership
    vesselCategory,     // "-" if membership
    duration,           // Duration.ZERO if membership
    0,                  // base amount irrelevant
    0,
    0,
    amount,             // final amount is the fee
    paymentMethod,
    maskedCard,
    eWalletPhone,
    LocalDateTime.now(),
    0,                  // no loyalty points for upgrades/penalties
    new ArrayList<>()
    );

    if (processPaymentGateway(amount, paymentMethod)) {
        customer.addToTotalSpent(amount);
        receipts.add(receipt);
        updatePaymentSummary(customer.getCustomerId(), amount, 0, paymentMethod, false);

        System.out.println("\n" + description + " processed successfully! Amount: RM " 
                           + String.format("%.2f", amount));
        return receipt;
    } else {
        System.out.println("Payment failed.");
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
            if (receipt.getFinalAmount() > 0) { 
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

    public Receipt getReceiptByRentalId(String rentalId) {
    for (Receipt receipt : receipts) {
        if (receipt.getRentalId() != null && receipt.getRentalId().equals(rentalId)) {
            return receipt;
        }
    }
    return null;
    }

public void updateReceiptLoyaltyPoints(String rentalId, int loyaltyPoints) {
    Receipt receipt = getReceiptByRentalId(rentalId);
    if (receipt != null) {
        receipt.setLoyaltyPointsEarned(loyaltyPoints);

        PaymentSummary summary = paymentSummaries.get(receipt.getCustomerId());
        if (summary != null) {
            // ✅ accumulate instead of overwrite
            summary.addLoyaltyPoints(loyaltyPoints);
        }
    }
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
        System.out.println("\nProcessing payment of RM " + String.format("%.2f", amount) + 
                          " via " + paymentMethod + "...");
        
        return true;
    }

    private boolean processRefundGateway(double amount) {
        // Simulate refund processing
        System.out.println("\nProcessing refund of RM " + String.format("%.2f", amount) + "...");
        
        // always succeed
        return true;
    }

   private void updatePaymentSummary(String customerId, double amount, int loyaltyPoints, String paymentMethod, boolean isNewRental) {
    PaymentSummary summary = paymentSummaries.get(customerId);
    if (summary == null) {
        summary = new PaymentSummary(customerId);
        paymentSummaries.put(customerId, summary);
    }
    summary.addPayment(amount, loyaltyPoints, paymentMethod, isNewRental);
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
 * Process extension or late return payment
 */
public Receipt processExtensionPayment(
        RentalRecord rental,
        Customer customer,
        Duration additionalDuration,
        boolean isLateReturn,
        String paymentMethod,
        String maskedCardNumber,
        String eWalletPhoneNumber
) {
    // Calculate cost using PaymentCalculator
    double extensionCost = paymentCalculator.calculateExtensionCost(
            rental.getVesselCategory(),
            additionalDuration
    );

    String prefix = isLateReturn ? "LATE" : "EXT";
    String type = isLateReturn ? "Late Return Extension" : "Rental Extension";

    int loyaltyPointsEarned = 0;
    if (!isLateReturn && customer instanceof MemberCustomer) {
        loyaltyPointsEarned = (int) (extensionCost / 10);
    }

    String receiptId = prefix + (++receiptCounter);

    Receipt receipt = new Receipt(
        receiptId,
        "EXTENSION",
        customer.getCustomerId(),
        customer.getName(),
        rental.getVesselId(),
        type,                           
        rental.getVesselCategory(),
        additionalDuration,
        extensionCost,                  
        0,                              
        0,                             
        extensionCost,                  
        paymentMethod,
        maskedCardNumber,               
        eWalletPhoneNumber,             
        LocalDateTime.now(),
        loyaltyPointsEarned,
        new ArrayList<>()               
    );

    // Process payment
    if (processPaymentGateway(extensionCost, paymentMethod)) {
        customer.addToTotalSpent(extensionCost);
        receipts.add(receipt);

        // Loyalty points only for planned extensions
         if (!isLateReturn && loyaltyPointsEarned > 0 && customer instanceof MemberCustomer) {
            loyaltyPointManager.addPoints(customer.getCustomerId(), loyaltyPointsEarned);
        }

         updatePaymentSummary(customer.getCustomerId(), extensionCost, loyaltyPointsEarned, paymentMethod, false);

        return receipt;
    } else {
        System.out.println(type + " payment failed. Please try again.");
        return null;
    }
}

    public Receipt processAdditionalCharge(
        RentalRecord rental,
        Customer customer,
        double amount,
        String paymentMethod,
        String maskedCardNumber,
        String eWalletPhoneNumber
) {
    if (amount <= 0) {
        System.out.println("Invalid additional charge amount.");
        return null;
    }

    String receiptId = "CHG" + (++receiptCounter);

    Receipt chargeReceipt = new Receipt(
        receiptId,
        rental.getRentalId(),
        customer.getCustomerId(),
        customer.getName(),
        rental.getVesselId(),
        "Additional Charge",
        rental.getVesselCategory(),
        Duration.ZERO,
        amount,                 
        0,                      
        0,                      
        amount,                 
        paymentMethod,
        maskedCardNumber,
        eWalletPhoneNumber,
        LocalDateTime.now(),
        0,                      
        new ArrayList<>()
    );

    if (processPaymentGateway(amount, paymentMethod)) {
        customer.addToTotalSpent(amount);
        receipts.add(chargeReceipt);
        updatePaymentSummary(customer.getCustomerId(), amount, 0, paymentMethod, false);
        System.out.println("Additional charge processed successfully! Amount: RM " + String.format("%.2f", amount));
        return chargeReceipt;
    } else {
        System.out.println("Additional charge payment failed.");
        return null;
    }
}
}
