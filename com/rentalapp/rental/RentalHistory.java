package com.rentalapp.rental;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.time.Duration;

public class RentalHistory {
    private List<RentalHistoryRecord> historyRecords;
    private Map<String, List<RentalHistoryRecord>> customerHistoryMap;
    

    public RentalHistory() {
        this.historyRecords = new ArrayList<>();
        this.customerHistoryMap = new HashMap<>();
    }

    public void addRentalRecord(RentalHistoryRecord record) {
        historyRecords.add(record);
        customerHistoryMap.computeIfAbsent(record.getCustomerId(), k -> new ArrayList<>()).add(record);
    }

    public boolean updateStatus(String rentalId, String newStatus) {
    for (RentalHistoryRecord record : historyRecords) {
        if (record.getRentalId().equals(rentalId)) {
            record.setStatus(newStatus);
            return true;
        }
    }
    return false;
}


    public List<RentalHistoryRecord> getCustomerHistory(String customerId) {
        return customerHistoryMap.getOrDefault(customerId, new ArrayList<>());
    }

    public List<RentalHistoryRecord> getCustomerHistoryByDateRange(String customerId,
                                                                   LocalDateTime start,
                                                                   LocalDateTime end) {
        return getCustomerHistory(customerId).stream()
                .filter(record -> !record.getScheduledStart().isBefore(start) &&
                                  !record.getScheduledEnd().isAfter(end))
                .collect(Collectors.toList());
    }

    public List<RentalHistoryRecord> getAllHistory() {
        return new ArrayList<>(historyRecords);
    }

    public List<RentalHistoryRecord> getHistoryByVesselType(String vesselType) {
        return historyRecords.stream()
                .filter(record -> record.getVesselType().equalsIgnoreCase(vesselType))
                .collect(Collectors.toList());
    }

    public List<RentalHistoryRecord> getHistoryByStatus(String status) {
        return historyRecords.stream()
                .filter(record -> record.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
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

        // FIXED: Display ALL rentals including cancelled ones
        for (RentalHistoryRecord record : customerRecords) {
            System.out.println("Rental ID: " + record.getRentalId());
            System.out.println("Vessel: " + record.getVesselId() + " - " + record.getVesselModel() + " (" + record.getVesselType() + ")");
            System.out.println("Location: " + record.getLocation());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            System.out.println("Start: " + record.getScheduledStart().format(formatter));
            System.out.println("End: " + record.getScheduledEnd().format(formatter));
            
            if (record.getActualEnd() != null) {
                System.out.println("Actual End: " + record.getActualEnd().format(formatter));
            } else {
                System.out.println("Actual End: N/A");
            }

            if (record.getDuration() != null && !record.getStatus().equalsIgnoreCase("CANCELLED")) {
                Duration d = record.getDuration();
                long hours = d.toHours();
                long minutes = d.toMinutesPart();
                System.out.println("Duration: " + hours + "h " + minutes + "m");
            } else {
                System.out.println("Duration: 0h 0m");
            }

            System.out.println("Amount: RM " + String.format("%.2f", record.getTotalAmount()));
            System.out.println("Payment Method: " + record.getPaymentMethod());
            System.out.println("Status: " + record.getStatus());
            System.out.println("Points Earned: " + record.getLoyaltyPointsEarned());
            System.out.println("-".repeat(40));
        }
        System.out.println("========================================================\n");
    }


    public double getCustomerTotalSpent(String customerId) {
    return getCustomerHistory(customerId).stream()
            .filter(record -> !record.getStatus().equalsIgnoreCase("CANCELLED")) // ✅ exclude cancelled
            .mapToDouble(RentalHistoryRecord::getTotalAmount)
            .sum();
}

    public int getCustomerTotalLoyaltyPoints(String customerId) {
        return getCustomerHistory(customerId).stream()
                .mapToInt(RentalHistoryRecord::getLoyaltyPointsEarned)
                .sum();
    }


    public void generateHistoryReport(LocalDateTime start, LocalDateTime end) {
        List<RentalHistoryRecord> filteredRecords = historyRecords.stream()
                .filter(record -> !record.getScheduledStart().isBefore(start) &&
                                  !record.getScheduledEnd().isAfter(end))
                .collect(Collectors.toList());

        System.out.println("\n============= RENTAL HISTORY REPORT =============");
        System.out.println("Period: " + start + " to " + end);
        System.out.println("Total Rentals: " + filteredRecords.size());
        System.out.println("Total Revenue: RM " +
                String.format("%.2f", filteredRecords.stream().mapToDouble(RentalHistoryRecord::getTotalAmount).sum()));
        System.out.println("-".repeat(50));

        for (RentalHistoryRecord record : filteredRecords) {
            System.out.println(record.getRentalId() + " | " +
                               record.getCustomerName() + " | " +
                               record.getVesselModel() + " | " +
                               record.getScheduledStart() + " → " + record.getScheduledEnd() +
                               " | RM " + String.format("%.2f", record.getTotalAmount()) +
                               " | " + record.getPaymentMethod()); 
        }
        System.out.println("=================================================\n");
    }

    public List<RentalHistoryRecord> searchHistory(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return historyRecords.stream()
                .filter(record -> record.getRentalId().toLowerCase().contains(lowerKeyword) ||
                                  record.getCustomerName().toLowerCase().contains(lowerKeyword) ||
                                  record.getVesselModel().toLowerCase().contains(lowerKeyword) ||
                                  record.getVesselType().toLowerCase().contains(lowerKeyword) ||
                                  record.getStatus().toLowerCase().contains(lowerKeyword) ||
                                  record.getPaymentMethod().toLowerCase().contains(lowerKeyword)) 
                .collect(Collectors.toList());
    }
}
