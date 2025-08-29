package com.rentalapp.rental;

import com.rentalapp.vessel.VesselManager;
import com.rentalapp.auth.Customer;
import com.rentalapp.payment.PaymentManager;
import com.rentalapp.payment.Receipt;
import com.rentalapp.loyalty.LoyaltyPointManager;
import com.rentalapp.maintenance.MaintenanceManager;
import com.rentalapp.payment.PaymentCalculator;


import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;

public class RentalManager {
    private final RentalService rentalService;
    private final PaymentManager paymentManager;
    private final RentalHistory rentalHistory;
    private final LoyaltyPointManager loyaltyPointManager;
    private final PaymentCalculator paymentCalculator;
    private final Map<String, Customer> customerDatabase;
    private final MaintenanceManager maintenanceManager;

    private final Scanner scanner = new Scanner(System.in);

    // ✅ New constructor: everything is passed in, nothing new() inside
    public RentalManager(RentalService rentalService,
                         RentalHistory rentalHistory,
                         PaymentCalculator paymentCalculator,
                         PaymentManager paymentManager,
                         LoyaltyPointManager loyaltyPointManager,
                         MaintenanceManager maintenanceManager) {
        this.rentalService = rentalService;
        this.rentalHistory = rentalHistory;
        this.paymentCalculator = paymentCalculator;
        this.paymentManager = paymentManager;
        this.loyaltyPointManager = loyaltyPointManager;
        this.maintenanceManager = maintenanceManager;
        this.customerDatabase = new HashMap<>();
    }



    // Create rental using LocalDateTime for scheduled start/end
     public RentalRecord createRental(String customerId, String vesselId, String pickupLocation, 
                                LocalDateTime scheduledStart, LocalDateTime scheduledEnd, 
                                List<AddOn> selectedAddOns) {
    Customer customer = customerDatabase.get(customerId);
    if (customer == null) {
        System.out.println("Customer not found: " + customerId);
        return null;
    }

    // Calculate duration properly
    Duration duration = Duration.between(scheduledStart, scheduledEnd);
    RentalRequest request = new RentalRequest(customerId, vesselId, pickupLocation, 
                                            scheduledStart, scheduledEnd, duration);

    // CRITICAL FIX: Add selected add-ons to the request BEFORE processing
    if (selectedAddOns != null) {
        request.setAddOns(selectedAddOns);
    }

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

    // Get payment details based on method
    String cardNumber = null;
    String eWalletPhone = null;
    if (paymentMethod.equalsIgnoreCase("CARD")) {
        System.out.print("Enter card number: ");
        cardNumber = scanner.nextLine().trim();
    } else if (paymentMethod.equalsIgnoreCase("EWALLET")) {
        System.out.print("Enter e-Wallet phone number: ");
        eWalletPhone = scanner.nextLine().trim();
    }

    // Call unified processPayment
    Receipt receipt = paymentManager.processPayment(
            rental,
            customer,
            paymentMethod,
            cardNumber,
            eWalletPhone
    );

    if (receipt != null) {
        addToRentalHistory(rental, customer, paymentMethod, receipt.getLoyaltyPointsEarned());
        System.out.println("Payment processed successfully!");
        paymentManager.printReceipt(receipt.getReceiptId());
        return true;
    }
    return false;
}

    public boolean returnVessel(String rentalId) {
        RentalRecord rental = rentalService.getRentalById(rentalId);
        if (rental == null) {
            System.out.println("Active rental not found: " + rentalId);
            return false;
        }

        boolean returned = rentalService.returnVessel(rentalId);
        if (returned) {
            updateRentalHistoryStatus(rentalId, "RETURNED");
            System.out.println("Vessel returned successfully!");
            System.out.println("Thank you for choosing our vessel rental service!");
        }
        return returned;
    }

    public boolean extendRental(String rentalId, int additionalHours, String paymentMethod) {
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

    Duration additionalDuration = Duration.ofHours(additionalHours);

    boolean extended = rentalService.extendRental(rentalId, additionalDuration);
    if (extended) {
        String cardNumber = null;
        String eWalletPhone = null;

        if (paymentMethod.equalsIgnoreCase("CARD")) {
            System.out.print("Enter card number: ");
            cardNumber = scanner.nextLine().trim();
        } else if (paymentMethod.equalsIgnoreCase("EWALLET")) {
            System.out.print("Enter e-Wallet phone number: ");
            eWalletPhone = scanner.nextLine().trim();
        }

         boolean isLateReturn = LocalDateTime.now().isAfter(rental.getScheduledEnd());
        Receipt receipt = paymentManager.processExtensionPayment(
        rental,
        customer,
        additionalDuration,   // Duration of extension
        isLateReturn,         // true if late, false if planned
        paymentMethod,
        cardNumber,           // pass null if not card
        eWalletPhone          // pass null if not e-wallet
        );

        if (receipt != null) {
            addToRentalHistory(rental, customer, paymentMethod, receipt.getLoyaltyPointsEarned());
            System.out.println("Rental extended and payment processed successfully!");
            return true;
        } else {
            System.out.println("Rental extended but payment failed. Please process payment manually.");
            return false;
        }
    }
    return false;
}

