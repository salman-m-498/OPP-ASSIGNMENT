package com.rentalapp.rental;

import com.rentalapp.auth.Customer;
import com.rentalapp.auth.MemberCustomer;
import com.rentalapp.payment.PaymentManager;
import com.rentalapp.payment.Receipt;
import com.rentalapp.vehicle.Vehicle;
import com.rentalapp.vehicle.VehicleManager;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.List;

/**
 * RentalController handles all rental operations with integrated payment processing
 */
public class RentalController {
    private Scanner scanner;
    private RentalService rentalService;
    private PaymentManager paymentManager;
    private VehicleManager vehicleManager;
    
    public RentalController(Scanner scanner, RentalService rentalService, 
                          PaymentManager paymentManager, VehicleManager vehicleManager) {
        this.scanner = scanner;
        this.rentalService = rentalService;
        this.paymentManager = paymentManager;
        this.vehicleManager = vehicleManager;
    }
    
    /**
     * Process a new rental request from start to payment
     */
    public void processNewRental(Customer customer) {
        clearScreen();
        showHeader("NEW RENTAL REQUEST");
        
        try {
            // Step 1: Display available vehicles
            System.out.println("Step 1: Select a Vehicle");
            System.out.println("═".repeat(50));
            
            List<Vehicle> availableVehicles = vehicleManager.getAvailableVehicles();
            if (availableVehicles.isEmpty()) {
                System.out.println("Sorry, no vehicles are currently available for rental.");
                pauseForUser();
                return;
            }
            
            vehicleManager.displayVehicles(availableVehicles);
            
            System.out.print("\nEnter Vehicle ID to rent: ");
            String vehicleId = scanner.nextLine().trim();
            
            Vehicle selectedVehicle = vehicleManager.getVehicleById(vehicleId);
            if (selectedVehicle == null || !selectedVehicle.isAvailable()) {
                System.out.println("Invalid vehicle ID or vehicle not available.");
                pauseForUser();
                return;
            }
            
            // Step 2: Get rental details
            System.out.println("\nStep 2: Rental Details");
            System.out.println("═".repeat(50));
            
            RentalRequest request = createRentalRequest(vehicleId, customer);
            if (request == null) {
                return; // User cancelled or invalid input
            }
            
            // Step 3: Calculate and show cost breakdown
            System.out.println("\nStep 3: Cost Calculation");
            System.out.println("═".repeat(50));
            
            showRentalCostBreakdown(request, selectedVehicle, customer);
            
            System.out.print("\nProceed with this rental? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (!confirm.equals("y") && !confirm.equals("yes")) {
                System.out.println("Rental cancelled.");
                pauseForUser();
                return;
            }
            
            // Step 4: Process the rental
            System.out.println("\nStep 4: Processing Rental");
            System.out.println("═".repeat(50));
            
            showLoadingMessage("Creating rental record");
            
            RentalRecord rental = rentalService.processRental(request, customer);
            if (rental == null) {
                System.out.println("Failed to process rental. Please try again.");
                pauseForUser();
                return;
            }
            
            // Step 5: Process payment
            System.out.println("\nStep 5: Payment Processing");
            System.out.println("═".repeat(50));
            
            // Get payment method from user
            System.out.println("Available payment methods:");
            System.out.println("1. Credit Card");
            System.out.println("2. Debit Card");
            System.out.println("3. Cash");
            System.out.print("Select payment method (1-3): ");
            
            String paymentMethodChoice = scanner.nextLine().trim();
            String paymentMethod = "";
            
            switch (paymentMethodChoice) {
                case "1": paymentMethod = "Credit Card"; break;
                case "2": paymentMethod = "Debit Card"; break;
                case "3": paymentMethod = "Cash"; break;
                default: paymentMethod = "Credit Card"; break;
            }
            
            Receipt receipt = paymentManager.processPayment(rental, customer, paymentMethod);
            if (receipt == null) {
                // Payment failed, cancel the rental
                rentalService.cancelRental(rental.getRentalId());
                System.out.println("Payment failed. Rental has been cancelled.");
                pauseForUser();
                return;
            }
            
            // Step 6: Update customer rental history
            customer.addRentalRecord(rental.getRentalId());
            
            // Step 7: Apply loyalty points for member customers
            if (customer instanceof MemberCustomer) {
                MemberCustomer memberCustomer = (MemberCustomer) customer;
                int pointsEarned = (int) (rental.getTotalCost() / 10); // 1 point per RM10 spent
                memberCustomer.addLoyaltyPoints(pointsEarned);
                System.out.println("\nLoyalty Points Earned: " + pointsEarned);
                System.out.println("Total Loyalty Points: " + memberCustomer.getLoyaltyPoints());
            }
            
            // Step 8: Show rental confirmation
            System.out.println("\n" + "═".repeat(60));
            System.out.println("RENTAL CONFIRMED!");
            System.out.println("═".repeat(60));
            
            System.out.println("Rental ID: " + rental.getRentalId());
            System.out.println("Vehicle: " + selectedVehicle.getModel());
            System.out.println("Pickup Date: " + rental.getPickupDate());
            System.out.println("Return Date: " + rental.getReturnDate());
            System.out.println("Total Cost: RM" + String.format("%.2f", rental.getTotalCost()));
            System.out.println("Payment Processed Successfully!");
            
            System.out.println("\nPlease save your rental ID for future reference.");
            System.out.println("Receipt has been generated for your records.");
            
        } catch (Exception e) {
            System.out.println("An error occurred while processing your rental: " + e.getMessage());
        }
        
        pauseForUser();
    }
    
