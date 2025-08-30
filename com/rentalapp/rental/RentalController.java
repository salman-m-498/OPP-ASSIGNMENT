package com.rentalapp.rental;

import com.rentalapp.auth.Customer;
import com.rentalapp.auth.MemberCustomer;
import com.rentalapp.loyalty.LoyaltyAccount;
import com.rentalapp.loyalty.LoyaltyPointManager;
import com.rentalapp.payment.PaymentManager;
import com.rentalapp.payment.Receipt;
import com.rentalapp.vessel.Vessel;
import com.rentalapp.vessel.VesselManager;
import com.rentalapp.payment.PaymentCalculator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.List;

/**
 * RentalController handles all vessel rental operations with integrated payment processing
 */
public class RentalController {
    private final Scanner scanner; 
    private RentalService rentalService;
    private PaymentManager paymentManager;
    private VesselManager vesselManager;
    private RentalManager rentalManager;
    private PaymentCalculator paymentCalculator;
    private LoyaltyPointManager loyaltyPointManager;
    
    public RentalController(RentalService rentalService, PaymentManager paymentManager, 
                        PaymentCalculator paymentCalculator, VesselManager vesselManager,
                        RentalManager rentalManager,LoyaltyPointManager loyaltyPointManager) {
    this.scanner = new Scanner(System.in);
    this.rentalService = rentalService;
    this.paymentManager = paymentManager;
    this.paymentCalculator = paymentCalculator;
    this.vesselManager = vesselManager;  
    this.rentalManager = rentalManager;  
    this.loyaltyPointManager = loyaltyPointManager;  
}
    
