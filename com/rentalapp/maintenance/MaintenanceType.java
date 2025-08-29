package com.rentalapp.maintenance;

public enum MaintenanceType {
    ENGINE_SERVICE("Engine Service"),
    ENGINE_OIL_CHANGE("Engine Oil Change"),
    HULL_INSPECTION("Hull Inspection"),
    PROPELLER_MAINTENANCE("Propeller Maintenance"),
    PROPELLER_SERVICE("Propeller Service"),
    TRANSMISSION_SERVICE("Transmission Service"),
    FUEL_SYSTEM_SERVICE("Fuel System Service"),
    FUEL_SYSTEM_CHECK("Fuel System Check"),
    ELECTRICAL_SYSTEM_CHECK("Electrical System Check"),
    BATTERY_CHECK("Battery Check"),
    SAFETY_EQUIPMENT_CHECK("Safety Equipment Check"),
    SAFETY_EQUIPMENT_INSPECTION("Safety Equipment Inspection"),
    EMERGENCY_REPAIR("Emergency Repair"),
    DEEP_CLEANING("Deep Cleaning"),
    BILGE_PUMP_CHECK("Bilge Pump Check"),
    NAVIGATION_SYSTEM_CHECK("Navigation System Check"),
    ANCHOR_AND_CHAIN_INSPECTION("Anchor and Chain Inspection"),
    RADIO_COMMUNICATION_CHECK("Radio Communication Check"),
    GENERAL_INSPECTION("General Inspection");

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