   public double cancelRental(RentalRecord rental) {
    if (rental == null) {
        System.out.println("Active rental not found.");
        return -1.0;
    }

     if (!isCancellationAllowed(rental)) {
        return -1.0; // signal controller to NOT say "cancelled"
    }

    double refundAmount = paymentCalculator.calculateRefundAmount(rental);
    
     boolean cancelled = rentalService.cancelRental(rental);
    if (!cancelled) {
        return -1.0; // failed to cancel
    }

    if (refundAmount > 0) {
        paymentManager.processRefund(rental.getRentalId(), refundAmount);
    }
    updateRentalHistoryStatus(rental.getRentalId(), "CANCELLED");

    return refundAmount; // return to controller for display
    }

    private boolean isCancellationAllowed(RentalRecord rental) {
    if (rental == null) return false;

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime start = rental.getScheduledStart(); // <-- rename if needed

    // Block if already started
    if (start != null && !now.isBefore(start)) {
        return false;
    }

    // Block if within 48 hours of start
    if (start != null) {
        long hoursUntilStart = Duration.between(now, start).toHours();
        return hoursUntilStart >= 48;
    }

    // If start is unknown, be conservative (block) or return true per your business rule
    return false;
}

    public List<AddOn> showAddOnMenu(String vesselCategory) {
        List<AddOn> availableAddOns = new ArrayList<>();
        availableAddOns.add(new AddOn("Custom Décor", "Event themes, balloons, floral, LED lights", 800, List.of("Yacht", "Pontoon")));
        availableAddOns.add(new AddOn("Catering Service", "Buffet, plated meals, BBQ grill, seafood platters",  150, "pax", 1, List.of("Yacht", "Pontoon")));
        availableAddOns.add(new AddOn("Snorkel Sets", "Mask, fins, snorkel tube, life vest", 40, "set", 1, List.of("Boat", "Yacht")));
        availableAddOns.add(new AddOn("Fishing Gear", "Rods, reels, tackle, bait", 120, "pax", 1, List.of("Fishing Charter")));
        availableAddOns.add(new AddOn("Photo/Video Drone", "Professional drone photography & videography", 800, List.of("Yacht", "Jet Ski")));
        availableAddOns.add(new AddOn("Underwater GoPro", "Waterproof camera with memory card",200, List.of("Boat", "Jet Ski", "Snorkeler")));
        availableAddOns.add(new AddOn("Live Music/DJ", "Live band or DJ performance", 2000, List.of("Yacht", "Pontoon")));
        availableAddOns.add(new AddOn("Water Toys", "Floating mats, paddleboards, inflatables", 500, List.of("Yacht", "Pontoon")));

       // normalize category matching (ignore case)
    String cat = vesselCategory == null ? "" : vesselCategory.trim().toLowerCase();

    // filter by suitability
    List<AddOn> filtered = new ArrayList<>();
    for (AddOn a : availableAddOns) {
        if (a.getSuitableFor() != null) {
            for (String s : a.getSuitableFor()) {
                if (s != null && s.trim().equalsIgnoreCase(cat)) {
                    filtered.add(a);
                    break;
                }
            }
        }
    }

    System.out.println("\n=== Available Add-Ons for " + vesselCategory + " ===");
    if (filtered.isEmpty()) {
        System.out.println("No add-ons available for this vessel type.");
        return new ArrayList<>();
    }

    for (int i = 0; i < filtered.size(); i++) {
        AddOn a = filtered.get(i);
        String priceStr = String.format("RM %.2f", a.getPrice());
        if (a.getUnit() != null) {
            priceStr += " / " + a.getUnit();
        }
        System.out.printf("%d. %s - %s (%s)\n", i + 1, a.getName(), priceStr, a.getDescription());
    }

    System.out.print("Enter add-on numbers separated by commas (or leave blank for none): ");
    String input = scanner.nextLine().trim();
    if (input.isEmpty()) return new ArrayList<>();

    List<AddOn> selected = new ArrayList<>();
    String[] parts = input.split(",");
    for (String p : parts) {
        try {
            int idx = Integer.parseInt(p.trim()) - 1;
            if (idx >= 0 && idx < filtered.size()) {
                AddOn chosen = filtered.get(idx);
                // if unit is provided, ask quantity
                if (chosen.getUnit() != null) {
                    while (true) {
                        try {
                            System.out.printf("Enter quantity (%s) for %s: ", chosen.getUnit(), chosen.getName());
                            String qtyStr = scanner.nextLine().trim();
                            int qty = Integer.parseInt(qtyStr);
                            if (qty <= 0) {
                                System.out.println("Quantity must be >= 1.");
                                continue;
                            }
                            chosen.setCount(qty);
                            break;
                        } catch (NumberFormatException ex) {
                            System.out.println("Please enter a valid integer quantity.");
                        }
                    }
                }
                selected.add(chosen);
            }
        } catch (NumberFormatException ignored) {}
    }

    return selected;
}
   public void processOverdueRentals(String paymentMethod, String maskedCardNumber, String eWalletPhoneNumber) {
    List<RentalRecord> overdueList = rentalService.getOverdueRentals();
    overdueList.forEach(rental -> {
        long overdueHours = java.time.temporal.ChronoUnit.HOURS.between(
                rental.getScheduledEnd(),
                LocalDateTime.now()
        );

        if (overdueHours > 0) {
            System.out.printf("Rental %s is overdue by %d hours. Processing late return payment...\n",
                              rental.getRentalId(), overdueHours);

            Customer customer = customerDatabase.get(rental.getCustomerId());
            if (customer != null) {
                // Treat overdue hours as an "extension", but mark as late
                Duration overdueDuration = Duration.ofHours(overdueHours);

                Receipt receipt = paymentManager.processExtensionPayment(
                        rental,
                        customer,
                        overdueDuration,
                        true,                // isLateReturn = true
                        paymentMethod,
                        maskedCardNumber,
                        eWalletPhoneNumber
                );

                if (receipt != null) {
                    System.out.println("Overdue payment completed. Receipt: " + receipt.getReceiptId());
                } else {
                    System.out.println("Overdue payment failed for rental " + rental.getRentalId());
                }
            }
        }
    });
}

