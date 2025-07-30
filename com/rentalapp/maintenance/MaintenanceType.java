package com.rentalapp.maintenance;

public enum MaintenanceType {
    OIL_CHANGE("Oil Change"),
    TIRE_ROTATION("Tire Rotation"),
    BRAKE_INSPECTION("Brake Inspection"),
    ENGINE_SERVICE("Engine Service"),
    TRANSMISSION_SERVICE("Transmission Service"),
    AIR_FILTER_REPLACEMENT("Air Filter Replacement"),
    BATTERY_CHECK("Battery Check"),
    GENERAL_INSPECTION("General Inspection"),
    EMERGENCY_REPAIR("Emergency Repair"),
    DEEP_CLEANING("Deep Cleaning");

    private final String displayName;

    MaintenanceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