    /**
     * Process a new vessel rental request from start to payment
     */
    public void processNewRental(Customer customer) {
        clearScreen();
        showHeader("NEW VESSEL RENTAL REQUEST");
        
        try {
  
            // --- Step 1: Filter and select a vessel ---
            System.out.println("Step 1: Select a Vessel");
            System.out.println("═".repeat(50));

            List<Vessel> availableVessels = vesselManager.getAvailableVessels();
            if (availableVessels.isEmpty()) {
                System.out.println("Sorry, no vessels are currently available for rental.");
                pauseForUser();
                return;
            }
            
            final String[] selectedCategory = {null};
            final String[] selectedLocation = {null};
            final String[] selectedPurpose = {null};
            String vesselId = null;
            Vessel selectedVessel = null;

    while (true) {
    // --- Step 1a: Category ---
    List<String> categories = availableVessels.stream()
            .map(Vessel::getVesselCategory)
            .distinct()
            .sorted()
            .toList();

    boolean backToDashboard = false;
    while (true) {
        System.out.println("\nSelect a category (0 to skip):");
        for (int i = 0; i < categories.size(); i++)
            System.out.printf("%d. %s%n", i + 1, categories.get(i));
        System.out.print("Enter choice: ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("B"))  {
            backToDashboard = true;
            break;
        }

        try {
            int choice = Integer.parseInt(input);
            if (choice == 0) {
                selectedCategory[0] = null;
                break;
            } else if (choice >= 1 && choice <= categories.size()) {
                selectedCategory[0] = categories.get(choice - 1);
                break;
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid number.");
        }
    }

    if (backToDashboard) return;

    // --- Step 1b: Location ---
    List<String> locations = availableVessels.stream()
            .map(Vessel::getLocation)
            .distinct()
            .sorted()
            .toList();

    
     boolean backToCategory = false;
     while (true) {
        System.out.println("\nSelect a location (0 to skip, B to go back):");
        for (int i = 0; i < locations.size(); i++)
            System.out.printf("%d. %s%n", i + 1, locations.get(i));
        System.out.print("Enter choice: ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("B")) {
            backToCategory = true;
            break;
        }

        try {
            int choice = Integer.parseInt(input);
            if (choice == 0) {
                selectedLocation[0] = null;
                break;
            } else if (choice >= 1 && choice <= locations.size()) {
                selectedLocation[0] = locations.get(choice - 1);
                break;
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid number.");
        }
    }
   if (backToCategory) {
        selectedCategory[0] = null; // reset category
        continue; // go back to category selection
    }

    // --- Step 1c: Purpose ---
    List<String> purposes = availableVessels.stream()
            .map(Vessel::getPurpose)
            .distinct()
            .sorted()
            .toList();

    
    boolean backToLocation = false;
    while (true) {
        System.out.println("\nSelect a purpose (0 to skip, B to go back):");
        for (int i = 0; i < purposes.size(); i++)
            System.out.printf("%d. %s%n", i + 1, purposes.get(i));
        System.out.print("Enter choice: ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("B")) {
            backToLocation = true;
            break;
        }

        try {
            int choice = Integer.parseInt(input);
            if (choice == 0) {
                selectedPurpose[0] = null;
                break;
            } else if (choice >= 1 && choice <= purposes.size()) {
                selectedPurpose[0] = purposes.get(choice - 1);
                break;
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid number.");
        }
    }
    if (backToLocation) {
        selectedLocation[0] = null; // reset location
        continue; // go back to location selection
    }

    // --- Step 1d: Apply filters ---
    List<Vessel> filteredVessels = availableVessels.stream()
            .filter(v -> selectedCategory[0] == null || v.getVesselCategory().equalsIgnoreCase(selectedCategory[0]))
            .filter(v -> selectedLocation[0] == null || v.getLocation().equalsIgnoreCase(selectedLocation[0]))
            .filter(v -> selectedPurpose[0] == null || v.getPurpose().equalsIgnoreCase(selectedPurpose[0]))
            .toList();

    if (filteredVessels.isEmpty()) {
        System.out.println("No vessels match the selected filters. Re-select filters.");
        selectedCategory[0] = null;
        selectedLocation[0] = null;
        selectedPurpose[0] = null;
        pauseForUser();
        continue;
    }

    // --- Step 1e: Display filtered vessels ---
    vesselManager.displayVessels(filteredVessels);
    System.out.print("\nEnter Vessel ID to rent (B to go back): ");
    String input = scanner.nextLine().trim();
    if (input.equalsIgnoreCase("B")) {
        selectedPurpose[0] = null; // go back to purpose selection
        continue;
    }

    vesselId = input; // assign to outer variable
    selectedVessel = vesselManager.getVesselById(vesselId); // assign to outer variable

    if (selectedVessel == null || !selectedVessel.isAvailable()) {
        System.out.println("Invalid vessel ID or vessel not available.");
        pauseForUser();
        continue;
    }

    // Vessel successfully selected, exit loop
    break;
}

            // Step 2: Get rental details
            System.out.println("\nStep 2: Rental Details");
            System.out.println("═".repeat(50));
            
            RentalRequest request = createRentalRequest(vesselId, customer);
            if (request == null) {
                return; // User cancelled or invalid input
            }
            
            // Step 3: Calculate and show cost breakdown
            System.out.println("\nStep 3: Cost Calculation");
            System.out.println("═".repeat(50));
            
            double totalAmount = showRentalCostBreakdown(request, selectedVessel, customer);
            request.setTotalCost(totalAmount);

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

            PaymentInput paymentInput = collectPaymentInput(rental.getTotalCost());
            Receipt receipt = paymentManager.processPayment(
            rental,
            customer,
            paymentInput.paymentMethod(),
            paymentInput.maskedCard(),      // pass null if E-Wallet
            paymentInput.eWalletPhone()     // pass null if card
            );

            if (receipt == null) {
                rentalService.cancelRental(rental);
                System.out.println("Payment failed. Rental has been cancelled.");
                pauseForUser();
                return;
            }
            
             rental.setPaymentMethod(paymentInput.paymentMethod());

            
            // Step 6: Update customer rental history
            customer.addRentalRecord(rental.getRentalId());
            
            // Step 7: Apply loyalty points for member customers
            if (customer instanceof MemberCustomer memberCustomer) {
            LoyaltyAccount account = loyaltyPointManager.getLoyaltyAccount(memberCustomer.getCustomerId());
            if (account == null) {
            account = loyaltyPointManager.createLoyaltyAccount(
            memberCustomer.getCustomerId(),
            memberCustomer.getName()
        );
    }

            int pointsEarned = loyaltyPointManager.getPointsForVessel(selectedVessel.getVesselCategory());

            if (account.isVipMember()) {
                pointsEarned = (int) Math.round(pointsEarned * 1.15);
            }

           loyaltyPointManager.addPoints(
           memberCustomer.getCustomerId(),
           pointsEarned,
           "RENTAL_POINTS",
           "Points earned from renting " + selectedVessel.getVesselType()
         );

            account.incrementRentalCount();

           System.out.println("\nLoyalty Points Earned: " + pointsEarned);
           System.out.println("Total Loyalty Points: " + account.getCurrentPoints());
}
            
            // Step 8: Show rental confirmation
            System.out.println("\nVESSEL RENTAL CONFIRMED");
            System.out.println("═".repeat(50));
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            System.out.println("Rental ID: " + rental.getRentalId());
            System.out.println("Vessel: " + selectedVessel.getVesselType());
            System.out.println("Location: " + selectedVessel.getLocation());
            System.out.println("Schedule: " + rental.getScheduledStart().format(dtf) + " to " + rental.getScheduledEnd().format(dtf));
            // Show duration from RentalRecord (hours/minutes)
            long dh = rental.getDuration().toHours();
            long dm = rental.getDuration().toMinutesPart();
            System.out.println("Duration: " + dh + "h " + dm + "m");
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
 * Handle vessel return process with payment settlement
 */
public void processVesselReturn(Customer customer) {
    clearScreen();
    showHeader("RETURN VESSEL");
    
    // Show customer's active rental
    List<RentalRecord> activeRentals = rentalService.getCustomerActiveRentals(customer.getCustomerId());
    
    if (activeRentals.isEmpty()) {
        System.out.println("You don't have any active rental to complete.");
        pauseForUser();
        return;
    }
    
    System.out.println("Your Active Rental:");
    System.out.println("═".repeat(50));
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    for (int i = 0; i < activeRentals.size(); i++) {
        RentalRecord rental = activeRentals.get(i);
        System.out.printf(
            "%d. Rental ID: %s | Vessel: %s (%s) | Location: %s | Current End: %s%n ",
            i + 1,
            rental.getRentalId(),
            rental.getVesselType(),
            rental.getVesselCategory(),
            rental.getPickupLocation(),
            rental.getScheduledEnd().format(formatter)
        );
        System.out.println("-".repeat(60)); 
    }

    System.out.println("\n0. Go Back to Rental Menu");

    int choice = -1;
    while (true){
    System.out.print("Select rental to complete (enter number): ");
    try {
        choice = Integer.parseInt(scanner.nextLine().trim());
        
 
            if (choice == 0) {
                System.out.println("Returning to Rental Menu...");
                pauseForUser();
                return; 
            } else if (choice >= 1 && choice <= activeRentals.size()) {
                break; // valid, exit loop
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
        RentalRecord rental = activeRentals.get(choice - 1);

        // === Step 1: Get actual return date/time ===
        LocalDateTime actualReturn = promptForActualReturnDateTime(rental);
        if (actualReturn == null) {
            System.out.println("Return process cancelled.");
            pauseForUser();
            return;
        }

        rental.setActualEnd(actualReturn);

        double extensionFee = 0.0;
        double additionalFee = 0.0;
        String paymentMethod = rental.getPaymentMethod();

        // === Step 2: Handle overdue/extended rental ===
        if (actualReturn.isAfter(rental.getScheduledEnd())) {
            Duration overdueDuration = Duration.between(rental.getScheduledEnd(), actualReturn);
            extensionFee = paymentCalculator.calculateExtensionCost(rental.getVesselCategory(), overdueDuration);

            System.out.println("\n LATE RETURN DETECTED!");
            System.out.println("Scheduled End: " + rental.getScheduledEnd().format(formatter));
            System.out.println("Actual Return: " + actualReturn.format(formatter));
            System.out.println("Overdue by: " + overdueDuration.toHours() + " hours " + (overdueDuration.toMinutes() % 60) + " minutes");
            System.out.println("Late Return Fee: RM" + String.format("%.2f", extensionFee));

        } else if (actualReturn.isBefore(rental.getScheduledEnd())) {
            System.out.println("\n EARLY RETURN");
            System.out.println("Scheduled End: " + rental.getScheduledEnd().format(formatter));
            System.out.println("Actual Return: " + actualReturn.format(formatter));
            System.out.println("Returned early - no additional charges for time.");
        } else {
            System.out.println("\n ON-TIME RETURN");
            System.out.println("Returned exactly on schedule - no additional time charges.");
        }

        // === Step 3: Handle additional charges (damage fees) ===
        System.out.print("\nAny additional charges for damages or issues? (y/n): ");
        String hasAdditionalCharges = scanner.nextLine().trim().toLowerCase();

        if (hasAdditionalCharges.equals("y") || hasAdditionalCharges.equals("yes")) {
            additionalFee = selectDamageFee();
            
            if (additionalFee > 0) {
                System.out.printf("\nSelected damage fee: RM%.2f\n", additionalFee);
        }
    }
         // === Step 4: Final Payment (Combined) === 
        double totalPayment = extensionFee + additionalFee;

        if (totalPayment > 0) {
            System.out.println("\n" + "═".repeat(50));
            System.out.println("PAYMENT SUMMARY");
            System.out.println("═".repeat(50));
            if (extensionFee > 0) System.out.printf("Late Return Fee : RM%.2f\n", extensionFee);
            if (additionalFee > 0) System.out.printf("Damage Fee      : RM%.2f\n", additionalFee);
                System.out.println("----------------------------------------");
                System.out.printf("TOTAL PAYMENT   : RM%.2f\n", totalPayment);
                System.out.println("═".repeat(50));

        System.out.print("Proceed with payment? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Rental completion cancelled.");
            pauseForUser();
            return;
        }

        PaymentInput paymentInput = collectPaymentInput(totalPayment);
        Receipt receipt = paymentManager.processCustomPayment(
            rental,
            customer,
            totalPayment,
            paymentInput.paymentMethod(),
            paymentInput.maskedCard(),
            paymentInput.eWalletPhone()
        );

        if (receipt == null) {
            System.out.println("Payment failed. Cannot complete rental.");
            pauseForUser();
            return;
        }

        paymentMethod = paymentInput.paymentMethod();
        rental.setPaymentMethod(paymentMethod);

         if (extensionFee > 0) {
            rental.addExtensionFee(extensionFee);
    }
        if (additionalFee > 0) {
            rental.setDamageFee(additionalFee); 
    }

    rental.recalculateTotalCost();
}

        // === Step 5: Finalize rental completion ===
        showLoadingMessage("Processing rental completion");
        if (rentalService.returnVessel(rental.getRentalId())) {

           int awardedPoints = 0;

        if (customer instanceof MemberCustomer memberCustomer) {
            // ensure account exists
            com.rentalapp.loyalty.LoyaltyAccount account = loyaltyPointManager.getLoyaltyAccount(memberCustomer.getCustomerId());
            if (account == null) {
                account = loyaltyPointManager.createLoyaltyAccount(memberCustomer.getCustomerId(), memberCustomer.getName());
            }

            // points for vessel (category-based)
            int pointsForVessel = loyaltyPointManager.getPointsForVessel(rental.getVesselCategory());
            if (account.isVipMember()) {
                pointsForVessel = (int) Math.round(pointsForVessel * 1.15);
            }

            // completion bonus
            int bonusPoints = 20;

            // Award only the completion bonus now (vessel points should have been awarded at booking/payment)
            loyaltyPointManager.addPoints(memberCustomer.getCustomerId(), bonusPoints,
                "RENTAL_COMPLETION_BONUS", "Bonus points for rental completion");

            // total points to record for this rental = vessel points + completion bonus
            awardedPoints = pointsForVessel + bonusPoints;
            paymentManager.updateReceiptLoyaltyPoints(rental.getRentalId(), awardedPoints);
    
            System.out.println("\nBonus Points for Rental Completion: " + bonusPoints);
            System.out.println("Updated Total Loyalty Points: "
                + loyaltyPointManager.getCustomerPoints(memberCustomer.getCustomerId()));
        }

        // Create and store history record with the awardedPoints value
       rentalService.addToRentalHistory(rental, customer, paymentMethod, awardedPoints);

        System.out.println("\n" + "═".repeat(60));
        System.out.println(" VESSEL RENTAL COMPLETED SUCCESSFULLY!");
        System.out.println("═".repeat(60));
        System.out.println("Rental ID: " + rental.getRentalId());
        System.out.println("Actual Return Date: " + actualReturn.format(formatter));
        System.out.println("Completion Date: " + LocalDate.now());
        if (totalPayment > 0) {
            System.out.println("\nCharges Paid:");
            if (extensionFee > 0) {
                System.out.println("- Late Return Fee : RM" + String.format("%.2f", extensionFee));
            }
            if (additionalFee > 0) {
                System.out.println("- Damage Fee      : RM" + String.format("%.2f", additionalFee));
            }
            System.out.println("----------------------------------------");
            System.out.println("TOTAL PAID       : RM" + String.format("%.2f", totalPayment));
        } else {
            System.out.println("\nNo additional charges applied.");
        }

        System.out.println("\nThank you for choosing our rental service!");

    } else {
        System.out.println("Failed to process rental completion. Please contact support.");
    }

    pauseForUser();
}


    public void extendRental(Customer customer) {
    clearScreen();
    showHeader("EXTEND RENTAL");

    List<RentalRecord> activeRentals = rentalService.getCustomerActiveRentals(customer.getCustomerId());

    if (activeRentals.isEmpty()) {
        System.out.println("You don't have any active rental to extend.");
        pauseForUser();
        return;
    }

    // Show active charters
    System.out.println("Your Active Rentals:");
    System.out.println("═".repeat(50));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    for (int i = 0; i < activeRentals.size(); i++) {
        RentalRecord rental = activeRentals.get(i);
        System.out.printf(
            "%d. Rental ID: %s | Vessel: %s (%s) | Location: %s | Current End: %s%n ",
            i + 1,
            rental.getRentalId(),
            rental.getVesselType(),
            rental.getVesselCategory(),
            rental.getPickupLocation(),
            rental.getScheduledEnd().format(formatter)
        );
    }

    System.out.print("\nSelect Rental to extend (enter number): ");
    try {
        int choice = Integer.parseInt(scanner.nextLine().trim());

        if (choice < 1 || choice > activeRentals.size()) {
            System.out.println("Invalid selection.");
            pauseForUser();
            return;
        }

        RentalRecord rental = activeRentals.get(choice - 1);

        System.out.print("Enter additional hours: ");
        int additionalHours = Integer.parseInt(scanner.nextLine().trim());

        if (additionalHours <= 0) {
            System.out.println("Additional hours must be greater than 0.");
            pauseForUser();
            return;
        }

        Duration extensionDuration = Duration.ofHours(additionalHours);

        // FIX: Create PaymentCalculator instance or use dependency injection
        if (paymentCalculator == null) {
            paymentCalculator = new PaymentCalculator();
        }
        
        String vesselCategory = rental.getVesselCategory();
        double extensionCost = paymentCalculator.calculateExtensionCost(vesselCategory, extensionDuration);

        System.out.println("\nExtension Cost: RM" + String.format("%.2f", extensionCost));
        System.out.print("Proceed with extension? (y/n): ");

        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Extension cancelled.");
            pauseForUser();
            return;
        }

        // Ask payment method
        PaymentInput paymentInput = collectPaymentInput(extensionCost);

        Receipt extensionReceipt = paymentManager.processExtensionPayment(
                rental,
                customer,
                extensionDuration,
                false,                     // not late return
                paymentInput.paymentMethod(),
                paymentInput.maskedCard(), // masked card OR null
                paymentInput.eWalletPhone()// eWallet OR null
        );

        if (extensionReceipt == null) {
            System.out.println("Extension payment failed.");
            pauseForUser();
            return;
        }

        // ✅ Extend rental if payment succeeds
        if (rentalService.extendRental(rental.getRentalId(), extensionDuration)) {
            rental.addExtensionFee(extensionCost);
            rental.recalculateTotalCost();
            System.out.println("\nRental Extension payment processed successfully!");
            System.out.println("Additional duration: " + additionalHours + " hours");
            System.out.println("Additional cost: RM" + extensionCost);
            System.out.println("New total cost: RM" + rental.getTotalCost());
        } else {
           System.out.println("Failed to extend rental. Please contact support.");
        }

        

    } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter valid numbers.");
    }

    pauseForUser();
    }

    public void cancelRental(Customer customer) {
    clearScreen();
    showHeader("CANCEL / REFUND RENTAL");

    List<RentalRecord> activeRentals = rentalService.getCustomerActiveRentals(customer.getCustomerId());

    if (activeRentals.isEmpty()) {
        System.out.println("You don't have any active rentals to cancel.");
        pauseForUser();
        return;
    }

    // Show active rentals
    System.out.println("Your Active Rentals:");
    System.out.println("═".repeat(50));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    for (int i = 0; i < activeRentals.size(); i++) {
        RentalRecord rental = activeRentals.get(i);
        System.out.printf(
            "%d. Rental ID: %s | Vessel: %s (%s) | Location: %s | Current End: %s%n",
            i + 1,
            rental.getRentalId(),
            rental.getVesselType(),
            rental.getVesselCategory(),
            rental.getPickupLocation(),
            rental.getScheduledEnd().format(formatter)
        );
         System.out.println("-".repeat(60)); 
    }

    System.out.println("\n0. Go Back to Rental Menu");
    System.out.print("Select Rental to cancel (enter number): ");

    try {
        int choice = Integer.parseInt(scanner.nextLine().trim());

        if (choice == 0) {
                return;
            }

        if (choice < 1 || choice > activeRentals.size()) {
            System.out.println("Invalid selection.");
            pauseForUser();
            return;
        }

        RentalRecord rental = activeRentals.get(choice - 1);

        System.out.print("Confirm cancellation & refund? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Cancellation aborted.");
            pauseForUser();
            return;
        }

        double refundAmount = rentalManager.cancelRental(rental);

        if (Double.compare(refundAmount, -1.0) == 0) {
            System.out.println("Cancellation not allowed (within 48 hours of start or rental already started).");
        } else if (refundAmount > 0.0) {
            System.out.println("\nRental cancelled successfully.");
            System.out.println("Refund processed: RM " + String.format("%.2f", refundAmount));
        } else {
            System.out.println("Rental cancelled successfully. No refund applicable as per policy.");
        }

    } catch (NumberFormatException e) {
        System.out.println("Invalid input. Enter a valid number.");
    }

    pauseForUser();
}



/**
 * Prompt user for actual return date/time
 */
private LocalDateTime promptForActualReturnDateTime(RentalRecord rental) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    System.out.println("\n" + "═".repeat(50));
    System.out.println("ACTUAL RETURN DATE/TIME");
    System.out.println("═".repeat(50));
    System.out.println("Scheduled Return: " + rental.getScheduledEnd().format(formatter));
    System.out.println("Current Time: " + LocalDateTime.now().format(formatter));
    
    while(true){
    System.out.println("\nOptions:");
    System.out.println("1. Use current date/time");
    System.out.println("2. Enter custom date/time");
    System.out.print("Choose option (1-2): ");
    
    try {
        int choice = Integer.parseInt(scanner.nextLine().trim());
        if (choice == 1) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(rental.getScheduledStart())) {
                System.out.println("Return time cannot be before rental start.");
                continue; // ask again
            }
        return now;
        
        } else if (choice == 2) {
            System.out.println("\nEnter actual return date and time:");
            System.out.print("Date (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine().trim();
            System.out.print("Time (HH:mm): ");
            String timeStr = scanner.nextLine().trim();
            try {
                LocalDateTime actualReturn = LocalDateTime.parse(dateStr + " " + timeStr, formatter);

                if (actualReturn.isBefore(rental.getScheduledStart())) {
                    System.out.println("Return time cannot be before rental start.");
                    continue;
                }

                if (actualReturn.isAfter(rental.getScheduledEnd().plusDays(30))) {
                    System.out.println("Return time cannot be more than 30 days after the scheduled return.");
                    continue; 
                }
                return actualReturn; // valid
            } catch (Exception e) {
                System.out.println("Invalid date/time format. Try again.");
            }
        } else {
            System.out.println("Please choose 1 or 2.");
        }
    } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter a number.");
    }
}
}

/**
 * Allow user to select damage fee from predefined options
 */
private double selectDamageFee() {
    System.out.println("\n" + "═".repeat(50));
    System.out.println("DAMAGE FEE SELECTION");
    System.out.println("═".repeat(50));
    
    // Define damage categories and severities
    String[][] damageOptions = {
        {"MINOR", "LIGHT", "Minor Damage - Light", "200.00"},
        {"MINOR", "MODERATE", "Minor Damage - Moderate", "300.00"},
        {"MINOR", "SEVERE", "Minor Damage - Severe", "400.00"},
        {"MAJOR", "LIGHT", "Major Damage - Light", "5000.00"},
        {"MAJOR", "MODERATE", "Major Damage - Moderate", "7500.00"},
        {"MAJOR", "SEVERE", "Major Damage - Severe", "10000.00"},
        {"CLEANING", "LIGHT", "Cleaning Fee - Light", "200.00"},
        {"CLEANING", "MODERATE", "Cleaning Fee - Moderate", "300.00"},
        {"CLEANING", "SEVERE", "Cleaning Fee - Severe", "400.00"},
        {"LOST_EQUIPMENT", "LIGHT", "Lost Equipment - Light", "150.00"},
        {"LOST_EQUIPMENT", "MODERATE", "Lost Equipment - Moderate", "225.00"},
        {"LOST_EQUIPMENT", "SEVERE", "Lost Equipment - Severe", "300.00"}
    };
    
    System.out.println("Select damage type and severity:");
    for (int i = 0; i < damageOptions.length; i++) {
        System.out.printf("%2d. %-25s - RM%s\n", 
            i + 1, 
            damageOptions[i][2], 
            damageOptions[i][3]);
    }
    System.out.println(" 0. No damage charges");
    
    System.out.print("\nEnter your choice (0-" + damageOptions.length + "): ");
    
    try {
        int choice = Integer.parseInt(scanner.nextLine().trim());
        
        if (choice == 0) {
            return 0.0;
        }
        
        if (choice < 1 || choice > damageOptions.length) {
            System.out.println("Invalid selection.");
            return 0.0;
        }
        
        String damageType = damageOptions[choice - 1][0];
        String severity = damageOptions[choice - 1][1];
        
        // Calculate fee using PaymentCalculator
        double calculatedFee = paymentCalculator.calculateDamageFee(damageType, severity);
        
        System.out.println("\nSelected: " + damageOptions[choice - 1][2]);
        System.out.println("Damage Type: " + damageType + " | Severity: " + severity);
        
        return calculatedFee;
        
    } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter a valid number.");
        return 0.0;
    }
}

/**
 * Helper method to collect payment input for cards or e-wallet
 */
private PaymentInput collectPaymentInput(double amount) {
    String paymentMethod;
    String maskedCard = null;
    String eWalletPhone = null;

    while (true) {
        System.out.println("\nPayment Amount: RM" + String.format("%.2f", amount));
        System.out.println("Available payment methods:");
        System.out.println("1. Credit Card");
        System.out.println("2. Debit Card");
        System.out.println("3. E-Wallet");
        System.out.print("\nSelect payment method (1-3): ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> paymentMethod = "Credit Card";
            case "2" -> paymentMethod = "Debit Card";
            case "3" -> paymentMethod = "E-Wallet";
            default -> {
                System.out.println("Invalid choice. Try again.");
                continue;
            }
        }

        if (paymentMethod.equals("Credit Card") || paymentMethod.equals("Debit Card")) {
            // Card input
            System.out.print("Enter Card Number (16 digits): ");
            String cardNumber = scanner.nextLine().trim();
            System.out.print("Enter CSV (3 digits): ");
            String csv = scanner.nextLine().trim();
            System.out.print("Enter Expiry Date (MM/YY): ");
            String expiry = scanner.nextLine().trim();

            // Validation
            if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
                System.out.println("Invalid card number.");
                continue;
            }
            if (csv.length() != 3 || !csv.matches("\\d+")) {
                System.out.println("Invalid CSV.");
                continue;
            }
            try {
                String[] parts = expiry.split("/");
                if (parts.length != 2) throw new IllegalArgumentException();
                int expMonth = Integer.parseInt(parts[0]);
                int expYear = Integer.parseInt(parts[1]);
                if (expMonth < 1 || expMonth > 12) {
                    System.out.println("Invalid expiry month.");
                    continue;
                }
                int fullYear = (expYear < 100) ? 2000 + expYear : expYear;
                YearMonth expiryYM = YearMonth.of(fullYear, expMonth);
                if (expiryYM.isBefore(YearMonth.now())) {
                    System.out.println("Card is expired.");
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Invalid expiry format. Use MM/YY.");
                continue;
            }

            maskedCard = maskCardNumber(cardNumber);
        } else { // E-Wallet
            System.out.print("Enter E-Wallet phone number: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Enter 6-digit PIN: ");
            String pin = scanner.nextLine().trim(); // just simulate

            if (!phone.matches("\\d{8,15}")) {
                System.out.println("Invalid phone number. Expect digits (8-15).");
                continue;
            }
            if (!pin.matches("\\d{6}")) {
                System.out.println("PIN must be 6 digits.");
                continue;
            }
            eWalletPhone = phone;
        }

        return new PaymentInput(paymentMethod, maskedCard, eWalletPhone);
    }
}

// --- Payment input record ---
private record PaymentInput(String paymentMethod, String maskedCard, String eWalletPhone) {}
    
