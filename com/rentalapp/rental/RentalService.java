package com.rentalapp.rental;

import com.rentalapp.vessel.*;
import com.rentalapp.auth.Customer;
import com.rentalapp.auth.MemberCustomer;
import com.rentalapp.maintenance.MaintenanceManager;
import com.rentalapp.maintenance.MaintenanceType;
import com.rentalapp.payment.PaymentCalculator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class RentalService {
    private final VesselManager vesselManager;
    private final MaintenanceManager maintenanceManager;
    private final PaymentCalculator paymentCalculator;

    private final List<RentalRecord> activeRentals = new ArrayList<>();
    private final List<RentalRecord> completedRentals = new ArrayList<>();
    private List<AddOn> availableAddOns = new ArrayList<>();
    private int rentalIdCounter = 1000;
    private final RentalHistory rentalHistory;

    // Maintenance thresholds per vessel category
    private static final Map<String, Integer> MAINTENANCE_THRESHOLDS = Map.of(
        "yacht", 10,
        "pontoon", 10,
        "boat", 12,
        "jet ski", 15,
        "fishing charter", 10
    );

    public RentalService(VesselManager vesselManager, MaintenanceManager maintenanceManager, RentalHistory rentalHistory) {
        this.vesselManager = vesselManager;
        this.maintenanceManager = maintenanceManager;
        this.paymentCalculator = new PaymentCalculator();
        this.rentalHistory = rentalHistory;
    }

    

    // ================= ADD-ONS =================
    public void loadAvailableAddOns(List<AddOn> addOns) {
        this.availableAddOns = new ArrayList<>(addOns);
    }

    public List<AddOn> getAvailableAddOns() {
        return new ArrayList<>(availableAddOns);
    }

    public void addAddOnToRental(RentalRecord rental, AddOn addOn) {
        rental.addAddOn(addOn);
        rental.setTotalCost(rental.getBasePrice() + rental.getTotalAddOnsCost());
    }

    // ================= RENTAL PROCESSING =================
    public RentalRecord processRental(RentalRequest request, Customer customer) {
        if (!validateRentalRequest(request)) return null;

        Vessel vessel = vesselManager.getVesselById(request.getVesselId());
        if (vessel == null || !vessel.isAvailable()) {
            System.out.println("Vessel is not available for rental.");
            return null;
        }

        String rentalId = "V" + (++rentalIdCounter);
        double calculatedTotalCost = request.getTotalCost();

        RentalRecord rental = new RentalRecord(
            rentalId,
            customer.getCustomerId(),
            request.getVesselId(),
            request.getPickupLocation(),
            request.getScheduledStart(),
            request.getScheduledEnd(),
            request.getDuration(),
            0.0,
            calculatedTotalCost,
            vessel.getVesselType(),
            vessel.getVesselCategory(),
            customer.getName()
        );

        // Calculate base price
        double basePrice = vessel.getBasePrice();
        if (customer instanceof MemberCustomer member) {
        basePrice -= basePrice * (member.getDiscountRate() / 100.0);
        }
        double tax = basePrice * 0.06;

        // Add-ons
        double addOnTotal = 0.0;
        if (request.getAddOns() != null) {
            for (AddOn addon : request.getAddOns()) {
                rental.addAddOn(addon);
                addOnTotal += addon.getTotalPrice();
            }
        }

        double totalCost = basePrice + tax  + addOnTotal;
        rental.setBasePrice(basePrice);
        rental.setTaxAmount(tax);
        rental.setTotalCost(totalCost);

        if (vesselManager.rentVessel(request.getVesselId())) {
            activeRentals.add(rental);
            System.out.println("Vessel rental processed successfully!");
            System.out.println("Note: All rentals come with a certified captain for safety and navigation.");
            return rental;
        }
        return null;
    }

    public boolean extendRental(String rentalId, Duration additionalDuration) {
        RentalRecord rental = findRental(rentalId, activeRentals);
        if (rental == null) {
            System.out.println("Active rental not found.");
            return false;
        }
        rental.setScheduledEnd(rental.getScheduledEnd().plus(additionalDuration));
        rental.setDuration(rental.getDuration().plus(additionalDuration));
        double additionalCost = paymentCalculator.calculateExtensionCost(rental.getVesselCategory(), additionalDuration);
        rental.setTotalCost(rental.getTotalCost() + additionalCost);
        return true;
    }

    // ================= RENTAL COMPLETION =================
    public boolean returnVessel(String rentalId) {
        return completeRental(rentalId, RentalStatus.COMPLETED);
    }

    public boolean cancelRental(RentalRecord rental) {
    if (rental == null) return false;

    if (vesselManager.returnVessel(rental.getVesselId())) {
        rental.setStatus(RentalStatus.CANCELLED);
        rental.setActualEnd(LocalDateTime.now()); // record cancellation time

        activeRentals.remove(rental);
        completedRentals.add(rental); // keep in history as "cancelled"

       addToRentalHistory(
        rental,
        null,   
        rental.getPaymentMethod() != null ? rental.getPaymentMethod() : "N/A",
        0
        );


    System.out.println("\nRental " + rental.getRentalId() + " has been cancelled. Vessel returned to availability.");
    return true;
    }
    return false;
     }

    private boolean completeRental(String rentalId, RentalStatus finalStatus) {
        RentalRecord rental = findRental(rentalId, activeRentals);
        if (rental == null) {
            System.out.println("Active rental not found.");
            return false;
        }

        if (vesselManager.returnVessel(rental.getVesselId())) {
            if (rental.getActualEnd() == null) {
                rental.setActualEnd(LocalDateTime.now());
            }
            rental.setStatus(finalStatus);

            Vessel vessel = vesselManager.getVesselById(rental.getVesselId());
            if (vessel != null && finalStatus == RentalStatus.COMPLETED) {
                vessel.incrementRentalCount();
                checkAndScheduleMaintenance(vessel);
            }

            activeRentals.remove(rental);
            if (finalStatus == RentalStatus.COMPLETED || finalStatus == RentalStatus.CANCELLED) {
                completedRentals.add(rental);
            }
            System.out.println("Rental " + rentalId + " marked as " + finalStatus);
            return true;
        }
        return false;
    }

    public void addToRentalHistory(RentalRecord rental, Customer customer,
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

}

    // ================= QUERIES =================
    public List<RentalRecord> getActiveRentals() {
        return new ArrayList<>(activeRentals);
    }

    public List<RentalRecord> getCompletedRentals() {
        return new ArrayList<>(completedRentals);
    }

    public int getCompletedRentalsCount(String customerId) {
    return (int) completedRentals.stream()
            .filter(r -> r.getCustomerId().equals(customerId))
            .filter(r -> r.getStatus() == RentalStatus.COMPLETED) // only count finished rentals, not cancelled
            .count();
        }

    public List<RentalRecord> getAllRentals() {
        List<RentalRecord> all = new ArrayList<>(activeRentals);
        all.addAll(completedRentals);
        return all;
    }

    public List<RentalRecord> getCustomerActiveRentals(String customerId) {
        return filterRentals(activeRentals, r ->
            r.getCustomerId().equals(customerId) && r.getStatus() == RentalStatus.ACTIVE
        );
    }

   public List<RentalRecord> getCustomerFinishedRentals(String customerId) {
    return filterRentals(completedRentals, r ->
        r.getCustomerId().equals(customerId) &&
        (r.getStatus() == RentalStatus.COMPLETED || r.getStatus() == RentalStatus.CANCELLED)
    );
}
    public RentalRecord getRentalById(String rentalId) {
        RentalRecord rental = findRental(rentalId, activeRentals);
        return rental != null ? rental : findRental(rentalId, completedRentals);
    }

    public List<RentalRecord> getOverdueRentals() {
        return filterRentals(activeRentals, RentalRecord::isOverdue);
    }

    public double getTotalRevenue() {
        return activeRentals.stream().mapToDouble(RentalRecord::getTotalCost).sum();
    }

    public RentalHistory getRentalHistory() {
    return this.rentalHistory; 
    }

    public List<RentalHistoryRecord> getCustomerRentalHistory(String customerId) {
        return rentalHistory.getCustomerHistory(customerId);
    }

    // ================= DISPLAY =================
    public void displayRentalSummary(String rentalId) {
        RentalRecord rental = getRentalById(rentalId);
        if (rental == null) {
            System.out.println("Rental not found.");
            return;
        }
        rental.printDetails();
    }

    public void displayAllActiveRentals() {
    System.out.println("\n==================== ACTIVE VESSEL RENTALS ====================");

    List<RentalRecord> activeList = activeRentals.stream()
            .filter(r -> r.getStatus() == RentalStatus.ACTIVE)
            .toList();

    if (activeList.isEmpty()) {
        System.out.println("No active vessel rentals found.");
    } else {
        activeList.forEach(r -> System.out.println(r.toString()));
    }

    System.out.println("==============================================================\n");
}

    // ================= HELPERS =================
    private RentalRecord findRental(String rentalId, List<RentalRecord> rentals) {
        return rentals.stream()
            .filter(r -> r.getRentalId().equals(rentalId))
            .findFirst()
            .orElse(null);
    }

    private List<RentalRecord> filterRentals(List<RentalRecord> source, java.util.function.Predicate<RentalRecord> filter) {
        List<RentalRecord> results = new ArrayList<>();
        for (RentalRecord rental : source) {
            if (filter.test(rental)) {
                results.add(rental);
            }
        }
        return results;
    }

    private void checkAndScheduleMaintenance(Vessel vessel) {
        int threshold = MAINTENANCE_THRESHOLDS.getOrDefault(vessel.getVesselCategory().toLowerCase(), 12);
        if (vessel.getRentalCount() >= threshold) {
            vessel.setAvailable(false);
            vessel.resetRentalCount();
            maintenanceManager.scheduleMaintenance(
                vessel.getId(),
                vessel.getVesselType(),
                MaintenanceType.GENERAL_INSPECTION,
                LocalDate.now().plusDays(1),
                "Auto-scheduled after " + threshold + " rentals. Marine safety inspection required."
            );
            System.out.println("⚠ Vessel " + vessel.getId() + " auto-flagged for maintenance.");
        }
    }

    private boolean validateRentalRequest(RentalRequest request) {
        return request != null
            && request.getVesselId() != null && !request.getVesselId().isBlank()
            && request.getPickupLocation() != null && !request.getPickupLocation().isBlank()
            && request.getScheduledStart() != null && request.getScheduledEnd() != null
            && !request.getScheduledStart().isAfter(request.getScheduledEnd())
            && !request.getScheduledStart().isBefore(LocalDateTime.now());
    }

    
}
