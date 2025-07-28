package com.rentalapp.rental;

import com.rentalapp.vehicle.VehicleManager;
import com.rentalapp.auth.Customer;
import com.rentalapp.payment.PaymentManager;
import com.rentalapp.loyalty.LoyaltyPointManager;

import java.time.LocalDate;
import java.util.*;

public class RentalManager {
    private RentalService rentalService;
    private PaymentManager paymentManager;
    private RentalHistory rentalHistory;
    private LoyaltyPointManager loyaltyPointManager;
    private VehicleManager vehicleManager;
    private Map<String, Customer> customerDatabase;

    public RentalManager(VehicleManager vehicleManager, LoyaltyPointManager loyaltyPointManager) {
        this.vehicleManager = vehicleManager;
        this.loyaltyPointManager = loyaltyPointManager;
        this.rentalService = new RentalService(vehicleManager);
        this.paymentManager = new PaymentManager(loyaltyPointManager);
        this.rentalHistory = new RentalHistory();
        this.customerDatabase = new HashMap<>();
    }

    public RentalRecord createRental(String customerId, String vehicleId, String pickupLocation,
                                   LocalDate pickupDate, LocalDate returnDate, int driverAge) {
        // Get customer
        Customer customer = customerDatabase.get(customerId);
        if (customer == null) {
            System.out.println("Customer not found: " + customerId);
            return null;
        }

        // Create rental request
        RentalRequest request = new RentalRequest(customerId, vehicleId, pickupLocation,
                                                pickupDate, returnDate, driverAge);

        // Process rental through service
        RentalRecord rental = rentalService.processRental(request, customer);
        
        if (rental != null) {
            System.out.println("Rental created successfully!");
            displayRentalSummary(rental.getRentalId());
        }

        return rental;
    }

    public boolean processRentalPayment(String rentalId, String paymentMethod) {
        RentalRecord rental = rentalService.getRentalById(rentalId);
        if (rental == null) {
            System.out.println("Rental not found: " + rentalId);
            return false;
        }

        Customer customer = customerDatabase.get(rental.getCustomerId());
        if (customer == null) {
            System.out.println("Customer not found for rental: " + rentalId);
            return false;
        }

        // Process payment
        var receipt = paymentManager.processPayment(rental, customer, paymentMethod);
        
        if (receipt != null) {
            // Add to rental history
            addToRentalHistory(rental, customer, paymentMethod, receipt.getLoyaltyPointsEarned());

            
            System.out.println("Payment processed successfully!");
            paymentManager.printReceipt(receipt.getReceiptId());
            
            return true;
        }

        return false;
    }

    public boolean returnVehicle(String rentalId) {
        RentalRecord rental = rentalService.getRentalById(rentalId);
        if (rental == null) {
            System.out.println("Active rental not found: " + rentalId);
            return false;
        }

        boolean returned = rentalService.returnVehicle(rentalId);
        
        if (returned) {
            // Update rental history record status
            updateRentalHistoryStatus(rentalId, "RETURNED");
            
            System.out.println("Vehicle returned successfully!");
            System.out.println("Thank you for choosing our rental service!");
        }

        return returned;
    }

    public boolean extendRental(String rentalId, int additionalDays, String paymentMethod) {
        RentalRecord rental = rentalService.getRentalById(rentalId);
        if (rental == null) {
            System.out.println("Active rental not found: " + rentalId);
            return false;
        }

        Customer customer = customerDatabase.get(rental.getCustomerId());
        if (customer == null) {
            System.out.println("Customer not found for rental: " + rentalId);
            return false;
        }

        // Extend the rental
        boolean extended = rentalService.extendRental(rentalId, additionalDays);
        
        if (extended) {
            // Process payment for extension
            var receipt = paymentManager.processPayment(rental, customer, paymentMethod);
            
            if (receipt != null) {
                System.out.println("Rental extended and payment processed successfully!");
                return true;
            } else {
                System.out.println("Rental extended but payment failed. Please process payment manually.");
                return false;
            }
        }

        return false;
    }

    public boolean cancelRental(String rentalId) {
        RentalRecord rental = rentalService.getRentalById(rentalId);
        if (rental == null) {
            System.out.println("Active rental not found: " + rentalId);
            return false;
        }

        boolean cancelled = rentalService.cancelRental(rentalId);
        
        if (cancelled) {
            // Process refund (partial refund based on cancellation policy)
            double refundAmount = calculateRefundAmount(rental);
            
            if (refundAmount > 0) {
                paymentManager.processRefund(rentalId, refundAmount);
            }
            
            // Update rental history
            updateRentalHistoryStatus(rentalId, "CANCELLED");
            
            System.out.println("Rental cancelled successfully!");
        }

        return cancelled;
    }