    /**
     * View customer's rental history with payment details
     */
   public void viewRentalHistory(Customer customer) {
    clearScreen();
    showHeader("RENTAL HISTORY - " + customer.getName());

    List<RentalHistoryRecord> historyRecords = rentalService.getCustomerRentalHistory(customer.getCustomerId());

    if (historyRecords.isEmpty()) {
        System.out.println("No rental history found.");
        pauseForUser();
        return;
    }

    // Display rental history summary
    rentalService.getRentalHistory().displayCustomerHistory(customer.getCustomerId());

    // Ask if user wants to view details of a specific rental
    System.out.print("Enter Rental ID to view details (or press Enter to go back): ");
    String rentalId = scanner.nextLine().trim();

    if (!rentalId.isEmpty()) {
        rentalService.displayRentalSummary(rentalId);
    }

    pauseForUser();
}
    
 
    // Helper methods
    private RentalRequest createRentalRequest(String vesselId, Customer customer) {
        Vessel vessel = vesselManager.getVesselById(vesselId);
        
        System.out.println("Selected Vessel: " + vessel.getVesselType());
        System.out.println("Location: " + vessel.getLocation());
        long dh = vessel.getDuration().toHours();
        long dm = vessel.getDuration().toMinutesPart();
        System.out.println("Duration: " + dh + "h " + dm + "m");
        System.out.println("Purpose: " + vessel.getPurpose());
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime scheduledStart = null;
        LocalDateTime scheduledEnd = null;

    // --- Start Date & Time ---
    while (true) {
        try {
            System.out.print("\nStart Date & Time (YYYY-MM-DD HH:MM): ");
            String startStr = scanner.nextLine().trim();
            scheduledStart = LocalDateTime.parse(startStr, dtf);

            if (scheduledStart.isBefore(LocalDateTime.now())) {
                System.out.println("Start date/time cannot be in the past.");
                continue; // re-ask
            }

            // calculate end time
            scheduledEnd = scheduledStart.plus(vessel.getDuration());
            break; //  exit loop if success
        } catch (Exception e) {
            System.out.println("Invalid format. Example: 2025-08-31 13:00");
        }
    }

    // --- Number of Passengers ---
    int passengerCount = 0;
    while (true) {
        try {
            System.out.print("Number of passengers (max " + vessel.getCapacity() + "): ");
            passengerCount = Integer.parseInt(scanner.nextLine().trim());

            if (passengerCount <= 0 || passengerCount > vessel.getCapacity()) {
                System.out.println("Invalid passenger count. Try again.");
                continue;
            }
            break; // ✅ exit loop
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    // --- Add-On Selection ---
    List<AddOn> selectedAddOns = rentalManager.showAddOnMenu(vessel.getVesselCategory());

    // Duration calculation
    Duration duration = Duration.between(scheduledStart, scheduledEnd);

    // Build request
    RentalRequest request = new RentalRequest(
        customer.getCustomerId(), vesselId, vessel.getLocation(),
        scheduledStart, scheduledEnd, duration
    );
    request.setAddOns(selectedAddOns);
    return request;
    }

    private double showRentalCostBreakdown(RentalRequest request, Vessel vessel, Customer customer) {
       DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        System.out.println("Vessel: " + vessel.getVesselType());
        System.out.println("Location: " + vessel.getLocation());
        System.out.println("Schedule: " + request.getScheduledStart().format(dtf)  + " to " + request.getScheduledEnd().format(dtf));
        // Show duration from request (hours/minutes)
        Duration dur = request.getDuration();
        long h = dur.toHours();
        long m = dur.toMinutesPart();
        System.out.println("Duration: " + h + "h " + m + "m");
                      
        System.out.println("Base Rate: RM" + String.format("%.2f", vessel.getBasePrice()));
        
        double baseAmount = vessel.getBasePrice();
        System.out.println("Base Amount: RM" + String.format("%.2f", baseAmount));
        
        // Show member discount if applicable
        if (customer instanceof MemberCustomer) {
            MemberCustomer memberCustomer = (MemberCustomer) customer;
            double discount = baseAmount * memberCustomer.getDiscountRate();
            System.out.println("Member Discount (" + String.format("%.0f%%", memberCustomer.getDiscountRate() * 100) + "): -RM" + String.format("%.2f", discount));
            baseAmount -= discount;
        }
        
        double tax = baseAmount * 0.06; // 6% tax
        System.out.println("Tax (6%): RM" + String.format("%.2f", tax));
        
        double totalAmount = baseAmount + tax;
        // Add-on costs
        List<AddOn> addOns = request.getAddOns();
       if (addOns != null && !addOns.isEmpty()) {
           System.out.println("\nSelected Add-Ons:");
           for (AddOn addon : addOns) {
             System.out.printf("%s - RM%.2f x %d = RM%.2f\n", 
                addon.getName(), addon.getPrice(), addon.getCount(), addon.getTotalPrice());
              totalAmount += addon.getTotalPrice();
        }
    }

        System.out.println("═".repeat(30));
        System.out.println("TOTAL AMOUNT: RM" + String.format("%.2f", totalAmount));
        System.out.println("═".repeat(30));

        return totalAmount;
    }
    
    /**
     * Admin-only: Get all rentals in the system
     */
     public List<RentalRecord> getAllRentals() {
        return rentalService.getAllRentals(); // delegate to service layer
    }

    private String maskCardNumber(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 4) return "****";
    // Show first 2 and last 2 digits, mask the middle
    return cardNumber.substring(0, 2) 
           + "*".repeat(cardNumber.length() - 4) 
           + cardNumber.substring(cardNumber.length() - 2);
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