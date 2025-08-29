package com.rentalapp.maintenance;

import java.time.LocalDate;

public class MaintenanceSchedule {
    private String vesselId;
    private String vesselType;
    private LocalDate scheduledDate;
    private MaintenanceType type;
    private String description;

    public MaintenanceSchedule(String vesselId, String vesselType, 
                               MaintenanceType type, LocalDate scheduledDate, 
                               String description) {
        this.vesselId = vesselId;
        this.vesselType = vesselType;
        this.type = type;
        this.scheduledDate = scheduledDate;
        this.description = description;
    }

    // Getters
    public String getVesselId() { return vesselId; }
    public String getVesselType() { return vesselType; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public MaintenanceType getType() { return type; }
    public String getDescription() { return description; }

    // Optional: toString method
    @Override
    public String toString() {
        return String.format("Scheduled Maintenance: %s (%s) on %s [%s]",
                vesselType, vesselId, scheduledDate, type);
    }
}