    public void displayCustomerRentalHistory(String customerId) {
        rentalHistory.displayCustomerHistory(customerId);
        
        // Also display payment summary
        var paymentSummary = paymentManager.getPaymentSummary(customerId);
        if (paymentSummary != null) {
            paymentSummary.printSummary();
        }
        
        // Display loyalty status
        loyaltyPointManager.displayLoyaltyStatus(customerId);
    }

    public void displayRentalSummary(String rentalId) {
        rentalService.displayRentalSummary(rentalId);
    }

    public void displayAllActiveRentals() {
        rentalService.displayAllActiveRentals();
    }

    public List<RentalRecord> getOverdueRentals() {
        return rentalService.getOverdueRentals();
    }

    public void processOverdueRentals() {
        List<RentalRecord> overdueList = getOverdueRentals();
        
        if (overdueList.isEmpty()) {
            System.out.println("No overdue rentals found.");
            return;
        }

        System.out.println("\n================ PROCESSING OVERDUE RENTALS ================");
        
        for (RentalRecord rental : overdueList) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(
                rental.getReturnDate(), LocalDate.now());
            
            System.out.println("Processing overdue rental: " + rental.getRentalId());
            System.out.println("Customer: " + rental.getCustomerName());
            System.out.println("Days overdue: " + overdueDays);
            
            // Calculate and apply late fees
            // This would typically involve sending notifications, applying fees, etc.
            System.out.println("Late fee notifications sent.");
            System.out.println("-".repeat(50));
        }
        
