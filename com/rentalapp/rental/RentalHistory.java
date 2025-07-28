package com.rentalapp.rental;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RentalHistory {
    private List<RentalHistoryRecord> historyRecords;
    private Map<String, List<RentalHistoryRecord>> customerHistoryMap;

    public RentalHistory() {
        this.historyRecords = new ArrayList<>();
        this.customerHistoryMap = new HashMap<>();
    }

     public void addRentalRecord(RentalHistoryRecord record) {
        historyRecords.add(record);
        
        // Add to customer-specific history
        customerHistoryMap.computeIfAbsent(record.getCustomerId(), k -> new ArrayList<>())
                         .add(record);
    }

    public List<RentalHistoryRecord> getCustomerHistory(String customerId) {
        return customerHistoryMap.getOrDefault(customerId, new ArrayList<>());
    }

    public List<RentalHistoryRecord> getCustomerHistoryByDateRange(String customerId, 
                                                                   LocalDate startDate, 
                                                                   LocalDate endDate) {
        List<RentalHistoryRecord> customerRecords = getCustomerHistory(customerId);
        
        return customerRecords.stream()
                .filter(record -> !record.getRentalDate().isBefore(startDate) && 
                                !record.getRentalDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public List<RentalHistoryRecord> getAllHistory() {
        return new ArrayList<>(historyRecords);
}
     public List<RentalHistoryRecord> getHistoryByVehicleModel(String vehicleModel) {
        return historyRecords.stream()
                .filter(record -> record.getVehicleModel().equalsIgnoreCase(vehicleModel))
                .collect(Collectors.toList());
    }

    public List<RentalHistoryRecord> getHistoryByStatus(String status) {
        return historyRecords.stream()
                .filter(record -> record.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public int getCustomerRentalCount(String customerId) {
        return getCustomerHistory(customerId).size();
    }

    public double getCustomerTotalSpent(String customerId) {
        return getCustomerHistory(customerId).stream()
                .mapToDouble(RentalHistoryRecord::getTotalAmount)
                .sum();
    }

    public int getCustomerTotalLoyaltyPoints(String customerId) {
        return getCustomerHistory(customerId).stream()
                .mapToInt(RentalHistoryRecord::getLoyaltyPointsEarned)
                .sum();
    }

    public void displayCustomerHistory(String customerId) {
        List<RentalHistoryRecord> customerRecords = getCustomerHistory(customerId);
        
        if (customerRecords.isEmpty()) {
            System.out.println("No rental history found for customer: " + customerId);
            return;
        }

        System.out.println("\n==================== RENTAL HISTORY ====================");
        System.out.println("Customer ID: " + customerId);
        System.out.println("Total Rentals: " + customerRecords.size());
        System.out.println("Total Spent: RM " + String.format("%.2f", getCustomerTotalSpent(customerId)));
        System.out.println("Total Loyalty Points: " + getCustomerTotalLoyaltyPoints(customerId));
        System.out.println("-".repeat(60));

        for (RentalHistoryRecord record : customerRecords) {
            System.out.println("Rental ID: " + record.getRentalId());
            System.out.println("Vehicle: " + record.getVehicleModel());
            System.out.println("Date: " + record.getRentalDate());
            System.out.println("Days: " + record.getRentalDays());
            System.out.println("Amount: RM " + String.format("%.2f", record.getTotalAmount()));
            System.out.println("Status: " + record.getStatus());
            System.out.println("Points Earned: " + record.getLoyaltyPointsEarned());
            System.out.println("-".repeat(40));
        }
        System.out.println("========================================================\n");
    }

    public void displayRecentHistory(int limit) {
        List<RentalHistoryRecord> recentRecords = historyRecords.stream()
                .sorted((r1, r2) -> r2.getRentalDate().compareTo(r1.getRentalDate()))
                .limit(limit)
                .collect(Collectors.toList());

        System.out.println("\n================ RECENT RENTAL HISTORY ================");
        for (RentalHistoryRecord record : recentRecords) {
            System.out.println(record.getRentalId() + " | " + 
                             record.getCustomerName() + " | " + 
                             record.getVehicleModel() + " | " + 
                             record.getRentalDate() + " | " + 
                             "RM " + String.format("%.2f", record.getTotalAmount()));
        }
        System.out.println("========================================================\n");
    }

    public Map<String, Integer> getVehicleRentalStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (RentalHistoryRecord record : historyRecords) {
            stats.put(record.getVehicleModel(), 
                     stats.getOrDefault(record.getVehicleModel(), 0) + 1);
        }
        return stats;
    }

    public Map<String, Double> getMonthlyRevenue() {
        Map<String, Double> monthlyRevenue = new HashMap<>();
        for (RentalHistoryRecord record : historyRecords) {
            String monthKey = record.getRentalDate().getYear() + "-" + 
                            String.format("%02d", record.getRentalDate().getMonthValue());
            monthlyRevenue.put(monthKey, 
                             monthlyRevenue.getOrDefault(monthKey, 0.0) + record.getTotalAmount());
        }
        return monthlyRevenue;
    }

    public List<RentalHistoryRecord> searchHistory(String searchTerm) {
        return historyRecords.stream()
                .filter(record -> 
                    record.getCustomerName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    record.getVehicleModel().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    record.getRentalId().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void generateHistoryReport() {
        System.out.println("\n==================== HISTORY REPORT ====================");
        System.out.println("Total Records: " + historyRecords.size());
        System.out.println("Total Revenue: RM " + String.format("%.2f", 
            historyRecords.stream().mapToDouble(RentalHistoryRecord::getTotalAmount).sum()));
        
        // Status breakdown
        Map<String, Long> statusCount = historyRecords.stream()
                .collect(Collectors.groupingBy(RentalHistoryRecord::getStatus, Collectors.counting()));
        
        System.out.println("\nStatus Breakdown:");
        statusCount.forEach((status, count) -> 
            System.out.println("- " + status + ": " + count + " rentals"));
        
        // Top vehicles
        Map<String, Integer> vehicleStats = getVehicleRentalStats();
        System.out.println("\nTop 5 Rented Vehicles:");
        vehicleStats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> 
                    System.out.println("- " + entry.getKey() + ": " + entry.getValue() + " rentals"));
        
        System.out.println("========================================================\n");
    }

    public boolean removeRecord(String rentalId) {
        boolean removed = historyRecords.removeIf(record -> record.getRentalId().equals(rentalId));
        
        if (removed) {
            // Also remove from customer-specific map
            customerHistoryMap.values().forEach(list -> 
                list.removeIf(record -> record.getRentalId().equals(rentalId)));
        }
        
        return removed;
    }

    public void clearCustomerHistory(String customerId) {
        historyRecords.removeIf(record -> record.getCustomerId().equals(customerId));
        customerHistoryMap.remove(customerId);
    }

    public int getTotalRecordCount() {
        return historyRecords.size();
    }
}
