package com.rentalapp.maintenance;

import java.time.LocalDate;

public class MaintenanceSchedule {
    private String vehicleId;
    private String vehicleModel;
    private LocalDate scheduledDate;
    private MaintenanceType type;
    private String description;

    public MaintenanceSchedule(String vehicleId, String vehicleModel, 
                               MaintenanceType type, LocalDate scheduledDate, 
                               String description) {
        this.vehicleId = vehicleId;
        this.vehicleModel = vehicleModel;
        this.type = type;
        this.scheduledDate = scheduledDate;
        this.description = description;
    }

    // Getters
    public String getVehicleId() { return vehicleId; }
    public String getVehicleModel() { return vehicleModel; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public MaintenanceType getType() { return type; }
    public String getDescription() { return description; }

    // Optional: toString method
    @Override
    public String toString() {
        return String.format("Scheduled Maintenance: %s (%s) on %s [%s]",
                vehicleModel, vehicleId, scheduledDate, type);
    }
}