  private void addToRentalHistory(RentalRecord rental, Customer customer,
                                String paymentMethod, int loyaltyPoints) {
    // Pick actualEnd if available, otherwise fall back to scheduledEnd
    LocalDateTime actualEnd = rental.getActualEnd() != null
            ? rental.getActualEnd()
            : rental.getScheduledEnd();

    // Recalculate duration based on actual return
    Duration actualDuration = Duration.between(rental.getScheduledStart(), actualEnd);

    RentalHistoryRecord historyRecord = new RentalHistoryRecord(
            rental.getRentalId(),
            rental.getCustomerId(),
            rental.getCustomerName(),
            rental.getVesselId(),
            rental.getVesselType(),    
            rental.getVesselCategory(),
            rental.getPickupLocation(),
            rental.getScheduledStart(),
            rental.getScheduledEnd(),
            actualEnd,                 
            actualDuration,            
            rental.getTotalCost(),
            paymentMethod,       
            rental.getStatus().toString(),
            loyaltyPoints
    );

    rentalHistory.addRentalRecord(historyRecord);

    loyaltyPointManager.addPoints(rental.getCustomerId(), loyaltyPoints);
    System.out.println("Rental added to history successfully.");
    System.out.println("Loyalty points earned: " + loyaltyPoints);
}
    private void displayRentalSummary(String rentalId) {
        RentalRecord rental = rentalService.getRentalById(rentalId);
        if (rental != null) {
            rental.printDetails(); 
    }
}

    private void updateRentalHistoryStatus(String rentalId, String newStatus) {
        rentalHistory.updateStatus(rentalId, newStatus);
    }

    public void generateRentalReport() {

    System.out.println("\n==================== RENTAL REPORT ====================");

    List<RentalHistoryRecord> allRentals = rentalHistory.getAllHistory();
    if (allRentals.isEmpty()) {
        System.out.println("No rental records found.");
        return;
    }
    System.out.println("Total Rentals: " + allRentals.size());

    // Total revenue
    double totalRevenue = allRentals.stream()
            .mapToDouble(RentalHistoryRecord::getTotalAmount)
            .sum();
    System.out.println("Total Revenue: RM " + String.format("%.2f", totalRevenue));

    // Status breakdown
    Map<String, Long> statusCount = new HashMap<>();
    for (RentalHistoryRecord record : allRentals) {
        statusCount.put(record.getStatus(), statusCount.getOrDefault(record.getStatus(), 0L) + 1);
    }

    System.out.println("\nStatus Breakdown:");
    statusCount.forEach((status, count) -> System.out.println("- " + status + ": " + count));

    // Revenue per customer
    Map<String, Double> revenuePerCustomer = new HashMap<>();
    for (RentalHistoryRecord record : allRentals) {
        revenuePerCustomer.put(record.getCustomerName(),
                revenuePerCustomer.getOrDefault(record.getCustomerName(), 0.0) + record.getTotalAmount());
    }

    System.out.println("\nTop Customers by Revenue:");
    revenuePerCustomer.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> System.out.println("- " + entry.getKey() + ": RM " + String.format("%.2f", entry.getValue())));

    // Top rented vessels
    Map<String, Long> vesselCount = new HashMap<>();
    for (RentalHistoryRecord record : allRentals) {
        vesselCount.put(record.getVesselType(),
                vesselCount.getOrDefault(record.getVesselType(), 0L) + 1);
    }

    System.out.println("\nTop Rented Vessels:");
    vesselCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> System.out.println("- " + entry.getKey() + ": " + entry.getValue() + " rentals"));

    System.out.println("=======================================================\n");
}

}
