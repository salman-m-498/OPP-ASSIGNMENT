package com.rentalapp.rental;

import java.time.Duration;
import java.time.LocalDateTime;

public class RentalHistoryRecord {
    private String rentalId;
    private String customerId;
    private String customerName;
    private String vesselId;
    private String vesselModel;
    private String vesselType;
    private String location;
    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;
    private Duration duration;
    private double totalAmount;
    private String paymentMethod;   
    private String status;
    private int loyaltyPointsEarned;
    private LocalDateTime actualEnd;


    public RentalHistoryRecord(
    String rentalId,
    String customerId,
    String customerName,
    String vesselId,
    String vesselModel,
    String vesselType,
    String location,
    LocalDateTime scheduledStart,
    LocalDateTime scheduledEnd,
    LocalDateTime actualEnd,   // <-- NEW
    Duration duration,
    double totalAmount,
    String paymentMethod,
    String status,
    int loyaltyPointsEarned
) {
    this.rentalId = rentalId;
    this.customerId = customerId;
    this.customerName = customerName;
    this.vesselId = vesselId;
    this.vesselModel = vesselModel;
    this.vesselType = vesselType;
    this.location = location;
    this.scheduledStart = scheduledStart;
    this.scheduledEnd = scheduledEnd;
    this.actualEnd = actualEnd;             // ✅ store it
    this.duration = duration;
    this.totalAmount = totalAmount;
    this.paymentMethod = paymentMethod;
    this.status = status;
    this.loyaltyPointsEarned = loyaltyPointsEarned;
}

    // ===== Getters =====
    public String getRentalId() { return rentalId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getVesselId() { return vesselId; }
    public String getVesselModel() { return vesselModel; }
    public String getVesselType() { return vesselType; }
    public String getLocation() { return location; }
    public LocalDateTime getScheduledStart() { return scheduledStart; }
    public LocalDateTime getScheduledEnd() { return scheduledEnd; }
    public Duration getDuration() { return duration; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }   
    public String getStatus() { return status; }
    public int getLoyaltyPointsEarned() { return loyaltyPointsEarned; }
    public LocalDateTime getActualEnd() { return actualEnd; }

    // ===== Setters =====
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setVesselId(String vesselId) { this.vesselId = vesselId; }
    public void setVesselModel(String vesselModel) { this.vesselModel = vesselModel; }
    public void setVesselType(String vesselType) { this.vesselType = vesselType; }
    public void setLocation(String location) { this.location = location; }
    public void setScheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; }
    public void setScheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }
    public void setDuration(Duration duration) { this.duration = duration; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }   
    public void setStatus(String status) { this.status = status; }
    public void setLoyaltyPointsEarned(int loyaltyPointsEarned) { this.loyaltyPointsEarned = loyaltyPointsEarned; }
}
