package com.rentalapp.rental;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.rentalapp.review.Review;

public class RentalRecord {
    private String rentalId;
    private String customerId;
    private String vesselId;
    private String pickupLocation;
    private RentalStatus status;
    private String vesselType;
    private String vesselCategory; 
    private String customerName;

    private LocalDateTime scheduledStart;   
    private LocalDateTime scheduledEnd;     
    private LocalDateTime actualEnd;        

    private Duration duration;              
    private double basePrice;
    private double totalCost;               
    private double taxAmount;               
    private List<AddOn> addOns = new ArrayList<>();
    private Review review;  
    private double damageFee;
    private String paymentMethod;



    // ================= Constructor =================
    public RentalRecord(String rentalId, String customerId, String vesselId, String pickupLocation, LocalDateTime scheduledStart, LocalDateTime scheduledEnd, Duration duration,
                        double basePrice ,double totalCost, String vesselType, String vesselCategory, String customerName) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.vesselId = vesselId;
        this.pickupLocation = pickupLocation;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.duration = duration;
        this.basePrice = basePrice;
        this.totalCost = totalCost;
        this.taxAmount = 0.0; 
        this.status = RentalStatus.ACTIVE;
        this.vesselType = vesselType;
        this.vesselCategory = vesselCategory;
        this.customerName = customerName;
    }

    // ================= Getters =================
    public String getRentalId() { return rentalId; }
    public String getCustomerId() { return customerId; }
    public String getVesselId() { return vesselId; }
    public String getPickupLocation() { return pickupLocation; }
    public LocalDateTime getScheduledStart() { return scheduledStart; }
    public LocalDateTime getScheduledEnd() { return scheduledEnd; }
    public LocalDateTime getActualEnd() { return actualEnd; }
    public Duration getDuration() { return duration; }
    public double getBasePrice() { return basePrice; }
    public double getTotalCost() { return totalCost; }
    public double getTaxAmount() { return taxAmount; }
    public RentalStatus getStatus() { return status; }
    public String getVesselType() { return vesselType; }
    public String getVesselCategory() { return vesselCategory; }
    public String getCustomerName() { return customerName; }
    public List<AddOn> getAddOns() { return addOns; }
    public Review getReview() { return review; }
    public double getDamageFee() { return damageFee;}
    public String getPaymentMethod() { return paymentMethod; }

    // ================= Setters =================
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setVesselId(String vesselId) { this.vesselId = vesselId; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public void setScheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; }
    public void setScheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }
    public void setDuration(Duration duration) { this.duration = duration; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }
    public void setStatus(RentalStatus status) { this.status = status; }
    public void setVesselType(String vesselType) { this.vesselType = vesselType; }
    public void setVesselCategory(String vesselCategory) { this.vesselCategory = vesselCategory; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setReview(Review review) { this.review = review; }
    public void setDamageFee(double damageFee) { this.damageFee = damageFee; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setActualEnd(LocalDateTime actualEnd) {
    this.actualEnd = actualEnd;

    if (scheduledStart != null && actualEnd != null) {
        this.duration = Duration.between(scheduledStart, actualEnd);
    }
}

    // ================= Utility Methods =================
    public void addAddOn(AddOn addOn) {
        this.addOns.add(addOn);
    }

    public double getTotalAddOnsCost() {
        return addOns.stream().mapToDouble(AddOn::getPrice).sum();
    }

    private List<Double> extensionFees = new ArrayList<>();

    public void addExtensionFee(double fee) {
        extensionFees.add(fee);
    }

    public double getTotalExtensionFee() {
        return extensionFees.stream().mapToDouble(Double::doubleValue).sum();
    }


    // Updated to include tax in calculation
    private double calculateTotalCost() {
        return basePrice + taxAmount  + getTotalAddOnsCost() + getTotalExtensionFee() + damageFee;
    }

    // Method to recalculate total cost 
    public void recalculateTotalCost() {
        this.totalCost = calculateTotalCost();
    }


    @Override
    public String toString() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format(
                "Rental ID: %s | Customer: %s | Vessel: %s %s | Location: %s | Duration: %dh %dm | Cost: RM%.2f | Status: %s",
                rentalId, customerName, vesselCategory, vesselType, pickupLocation,
                hours, minutes, totalCost, status);
    }

   public void printDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        System.out.println("\n==================== VESSEL RENTAL DETAILS ====================");
        System.out.println("Rental ID: " + rentalId);
        System.out.println("Customer: " + customerName + " (" + customerId + ")");
        System.out.println("Vessel Category: " + vesselCategory);
        System.out.println("Vessel Type: " + vesselType + " (" + vesselId + ")");
        System.out.println("Pickup Location: " + pickupLocation);
        System.out.println("Scheduled Start: " + scheduledStart.format(formatter));
        System.out.println("Scheduled End:   " + scheduledEnd.format(formatter));
        if (actualEnd != null) {
            System.out.println("Actual End:      " + actualEnd.format(formatter));
        }
        System.out.println("Duration: " + hours + "h " + minutes + "m");
        System.out.println("Base Price: RM " + String.format("%.2f", basePrice));
        System.out.println("Tax (6%): RM " + String.format("%.2f", taxAmount));
        System.out.println("Add-Ons:");
        if (addOns.isEmpty()) {
            System.out.println("  None");
        } else {
            for (AddOn addOn : addOns) {
                System.out.println("  - " + addOn.getName() + ": RM " + String.format("%.2f", addOn.getPrice()));
            }
        }
        System.out.println("Total Amount (Before Fees): RM " +
        String.format("%.2f", basePrice + taxAmount + getTotalAddOnsCost()));
        System.out.println("Extension Fee: RM " + String.format("%.2f", getTotalExtensionFee()));
        System.out.println("Damage Fee: RM " + String.format("%.2f", damageFee));
        System.out.println("Total Cost: RM " + String.format("%.2f", totalCost));
        System.out.println("Status: " + status);

        if (review != null) {
            System.out.println("\n--- Review ---");
            System.out.println(review);
        } else {
            System.out.println("\n--- No Review Submitted ---");
        }

        System.out.println("==============================================================\n");
    }

    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(scheduledEnd) && status == RentalStatus.ACTIVE;
    }

    public long getHoursOverdue() {
        if (isOverdue()) {
            return Duration.between(scheduledEnd, LocalDateTime.now()).toHours();
        }
        return 0;
    }
}