    /**
     * Handle rental return process with payment settlement
     */
    public void processRentalReturn(Customer customer) {
        clearScreen();
        showHeader("RETURN VEHICLE");
        
        // Show customer's active rentals
        List<RentalRecord> activeRentals = rentalService.getCustomerRentals(customer.getCustomerId());
        
        if (activeRentals.isEmpty()) {
            System.out.println("You don't have any active rentals to return.");
            pauseForUser();
            return;
        }
        
        System.out.println("Your Active Rentals:");
        System.out.println("═".repeat(50));
        
        for (int i = 0; i < activeRentals.size(); i++) {
            RentalRecord rental = activeRentals.get(i);
            System.out.printf("%d. Rental ID: %s | Vehicle: %s | Due: %s%n",
                i + 1, rental.getRentalId(), rental.getVehicleModel(), rental.getReturnDate());
        }
        
        System.out.print("\nSelect rental to return (enter number): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice < 1 || choice > activeRentals.size()) {
                System.out.println("Invalid selection.");
                pauseForUser();
                return;
            }
            
            RentalRecord rental = activeRentals.get(choice - 1);
            
            // Check for late return
            LocalDate today = LocalDate.now();
            boolean isLate = today.isAfter(rental.getReturnDate());
            double lateFee = 0.0;
            
            if (isLate) {
                long daysLate = java.time.temporal.ChronoUnit.DAYS.between(rental.getReturnDate(), today);
                lateFee = daysLate * 50.0; // RM50 per day late fee
                
                System.out.println("\nLATE RETURN DETECTED!");
                System.out.println("Days Late: " + daysLate);
                System.out.println("Late Fee: RM" + String.format("%.2f", lateFee));
                
                if (lateFee > 0) {
                    System.out.print("Proceed with late fee payment? (y/n): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    
                    if (!confirm.equals("y") && !confirm.equals("yes")) {
                        System.out.println("Return cancelled. Late fees must be paid to complete return.");
                        pauseForUser();
                        return;
                    }
                    
                    // Process late fee payment
                    Receipt lateFeeReceipt = paymentManager.processLateFeePayment(customer, lateFee);
                    if (lateFeeReceipt == null) {
                        System.out.println("Late fee payment failed. Cannot complete return.");
                        pauseForUser();
                        return;
                    }
                }
            }
            
            // Process the return
            showLoadingMessage("Processing vehicle return");
            
            if (rentalService.returnVehicle(rental.getRentalId())) {
                System.out.println("\n" + "═".repeat(60));
                System.out.println("VEHICLE RETURNED SUCCESSFULLY!");
                System.out.println("═".repeat(60));
                
                System.out.println("Rental ID: " + rental.getRentalId());
                System.out.println("Return Date: " + today);
                if (lateFee > 0) {
                    System.out.println("Late Fee Paid: RM" + String.format("%.2f", lateFee));
                }
                System.out.println("Thank you for using our rental service!");
                
                // Apply loyalty points for member customers
                if (customer instanceof MemberCustomer && !isLate) {
                    MemberCustomer memberCustomer = (MemberCustomer) customer;
                    int bonusPoints = 10; // Bonus points for on-time return
                    memberCustomer.addLoyaltyPoints(bonusPoints);
                    System.out.println("Bonus Points for On-Time Return: " + bonusPoints);
                }
            } else {
                System.out.println("Failed to process vehicle return. Please contact support.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
        
        pauseForUser();
    }
    
    /**
     * View customer's rental history with payment details
     */
    public void viewRentalHistory(Customer customer) {
        clearScreen();
        showHeader("RENTAL HISTORY - " + customer.getName());
        
        List<String> rentalHistory = customer.getRentalHistory();
        
        if (rentalHistory.isEmpty()) {
            System.out.println("No rental history found.");
            pauseForUser();
            return;
        }
        
        System.out.println("Total Rentals: " + rentalHistory.size());
        System.out.println("═".repeat(60));
        
        for (String rentalId : rentalHistory) {
            rentalService.displayRentalSummary(rentalId);
        }
        
        pauseForUser();
    }
    
    /**
     * Extend an existing rental
     */
    public void extendRental(Customer customer) {
        clearScreen();
        showHeader("EXTEND RENTAL");
        
        List<RentalRecord> activeRentals = rentalService.getCustomerRentals(customer.getCustomerId());
        
        if (activeRentals.isEmpty()) {
            System.out.println("You don't have any active rentals to extend.");
            pauseForUser();
            return;
        }
        
        // Show active rentals
        System.out.println("Your Active Rentals:");
        System.out.println("═".repeat(50));
        
        for (int i = 0; i < activeRentals.size(); i++) {
            RentalRecord rental = activeRentals.get(i);
            System.out.printf("%d. Rental ID: %s | Vehicle: %s | Current Return Date: %s%n",
                i + 1, rental.getRentalId(), rental.getVehicleModel(), rental.getReturnDate());
        }
        
        System.out.print("\nSelect rental to extend (enter number): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice < 1 || choice > activeRentals.size()) {
                System.out.println("Invalid selection.");
                pauseForUser();
                return;
            }
            
            RentalRecord rental = activeRentals.get(choice - 1);
            
            System.out.print("Enter number of additional days: ");
            int additionalDays = Integer.parseInt(scanner.nextLine().trim());
            
            if (additionalDays <= 0) {
                System.out.println("Additional days must be greater than 0.");
                pauseForUser();
                return;
            }
            
            // Calculate extension cost and process payment
            double extensionCost = calculateExtensionCost(rental.getVehicleModel(), additionalDays);
            
            System.out.println("\nExtension Cost: RM" + String.format("%.2f", extensionCost));
            System.out.print("Proceed with extension? (y/n): ");
            
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (!confirm.equals("y") && !confirm.equals("yes")) {
                System.out.println("Extension cancelled.");
                pauseForUser();
                return;
            }
            
            // Process extension payment
            Receipt extensionReceipt = paymentManager.processExtensionPayment(customer, extensionCost);
            if (extensionReceipt == null) {
                System.out.println("Extension payment failed.");
                pauseForUser();
                return;
            }
            
            if (rentalService.extendRental(rental.getRentalId(), additionalDays)) {
                System.out.println("\nRental extended successfully!");
                System.out.println("New return date: " + rental.getReturnDate().plusDays(additionalDays));
            } else {
                System.out.println("Failed to extend rental. Please contact support.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        }
        
        pauseForUser();
    }
    
    // Helper methods
    private RentalRequest createRentalRequest(String vehicleId, Customer customer) {
        System.out.print("Pickup Location: ");
        String pickupLocation = scanner.nextLine().trim();
        
        if (pickupLocation.isEmpty()) {
            System.out.println("Pickup location is required.");
            pauseForUser();
            return null;
        }
        
        System.out.print("Pickup Date (YYYY-MM-DD): ");
        String pickupDateStr = scanner.nextLine().trim();
        
        System.out.print("Return Date (YYYY-MM-DD): ");
        String returnDateStr = scanner.nextLine().trim();
        
        System.out.print("Driver Age: ");
        String driverAgeStr = scanner.nextLine().trim();
        
        try {
            LocalDate pickupDate = LocalDate.parse(pickupDateStr);
            LocalDate returnDate = LocalDate.parse(returnDateStr);
            int driverAge = Integer.parseInt(driverAgeStr);
            
            if (pickupDate.isBefore(LocalDate.now())) {
                System.out.println("Pickup date cannot be in the past.");
                pauseForUser();
                return null;
            }
            
            if (returnDate.isBefore(pickupDate)) {
                System.out.println("Return date cannot be before pickup date.");
                pauseForUser();
                return null;
            }
            
            return new RentalRequest(customer.getCustomerId(), vehicleId, pickupLocation, 
                                   pickupDate, returnDate, driverAge);
            
        } catch (Exception e) {
            System.out.println("Invalid input format. Please try again.");
            pauseForUser();
            return null;
        }
    }
    
    private void showRentalCostBreakdown(RentalRequest request, Vehicle vehicle, Customer customer) {
        System.out.println("Vehicle: " + vehicle.getModel());
        System.out.println("Rental Period: " + request.getPickupDate() + " to " + request.getReturnDate());
        System.out.println("Number of Days: " + request.getRentalDays());
        System.out.println("Daily Rate: RM" + String.format("%.2f", vehicle.getDailyRate()));
        
        double baseAmount = vehicle.getDailyRate() * request.getRentalDays();
        System.out.println("Base Amount: RM" + String.format("%.2f", baseAmount));
        
        // Show member discount if applicable
        if (customer instanceof MemberCustomer) {
            MemberCustomer memberCustomer = (MemberCustomer) customer;
            double discount = baseAmount * (memberCustomer.getDiscountRate() / 100.0);
            System.out.println("Member Discount (" + memberCustomer.getDiscountRate() + "%): -RM" + String.format("%.2f", discount));
            baseAmount -= discount;
        }
        
        double tax = baseAmount * 0.06; // 6% tax
        System.out.println("Tax (6%): RM" + String.format("%.2f", tax));
        
        double totalAmount = baseAmount + tax;
        System.out.println("═".repeat(30));
        System.out.println("TOTAL AMOUNT: RM" + String.format("%.2f", totalAmount));
        System.out.println("═".repeat(30));
    }
    
    private double calculateExtensionCost(String vehicleModel, int additionalDays) {
        // This is a simplified calculation - in reality, you'd get the vehicle's daily rate
        double dailyRate = vehicleModel.toLowerCase().contains("luxury") ? 200.0 : 100.0;
        return dailyRate * additionalDays * 1.06; // Including tax
    }
    
    private void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    private void showHeader(String title) {
        System.out.println("╔" + "═".repeat(title.length() + 2) + "╗");
        System.out.println("║ " + title + " ║");
        System.out.println("╚" + "═".repeat(title.length() + 2) + "╝");
        System.out.println();
    }
    
    private void showLoadingMessage(String message) {
        System.out.print(message + " ");
        for (int i = 0; i < 10; i++) {
            System.out.print(".");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(" Done!");
    }
    
    private void pauseForUser() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
