package com.rentalapp.rental;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class RentalRequest {
    private String customerId;
    private String vesselId;
    private String pickupLocation;
    private LocalDateTime scheduledStart;   
    private LocalDateTime scheduledEnd;     
    private Duration duration;              
    private List<AddOn> addOns; 
    private double totalCost; 

    // ================= Constructor =================
    public RentalRequest(String customerId, String vesselId, String pickupLocation,
                         LocalDateTime scheduledStart, LocalDateTime scheduledEnd, Duration duration) {
        this.customerId = customerId;
        this.vesselId = vesselId;
        this.pickupLocation = pickupLocation;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.duration = duration;
        this.addOns = new ArrayList<>();
        this.totalCost = 0.0; 
    }

    // Alternative constructor
    public RentalRequest(String customerId, String vesselId, String pickupLocation,
                         LocalDateTime scheduledStart, LocalDateTime scheduledEnd) {
        this.customerId = customerId;
        this.vesselId = vesselId;
        this.pickupLocation = pickupLocation;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.duration = Duration.between(scheduledStart, scheduledEnd);
    }

    // ================= Getters =================
    public String getCustomerId() { return customerId; }
    public String getVesselId() { return vesselId; }
    public String getPickupLocation() { return pickupLocation; }
    public LocalDateTime getScheduledStart() { return scheduledStart; }
    public LocalDateTime getScheduledEnd() { return scheduledEnd; }
    public Duration getDuration() { return duration; }
    public List<AddOn> getAddOns() { return addOns; }
    public double getTotalCost() { return totalCost; }
    
    // ================= Setters =================
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setVesselId(String vesselId) { this.vesselId = vesselId; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public void setScheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; }
    public void setScheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }
    public void setDuration(Duration duration) { this.duration = duration; }
    public void setAddOns(List<AddOn> addOns) { this.addOns = addOns; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public double getTotalAddOnsCost() {
    return addOns.stream().mapToDouble(AddOn::getPrice).sum();
}
}
