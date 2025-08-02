package com.rentalapp.rental;

import com.rentalapp.vehicle.*;
import com.rentalapp.auth.Customer;
import com.rentalapp.maintenance.MaintenanceManager;
import com.rentalapp.maintenance.MaintenanceType;
import com.rentalapp.payment.PaymentCalculator;

import java.time.LocalDate;
import java.util.*;

public class RentalService {
    private VehicleManager vehicleManager;
    private MaintenanceManager maintenanceManager;
    private List<RentalRecord> activeRentals;
    private int rentalIdCounter;
    
    public RentalService(VehicleManager vehicleManager, MaintenanceManager maintenanceManager) {
        this.vehicleManager = vehicleManager;
        this.maintenanceManager = maintenanceManager;
        this.activeRentals = new ArrayList<>();
        this.rentalIdCounter = 1000;
    }

    public RentalRecord processRental(RentalRequest request, Customer customer) {
        // Validate rental request
        if (!validateRentalRequest(request)) {
            return null;
        }
        
        // Check vehicle availability
        Vehicle vehicle = vehicleManager.getVehicleById(request.getVehicleId());
        if (vehicle == null || !vehicle.isAvailable()) {
            System.out.println("Vehicle is not available for rental.");
            return null;
        }
        
       // Create rental record 
    String rentalId = "R" + (++rentalIdCounter);
    RentalRecord rental = new RentalRecord(
        rentalId,
        customer.getCustomerId(),
        request.getVehicleId(),
        request.getPickupLocation(),
        request.getPickupDate(),
        request.getReturnDate(),
        request.getRentalDays(),
        0.0, // temporary cost 
        vehicle.getModel(),
        customer.getName()
    );

       //Calculate total cost 
       PaymentCalculator calculator = new PaymentCalculator();
       double totalCost = calculator.calculateBaseAmount(rental);
       rental.setTotalCost(totalCost);
        
        // Mark vehicle as rented
        if (vehicleManager.rentVehicle(request.getVehicleId())) {
            activeRentals.add(rental);
            System.out.println("Rental processed successfully!");
            return rental;
        }
        
        return null;
    }
    
    public boolean returnVehicle(String rentalId) {
        RentalRecord rental = findActiveRental(rentalId);
        if (rental == null) {
            System.out.println("Active rental not found.");
            return false;
        }

            // Mark vehicle as available again
        if (vehicleManager.returnVehicle(rental.getVehicleId())) {
            // Update rental record status
            rental.setReturnDate(LocalDate.now());
            rental.setStatus(RentalStatus.RETURNED);
            
            Vehicle vehicle = vehicleManager.getVehicleById(rental.getVehicleId());
            if (vehicle != null) {
            vehicle.incrementRentalCount();
            checkAndScheduleMaintenance(vehicle); // Trigger maintenance if needed
            }
            // Remove from active rentals
            activeRentals.remove(rental);
            
            System.out.println("Vehicle returned successfully!");
            System.out.println("Rental ID: " + rentalId);
            System.out.println("Total Cost: RM" + rental.getTotalCost());
            
            return true;
        }

        return false;
    }

    public boolean extendRental(String rentalId, int additionalDays) {
        RentalRecord rental = findActiveRental(rentalId);
        if (rental == null) {
            System.out.println("Active rental not found.");
            return false;
        }

        // Get vehicle to calculate additional cost
        Vehicle vehicle = vehicleManager.getVehicleById(rental.getVehicleId());
        if (vehicle == null) {
            System.out.println("Vehicle not found.");
            return false;
        }

        // Calculate additional cost
         PaymentCalculator calculator = new PaymentCalculator();
        double additionalCost = calculator.calculateExtensionCost(rental.getVehicleModel(),additionalDays);
        
        // Update rental record
        rental.setRentalDays(rental.getRentalDays() + additionalDays);
        rental.setReturnDate(rental.getReturnDate().plusDays(additionalDays));
        rental.setTotalCost(rental.getTotalCost() + additionalCost);

        System.out.println("Rental extended successfully!");
        System.out.println("Additional days: " + additionalDays);
        System.out.println("Additional cost: $" + additionalCost);
        System.out.println("New total cost: $" + rental.getTotalCost());
        
        return true;
    }

    public List<RentalRecord> getActiveRentals() {
        return new ArrayList<>(activeRentals);
    }

    public List<RentalRecord> getCustomerRentals(String customerId) {
        List<RentalRecord> customerRentals = new ArrayList<>();
        for (RentalRecord rental : activeRentals) {
            if (rental.getCustomerId().equals(customerId)) {
                customerRentals.add(rental);
            }
        }
        return customerRentals;
    }

    public RentalRecord getRentalById(String rentalId) {
        return findActiveRental(rentalId);
    }

