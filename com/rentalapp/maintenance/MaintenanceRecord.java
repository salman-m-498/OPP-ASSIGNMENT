package com.rentalapp.maintenance;

import java.time.LocalDate;

public class MaintenanceRecord {
    private String recordId;
    private String vehicleId;
    private String vehicleModel;
    private MaintenanceType type;
    private LocalDate scheduledDate;
    private LocalDate actualStartDate;
    private LocalDate actualCompletionDate;
    private MaintenanceStatus status;
    private String description;
    private double cost;
    private String technician;
    private String notes;
    private int mileage;
    private String partsUsed;

    public MaintenanceRecord(String recordId, String vehicleId, String vehicleModel,
                           MaintenanceType type, LocalDate scheduledDate, String description,
                           MaintenanceStatus status) {
        this.recordId = recordId;
        this.vehicleId = vehicleId;
        this.vehicleModel = vehicleModel;
        this.type = type;
        this.scheduledDate = scheduledDate;
        this.description = description;
        this.status = status;
        this.cost = 0.0;
        this.technician = "";
        this.notes = "";
        this.mileage = 0;
        this.partsUsed = "";
    }

    // Full constructor
    public MaintenanceRecord(String recordId, String vehicleId, String vehicleModel,
                           MaintenanceType type, LocalDate scheduledDate, String description,
                           MaintenanceStatus status, double cost, String technician, String notes) {
        this(recordId, vehicleId, vehicleModel, type, scheduledDate, description, status);
        this.cost = cost;
        this.technician = technician;
        this.notes = notes;
    }

    public void printRecord() {
        System.out.println("\n================ MAINTENANCE RECORD ================");
        System.out.println("Record ID: " + recordId);
        System.out.println("Vehicle: " + vehicleModel + " (" + vehicleId + ")");
        System.out.println("Type: " + type);
        System.out.println("Status: " + status);
        System.out.println("Scheduled Date: " + scheduledDate);
        
        if (actualStartDate != null) {
            System.out.println("Start Date: " + actualStartDate);
        }
        
        if (actualCompletionDate != null) {
            System.out.println("Completion Date: " + actualCompletionDate);
        }
        
        System.out.println("Description: " + description);
        
        if (cost > 0) {
            System.out.println("Cost: RM " + String.format("%.2f", cost));
        }
        
        if (!technician.isEmpty()) {
            System.out.println("Technician: " + technician);
        }
        
        if (mileage > 0) {
            System.out.println("Vehicle Mileage: " + mileage + " km");
        }
        
        if (!partsUsed.isEmpty()) {
            System.out.println("Parts Used: " + partsUsed);
        }
        
        if (!notes.isEmpty()) {
            System.out.println("Notes: " + notes);
        }
        
        System.out.println("====================================================\n");
    }

    public void printSummary() {
        String costStr = cost > 0 ? "RM " + String.format("%.2f", cost) : "N/A";
        System.out.println(recordId + " | " + vehicleId + " | " + type + 
                          " | " + scheduledDate + " | " + status + " | " + costStr);
    }

    public boolean isOverdue() {
        return status == MaintenanceStatus.SCHEDULED && 
               scheduledDate.isBefore(LocalDate.now());
    }

    public boolean isCompleted() {
        return status == MaintenanceStatus.COMPLETED;
    }

    public boolean isInProgress() {
        return status == MaintenanceStatus.IN_PROGRESS;
    }

    public long getDaysUntilDue() {
        if (status != MaintenanceStatus.SCHEDULED) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), scheduledDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(scheduledDate, LocalDate.now());
    }

    public int getDurationDays() {
        if (actualStartDate == null || actualCompletionDate == null) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(actualStartDate, actualCompletionDate);
    }

    public String getPriorityLevel() {
        switch (type) {
            case EMERGENCY_REPAIR:
                return "CRITICAL";
            case BRAKE_INSPECTION:
            case ENGINE_SERVICE:
            case TRANSMISSION_SERVICE:
                return "HIGH";
            case OIL_CHANGE:
            case TIRE_ROTATION:
            case BATTERY_CHECK:
                return "MEDIUM";
            case AIR_FILTER_REPLACEMENT:
            case DEEP_CLEANING:
            case GENERAL_INSPECTION:
                return "LOW";
            default:
                return "MEDIUM";
        }
    }

    public double getCostPerDay() {
        int duration = getDurationDays();
        return duration > 0 ? cost / duration : cost;
    }

    // Getters
    public String getRecordId() { return recordId; }
    public String getVehicleId() { return vehicleId; }
    public String getVehicleModel() { return vehicleModel; }
    public MaintenanceType getType() { return type; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public LocalDate getActualStartDate() { return actualStartDate; }
    public LocalDate getActualCompletionDate() { return actualCompletionDate; }
    public MaintenanceStatus getStatus() { return status; }
    public String getDescription() { return description; }
    public double getCost() { return cost; }
    public String getTechnician() { return technician; }
    public String getNotes() { return notes; }
    public int getMileage() { return mileage; }
    public String getPartsUsed() { return partsUsed; }

    // Setters
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public void setType(MaintenanceType type) { this.type = type; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    public void setActualStartDate(LocalDate actualStartDate) { this.actualStartDate = actualStartDate; }
    public void setActualCompletionDate(LocalDate actualCompletionDate) { this.actualCompletionDate = actualCompletionDate; }
    public void setStatus(MaintenanceStatus status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setCost(double cost) { this.cost = cost; }
    public void setTechnician(String technician) { this.technician = technician; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setMileage(int mileage) { this.mileage = mileage; }
    public void setPartsUsed(String partsUsed) { this.partsUsed = partsUsed; }

    @Override
    public String toString() {
        return "MaintenanceRecord{" +
                "recordId='" + recordId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", type=" + type +
                ", scheduledDate=" + scheduledDate +
                ", status=" + status +
                ", cost=" + cost +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MaintenanceRecord that = (MaintenanceRecord) obj;
        return recordId != null ? recordId.equals(that.recordId) : that.recordId == null;
    }

    @Override
    public int hashCode() {
        return recordId != null ? recordId.hashCode() : 0;
    }
}
