package com.rentalapp.maintenance;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MaintenanceManager {
    private List<MaintenanceRecord> maintenanceRecords;
    private Map<String, List<MaintenanceRecord>> vehicleMaintenanceMap;
    private int recordIdCounter;

    public MaintenanceManager() {
        this.maintenanceRecords = new ArrayList<>();
        this.vehicleMaintenanceMap = new HashMap<>();
        this.recordIdCounter = 1000;
    }

    public MaintenanceRecord scheduleMaintenance(String vehicleId, String vehicleModel, 
                                               MaintenanceType type, LocalDate scheduledDate, 
                                               String description) {
        String recordId = "MR" + (++recordIdCounter);
        
        MaintenanceRecord record = new MaintenanceRecord(
            recordId, vehicleId, vehicleModel, type, scheduledDate, 
            description, MaintenanceStatus.SCHEDULED
        );

        maintenanceRecords.add(record);
        vehicleMaintenanceMap.computeIfAbsent(vehicleId, k -> new ArrayList<>()).add(record);

        System.out.println("Maintenance scheduled successfully!");
        System.out.println("Record ID: " + recordId);
        System.out.println("Vehicle: " + vehicleModel + " (" + vehicleId + ")");
        System.out.println("Type: " + type);
        System.out.println("Scheduled Date: " + scheduledDate);

        return record;
    }

    public boolean startMaintenance(String recordId) {
        MaintenanceRecord record = findRecord(recordId);
        if (record == null) {
            System.out.println("Maintenance record not found: " + recordId);
            return false;
        }

        if (record.getStatus() != MaintenanceStatus.SCHEDULED) {
            System.out.println("Maintenance cannot be started. Current status: " + record.getStatus());
            return false;
        }

        record.setStatus(MaintenanceStatus.IN_PROGRESS);
        record.setActualStartDate(LocalDate.now());

        System.out.println("Maintenance started for record: " + recordId);
        return true;
    }

    public boolean completeMaintenance(String recordId, double cost, String technician, String notes) {
        MaintenanceRecord record = findRecord(recordId);
        if (record == null) {
            System.out.println("Maintenance record not found: " + recordId);
            return false;
        }

        if (record.getStatus() != MaintenanceStatus.IN_PROGRESS) {
            System.out.println("Maintenance is not in progress. Current status: " + record.getStatus());
            return false;
        }

        record.setStatus(MaintenanceStatus.COMPLETED);
        record.setActualCompletionDate(LocalDate.now());
        record.setCost(cost);
        record.setTechnician(technician);
        record.setNotes(notes);

        // Schedule next maintenance based on type
        scheduleNextMaintenance(record);

        System.out.println("Maintenance completed successfully!");
        System.out.println("Record ID: " + recordId);
        System.out.println("Cost: RM " + String.format("%.2f", cost));
        System.out.println("Technician: " + technician);

        return true;
    }

    public List<MaintenanceRecord> getVehicleMaintenance(String vehicleId) {
        return vehicleMaintenanceMap.getOrDefault(vehicleId, new ArrayList<>());
    }

    public List<MaintenanceRecord> getMaintenanceByStatus(MaintenanceStatus status) {
        return maintenanceRecords.stream()
                .filter(record -> record.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<MaintenanceRecord> getOverdueMaintenance() {
        LocalDate today = LocalDate.now();
        return maintenanceRecords.stream()
                .filter(record -> record.getStatus() == MaintenanceStatus.SCHEDULED && 
                                record.getScheduledDate().isBefore(today))
                .collect(Collectors.toList());
    }

    public List<MaintenanceRecord> getUpcomingMaintenance(int days) {
        LocalDate cutoffDate = LocalDate.now().plusDays(days);
        return maintenanceRecords.stream()
                .filter(record -> record.getStatus() == MaintenanceStatus.SCHEDULED &&
                                !record.getScheduledDate().isAfter(cutoffDate) &&
                                !record.getScheduledDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(MaintenanceRecord::getScheduledDate))
                .collect(Collectors.toList());
    }

    public double getMaintenanceCostByVehicle(String vehicleId) {
        return getVehicleMaintenance(vehicleId).stream()
                .filter(record -> record.getStatus() == MaintenanceStatus.COMPLETED)
                .mapToDouble(MaintenanceRecord::getCost)
                .sum();
    }

    public double getTotalMaintenanceCost() {
        return maintenanceRecords.stream()
                .filter(record -> record.getStatus() == MaintenanceStatus.COMPLETED)
                .mapToDouble(MaintenanceRecord::getCost)
                .sum();
    }

    public void displayVehicleMaintenanceHistory(String vehicleId) {
        List<MaintenanceRecord> vehicleRecords = getVehicleMaintenance(vehicleId);
        
        if (vehicleRecords.isEmpty()) {
            System.out.println("No maintenance records found for vehicle: " + vehicleId);
            return;
        }

        System.out.println("\n================ MAINTENANCE HISTORY ================");
        System.out.println("Vehicle ID: " + vehicleId);
        System.out.println("Total Maintenance Records: " + vehicleRecords.size());
        System.out.println("Total Cost: RM " + String.format("%.2f", getMaintenanceCostByVehicle(vehicleId)));
        System.out.println("-".repeat(55));

        vehicleRecords.stream()
                .sorted(Comparator.comparing(MaintenanceRecord::getScheduledDate).reversed())
                .forEach(record -> {
                    System.out.println("Record ID: " + record.getRecordId());
                    System.out.println("Type: " + record.getType());
                    System.out.println("Scheduled: " + record.getScheduledDate());
                    System.out.println("Status: " + record.getStatus());
                    if (record.getStatus() == MaintenanceStatus.COMPLETED) {
                        System.out.println("Completed: " + record.getActualCompletionDate());
                        System.out.println("Cost: RM " + String.format("%.2f", record.getCost()));
                        System.out.println("Technician: " + record.getTechnician());
                    }
                    System.out.println("Description: " + record.getDescription());
                    System.out.println("-".repeat(40));
                });

        System.out.println("=====================================================\n");
    }

    public void displayUpcomingMaintenance() {
        List<MaintenanceRecord> upcoming = getUpcomingMaintenance(30); // Next 30 days
        
        if (upcoming.isEmpty()) {
            System.out.println("No upcoming maintenance in the next 30 days.");
            return;
        }

        System.out.println("\n================ UPCOMING MAINTENANCE ================");
        System.out.printf("%-12s %-15s %-15s %-12s %-20s%n", 
                         "Record ID", "Vehicle ID", "Type", "Date", "Description");
        System.out.println("-".repeat(75));

        for (MaintenanceRecord record : upcoming) {
            System.out.printf("%-12s %-15s %-15s %-12s %-20s%n",
                             record.getRecordId(),
                             record.getVehicleId(),
                             record.getType(),
                             record.getScheduledDate(),
                             record.getDescription().length() > 20 ? 
                                 record.getDescription().substring(0, 17) + "..." : 
                                 record.getDescription());
        }
        System.out.println("======================================================\n");
    }

    public void displayOverdueMaintenance() {
        List<MaintenanceRecord> overdue = getOverdueMaintenance();
        
        if (overdue.isEmpty()) {
            System.out.println("No overdue maintenance found.");
            return;
        }

        System.out.println("\n================ OVERDUE MAINTENANCE ================");
        System.out.printf("%-12s %-15s %-15s %-12s %-10s%n", 
                         "Record ID", "Vehicle ID", "Type", "Due Date", "Days Late");
        System.out.println("-".repeat(70));

        LocalDate today = LocalDate.now();
        for (MaintenanceRecord record : overdue) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(record.getScheduledDate(), today);
            System.out.printf("%-12s %-15s %-15s %-12s %-10d%n",
                             record.getRecordId(),
                             record.getVehicleId(),
                             record.getType(),
                             record.getScheduledDate(),
                             daysLate);
        }
        System.out.println("=====================================================\n");
    }

    public Map<MaintenanceType, Integer> getMaintenanceTypeStatistics() {
        Map<MaintenanceType, Integer> stats = new HashMap<>();
        for (MaintenanceRecord record : maintenanceRecords) {
            stats.put(record.getType(), stats.getOrDefault(record.getType(), 0) + 1);
        }
        return stats;
    }

    public void generateMaintenanceReport() {
        System.out.println("\n================ MAINTENANCE REPORT ================");
        System.out.println("Total Maintenance Records: " + maintenanceRecords.size());
        System.out.println("Total Maintenance Cost: RM " + String.format("%.2f", getTotalMaintenanceCost()));
        
        // Status breakdown
        Map<MaintenanceStatus, Long> statusCount = maintenanceRecords.stream()
                .collect(Collectors.groupingBy(MaintenanceRecord::getStatus, Collectors.counting()));
        
        System.out.println("\nStatus Breakdown:");
        statusCount.forEach((status, count) -> 
            System.out.println("- " + status + ": " + count + " records"));
        
        // Type breakdown
        Map<MaintenanceType, Integer> typeStats = getMaintenanceTypeStatistics();
        System.out.println("\nMaintenance Type Breakdown:");
        typeStats.forEach((type, count) -> 
            System.out.println("- " + type + ": " + count + " records"));
        
        // Overdue and upcoming
        System.out.println("\nOverdue Maintenance: " + getOverdueMaintenance().size());
        System.out.println("Upcoming Maintenance (30 days): " + getUpcomingMaintenance(30).size());
        
        // Average cost by type
        System.out.println("\nAverage Cost by Type:");
        for (MaintenanceType type : MaintenanceType.values()) {
            double avgCost = maintenanceRecords.stream()
                    .filter(r -> r.getType() == type && r.getStatus() == MaintenanceStatus.COMPLETED)
                    .mapToDouble(MaintenanceRecord::getCost)
                    .average()
                    .orElse(0.0);
            if (avgCost > 0) {
                System.out.println("- " + type + ": RM " + String.format("%.2f", avgCost));
            }
        }
        
        System.out.println("====================================================\n");
    }

    public boolean rescheduleMaintenance(String recordId, LocalDate newDate) {
        MaintenanceRecord record = findRecord(recordId);
        if (record == null) {
            System.out.println("Maintenance record not found: " + recordId);
            return false;
        }

        if (record.getStatus() != MaintenanceStatus.SCHEDULED) {
            System.out.println("Only scheduled maintenance can be rescheduled. Current status: " + record.getStatus());
            return false;
        }

        LocalDate oldDate = record.getScheduledDate();
        record.setScheduledDate(newDate);

        System.out.println("Maintenance rescheduled successfully!");
        System.out.println("Record ID: " + recordId);
        System.out.println("Old Date: " + oldDate);
        System.out.println("New Date: " + newDate);

        return true;
    }

    public boolean cancelMaintenance(String recordId, String reason) {
        MaintenanceRecord record = findRecord(recordId);
        if (record == null) {
            System.out.println("Maintenance record not found: " + recordId);
            return false;
        }

        if (record.getStatus() == MaintenanceStatus.COMPLETED) {
            System.out.println("Cannot cancel completed maintenance.");
            return false;
        }

        record.setStatus(MaintenanceStatus.CANCELLED);
        record.setNotes("Cancelled: " + reason);

        System.out.println("Maintenance cancelled successfully!");
        System.out.println("Record ID: " + recordId);
        System.out.println("Reason: " + reason);

        return true;
    }

    private void scheduleNextMaintenance(MaintenanceRecord completedRecord) {
        // Schedule next maintenance based on type
        LocalDate nextDate = calculateNextMaintenanceDate(completedRecord.getType());
        
        if (nextDate != null) {
            String description = "Regular " + completedRecord.getType() + " maintenance";
            scheduleMaintenance(completedRecord.getVehicleId(), completedRecord.getVehicleModel(),
                              completedRecord.getType(), nextDate, description);
        }
    }

    private LocalDate calculateNextMaintenanceDate(MaintenanceType type) {
        LocalDate today = LocalDate.now();
        
        switch (type) {
            case OIL_CHANGE:
                return today.plusMonths(3); // Every 3 months
            case TIRE_ROTATION:
                return today.plusMonths(6); // Every 6 months
            case BRAKE_INSPECTION:
                return today.plusMonths(6); // Every 6 months
            case ENGINE_SERVICE:
                return today.plusYears(1); // Every year
            case TRANSMISSION_SERVICE:
                return today.plusYears(2); // Every 2 years
            case AIR_FILTER_REPLACEMENT:
                return today.plusMonths(6); // Every 6 months
            case BATTERY_CHECK:
                return today.plusMonths(3); // Every 3 months
            case GENERAL_INSPECTION:
                return today.plusMonths(6); // Every 6 months
            case EMERGENCY_REPAIR:
                return null; // No scheduled follow-up for emergency repairs
            case DEEP_CLEANING:
                return today.plusMonths(3); // Every 3 months
            default:
                return today.plusMonths(6); // Default 6 months
        }
    }

    private MaintenanceRecord findRecord(String recordId) {
        return maintenanceRecords.stream()
                .filter(record -> record.getRecordId().equals(recordId))
                .findFirst()
                .orElse(null);
    }

    public List<MaintenanceRecord> searchMaintenanceRecords(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return maintenanceRecords.stream()
                .filter(record -> 
                    record.getVehicleId().toLowerCase().contains(lowerKeyword) ||
                    record.getVehicleModel().toLowerCase().contains(lowerKeyword) ||
                    record.getDescription().toLowerCase().contains(lowerKeyword) ||
                    record.getTechnician().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public List<MaintenanceRecord> getMaintenanceByDateRange(LocalDate startDate, LocalDate endDate) {
        return maintenanceRecords.stream()
                .filter(record -> !record.getScheduledDate().isBefore(startDate) && 
                                !record.getScheduledDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public void bulkRescheduleMaintenance(List<String> recordIds, int daysToAdd) {
        for (String recordId : recordIds) {
            MaintenanceRecord record = findRecord(recordId);
            if (record != null && record.getStatus() == MaintenanceStatus.SCHEDULED) {
                LocalDate newDate = record.getScheduledDate().plusDays(daysToAdd);
                record.setScheduledDate(newDate);
                System.out.println("Rescheduled " + recordId + " to " + newDate);
            }
        }
    }

    public Map<String, Double> getVehicleMaintenanceCostSummary() {
        Map<String, Double> costByVehicle = new HashMap<>();
        for (MaintenanceRecord record : maintenanceRecords) {
            if (record.getStatus() == MaintenanceStatus.COMPLETED) {
                costByVehicle.put(record.getVehicleId(), 
                    costByVehicle.getOrDefault(record.getVehicleId(), 0.0) + record.getCost());
            }
        }
        return costByVehicle;
    }

    public List<String> getHighMaintenanceVehicles(double threshold) {
        Map<String, Double> costByVehicle = getVehicleMaintenanceCostSummary();
        return costByVehicle.entrySet().stream()
                .filter(entry -> entry.getValue() > threshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Getters for external access
    public List<MaintenanceRecord> getAllMaintenanceRecords() {
        return new ArrayList<>(maintenanceRecords);
    }

    public int getTotalRecordsCount() {
        return maintenanceRecords.size();
    }

    public int getCompletedRecordsCount() {
        return (int) maintenanceRecords.stream()
                .filter(record -> record.getStatus() == MaintenanceStatus.COMPLETED)
                .count();
    }
}