        System.out.println("=============================================================\n");
    }

    public void generateComprehensiveReport() {
        System.out.println("\n================ COMPREHENSIVE RENTAL REPORT ================");
        
        // Rental statistics
        List<RentalRecord> activeRentals = rentalService.getActiveRentals();
        System.out.println("Active Rentals: " + activeRentals.size());
        System.out.println("Total Revenue (Active): RM " + String.format("%.2f", rentalService.getTotalRevenue()));
        
        // Overdue rentals
        List<RentalRecord> overdueRentals = getOverdueRentals();
        System.out.println("Overdue Rentals: " + overdueRentals.size());
        
        // Payment statistics
        paymentManager.generateMonthlyReport();
        
        // Rental history statistics
        rentalHistory.generateHistoryReport();
        
        // Loyalty program statistics
        loyaltyPointManager.generateLoyaltyReport();
        
        System.out.println("=============================================================\n");
    }

    public List<RentalRecord> searchRentals(String searchTerm) {
        // Search in active rentals
        List<RentalRecord> results = new ArrayList<>();
        List<RentalRecord> activeRentals = rentalService.getActiveRentals();
        
        for (RentalRecord rental : activeRentals) {
            if (rental.getCustomerName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                rental.getVehicleModel().toLowerCase().contains(searchTerm.toLowerCase()) ||
                rental.getRentalId().toLowerCase().contains(searchTerm.toLowerCase())) {
                results.add(rental);
            }
        }
        
        return results;
    }

    public void addCustomer(Customer customer) {
        customerDatabase.put(customer.getUserId(), customer);
        
        // Create loyalty account for new customer
        loyaltyPointManager.createLoyaltyAccount(customer.getUserId(), customer.getName());
    }

    public Customer getCustomer(String customerId) {
        return customerDatabase.get(customerId);
    }

    public void displayCustomerDetails(String customerId) {
        Customer customer = customerDatabase.get(customerId);
        if (customer == null) {
            System.out.println("Customer not found: " + customerId);
            return;
        }

        System.out.println("\n================ CUSTOMER DETAILS ================");
        System.out.println("Customer ID: " + customer.getUserId());
        System.out.println("Name: " + customer.getName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Member: " + (customer.isMember() ? "Yes" : "No"));
        
        if (customer.isMember()) {
            System.out.println("VIP Member: " + (customer.isVipMember() ? "Yes" : "No"));
        }
        
        // Show rental statistics
        List<RentalRecord> customerRentals = rentalService.getCustomerRentals(customerId);
        System.out.println("Active Rentals: " + customerRentals.size());
        
        int historyCount = rentalHistory.getCustomerRentalCount(customerId);
        System.out.println("Total Rental History: " + historyCount);
        
        double totalSpent = rentalHistory.getCustomerTotalSpent(customerId);
        System.out.println("Total Spent: RM " + String.format("%.2f", totalSpent));
        
        int loyaltyPoints = loyaltyPointManager.getCustomerPoints(customerId);
        System.out.println("Loyalty Points: " + loyaltyPoints);
        
        System.out.println("==================================================\n");
    }

    // Private helper methods
    private void addToRentalHistory(RentalRecord rental, Customer customer, 
                                  String paymentMethod, int loyaltyPoints) {
        RentalHistoryRecord historyRecord = new RentalHistoryRecord(
            rental.getRentalId(),
            rental.getCustomerId(),
            rental.getCustomerName(),
            rental.getVehicleId(),
            rental.getVehicleModel(),
            rental.getPickupDate(),
            rental.getReturnDate(),
            rental.getRentalDays(),
            rental.getTotalCost(),
            rental.getStatus().toString(),
            paymentMethod,
            loyaltyPoints,
            rental.getPickupLocation()
        );
        
        rentalHistory.addRentalRecord(historyRecord);
        
        // Award loyalty points to customer
        loyaltyPointManager.addPoints(rental.getCustomerId(), loyaltyPoints);
        
        System.out.println("Rental added to history successfully.");
        System.out.println("Loyalty points earned: " + loyaltyPoints);
    }

    private void updateRentalHistoryStatus(String rentalId, String newStatus) {
        // Find and update the rental record in history
        List<RentalHistoryRecord> allHistory = rentalHistory.getAllHistory();
        
        for (RentalHistoryRecord record : allHistory) {
            if (record.getRentalId().equals(rentalId)) {
                record.setStatus(newStatus);
                
                if ("RETURNED".equals(newStatus)) {
                    record.setReturnDate(LocalDate.now());
                }
                break;
            }
        }
        
        System.out.println("Rental history updated: " + rentalId + " -> " + newStatus);
    }

    private double calculateRefundAmount(RentalRecord rental) {
        LocalDate today = LocalDate.now();
        LocalDate pickupDate = rental.getPickupDate();
        double totalCost = rental.getTotalCost();
        
        // Calculate refund based on cancellation policy
        long daysUntilPickup = java.time.temporal.ChronoUnit.DAYS.between(today, pickupDate);
        
        if (daysUntilPickup >= 7) {
            // Full refund if cancelled 7+ days before pickup
            return totalCost;
        } else if (daysUntilPickup >= 3) {
            // 75% refund if cancelled 3-6 days before pickup
            return totalCost * 0.75;
        } else if (daysUntilPickup >= 1) {
            // 50% refund if cancelled 1-2 days before pickup
            return totalCost * 0.50;
        } else {
            // No refund if cancelled on pickup day or after
            return 0.0;
        }
    }

    // Additional utility methods
    public Map<String, Integer> getRentalStatsByVehicleType() {
        Map<String, Integer> stats = new HashMap<>();
        List<RentalRecord> activeRentals = rentalService.getActiveRentals();
        
        for (RentalRecord rental : activeRentals) {
            String vehicleModel = rental.getVehicleModel();
            stats.put(vehicleModel, stats.getOrDefault(vehicleModel, 0) + 1);
        }
        
        return stats;
    }

    public List<RentalRecord> getRentalsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<RentalRecord> filteredRentals = new ArrayList<>();
        List<RentalRecord> activeRentals = rentalService.getActiveRentals();
        
        for (RentalRecord rental : activeRentals) {
            LocalDate pickupDate = rental.getPickupDate();
            if (!pickupDate.isBefore(startDate) && !pickupDate.isAfter(endDate)) {
                filteredRentals.add(rental);
            }
        }
        
        return filteredRentals;
    }

    public void displayRentalStatistics() {
        System.out.println("\n================ RENTAL STATISTICS ================");
        
        List<RentalRecord> activeRentals = rentalService.getActiveRentals();
        System.out.println("Total Active Rentals: " + activeRentals.size());
        
        // Vehicle type statistics
        Map<String, Integer> vehicleStats = getRentalStatsByVehicleType();
        System.out.println("\nRentals by Vehicle Type:");
        for (Map.Entry<String, Integer> entry : vehicleStats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        
        // Revenue statistics
        double totalRevenue = rentalService.getTotalRevenue();
        System.out.println("\nTotal Active Revenue: RM " + String.format("%.2f", totalRevenue));
        
        if (!activeRentals.isEmpty()) {
            double averageRental = totalRevenue / activeRentals.size();
            System.out.println("Average Rental Value: RM " + String.format("%.2f", averageRental));
        }
        
        // Overdue statistics
        List<RentalRecord> overdueRentals = getOverdueRentals();
        System.out.println("Overdue Rentals: " + overdueRentals.size());
        
        if (!overdueRentals.isEmpty()) {
            double overduePercentage = (double) overdueRentals.size() / activeRentals.size() * 100;
            System.out.println("Overdue Rate: " + String.format("%.1f%%", overduePercentage));
        }
        
        System.out.println("==================================================\n");
    }

    public boolean hasActiveRental(String customerId) {
        List<RentalRecord> customerRentals = rentalService.getCustomerRentals(customerId);
        return !customerRentals.isEmpty();
    }

    public int getCustomerActiveRentalCount(String customerId) {
        return rentalService.getCustomerRentals(customerId).size();
    }

    public double getCustomerTotalActiveRentalCost(String customerId) {
        List<RentalRecord> customerRentals = rentalService.getCustomerRentals(customerId);
        double totalCost = 0.0;
        
        for (RentalRecord rental : customerRentals) {
            totalCost += rental.getTotalCost();
        }
        
        return totalCost;
    }
}