    public void displayRentalSummary(String rentalId) {
        RentalRecord rental = findActiveRental(rentalId);
        if (rental == null) {
            System.out.println("Rental not found.");
            return;
        }

        System.out.println("\n==================== RENTAL SUMMARY ====================");
        System.out.println("Rental ID: " + rental.getRentalId());
        System.out.println("Customer: " + rental.getCustomerName());
        System.out.println("Vehicle: " + rental.getVehicleModel());
        System.out.println("Pickup Location: " + rental.getPickupLocation());
        System.out.println("Pickup Date: " + rental.getPickupDate());
        System.out.println("Return Date: " + rental.getReturnDate());
        System.out.println("Rental Days: " + rental.getRentalDays());
        System.out.println("Total Cost: $" + rental.getTotalCost());
        System.out.println("Status: " + rental.getStatus());
        System.out.println("========================================================\n");
    }

    public void displayAllActiveRentals() {
        if (activeRentals.isEmpty()) {
            System.out.println("No active rentals found.");
            return;
        }

        System.out.println("\n==================== ACTIVE RENTALS ====================");
        for (RentalRecord rental : activeRentals) {
            System.out.println("ID: " + rental.getRentalId() + 
                             " | Customer: " + rental.getCustomerName() + 
                             " | Vehicle: " + rental.getVehicleModel() + 
                             " | Days: " + rental.getRentalDays() + 
                             " | Cost: RM" + rental.getTotalCost());
        }
        System.out.println("========================================================\n");
    }

    public double getTotalRevenue() {
        double totalRevenue = 0;
        for (RentalRecord rental : activeRentals) {
            totalRevenue += rental.getTotalCost();
        }
        return totalRevenue;
    }

    public List<RentalRecord> getOverdueRentals() {
        List<RentalRecord> overdueRentals = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (RentalRecord rental : activeRentals) {
            if (rental.getReturnDate().isBefore(today)) {
                overdueRentals.add(rental);
            }
        }
        
        return overdueRentals;
    }

    public boolean cancelRental(String rentalId) {
        RentalRecord rental = findActiveRental(rentalId);
        if (rental == null) {
            System.out.println("Active rental not found.");
            return false;
        }

        // Check if rental can be cancelled (e.g., within 24 hours of pickup)
        LocalDate today = LocalDate.now();
        if (rental.getPickupDate().isBefore(today.plusDays(1))) {
            System.out.println("Cannot cancel rental within 24 hours of pickup date.");
            return false;
        }

        // Return vehicle to available status
        if (vehicleManager.returnVehicle(rental.getVehicleId())) {
            rental.setStatus(RentalStatus.CANCELLED);
            activeRentals.remove(rental);
            
            System.out.println("Rental cancelled successfully!");
            System.out.println("Rental ID: " + rentalId);
            
            return true;
        }

        return false;
    }

    // Private helper methods
    private RentalRecord findActiveRental(String rentalId) {
        for (RentalRecord rental : activeRentals) {
            if (rental.getRentalId().equals(rentalId)) {
                return rental;
            }
        }
        return null;
    }

    private void checkAndScheduleMaintenance(Vehicle vehicle) {
    int threshold = vehicle.getCategory().equalsIgnoreCase("Economy") ? 20 : 10;

    if (vehicle.getRentalCount() >= threshold) {
        vehicle.setAvailable(false);  // Set status as unavailable for maintenance
        vehicle.resetRentalCount();   // Reset rental count
        maintenanceManager.scheduleMaintenance(
            vehicle.getId(),
            vehicle.getModel(),
            MaintenanceType.GENERAL_INSPECTION,
            LocalDate.now().plusDays(1),  // Schedule for tomorrow
            "Auto-scheduled after " + threshold + " rentals."
        );
        System.out.println("Vehicle " + vehicle.getId() + " auto-flagged for maintenance.");
    }
}


    private boolean validateRentalRequest(RentalRequest request) {
        if (request == null) {
            System.out.println("Invalid rental request.");
            return false;
        }

        if (request.getVehicleId() == null || request.getVehicleId().trim().isEmpty()) {
            System.out.println("Vehicle ID is required.");
            return false;
        }

        if (request.getPickupLocation() == null || request.getPickupLocation().trim().isEmpty()) {
            System.out.println("Pickup location is required.");
            return false;
        }

        if (request.getPickupDate() == null) {
            System.out.println("Pickup date is required.");
            return false;
        }

        if (request.getReturnDate() == null) {
            System.out.println("Return date is required.");
            return false;
        }

        if (request.getPickupDate().isAfter(request.getReturnDate())) {
            System.out.println("Pickup date cannot be after return date.");
            return false;
        }

        if (request.getPickupDate().isBefore(LocalDate.now())) {
            System.out.println("Pickup date cannot be in the past.");
            return false;
        }

        if (request.getRentalDays() <= 0) {
            System.out.println("Rental days must be greater than 0.");
            return false;
        }

        return true;
    }
}


