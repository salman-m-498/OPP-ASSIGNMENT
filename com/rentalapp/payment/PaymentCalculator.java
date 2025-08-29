package com.rentalapp.payment;

import com.rentalapp.rental.RentalRecord;
import com.rentalapp.auth.Customer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class PaymentCalculator {

        // Member discounts by category (Standard, VIP)
    private static final java.util.Map<String, Double[]> MEMBER_DISCOUNT_MAP = java.util.Map.of(
        "Yacht", new Double[]{0.08, 0.15},
        "Pontoon", new Double[]{0.08, 0.15},
        "Boat", new Double[]{0.08, 0.15},
        "Jet Ski", new Double[]{0.05, 0.12},
        "Fishing Charter", new Double[]{0.05, 0.12}
    );


    public double calculateBaseAmount(RentalRecord rental) {
        // Base price from the vessel itself
        double base = rental.getBasePrice();

        // Multiply by fractional days (convert Duration to hours / 24)
        Duration duration = rental.getDuration();
        double fractionalDays = duration.toMinutes() / 60.0 / 24.0;
        return base * fractionalDays;
    }


    // Calculate member discount
    public double calculateMemberDiscount(Customer customer, double amount, RentalRecord rental) {
        if (!customer.isMember()) return 0.0;

        Double[] discounts = MEMBER_DISCOUNT_MAP.getOrDefault(rental.getVesselCategory(), new Double[]{0.0, 0.0});
        return customer.isMember() ? amount * discounts[1] : amount * discounts[0];
    }

    // Calculate refund amount
  public double calculateRefundAmount(RentalRecord rental) {
    if (rental == null) return 0.0;

    LocalDateTime now = LocalDateTime.now();
    long hoursUntilStart = java.time.temporal.ChronoUnit.HOURS
                               .between(now, rental.getScheduledStart());
    double totalCost = rental.getTotalCost();

    if (hoursUntilStart >= 168) { 
        // 7+ days
        System.out.println("Eligible for 100% refund.");
        return totalCost;
    } else if (hoursUntilStart >= 72) { 
        // 3–6 days (72–167h)
        System.out.println("Eligible for 50% refund.");
        return totalCost * 0.50;
    } else {
        // <48h
        System.out.println("No refund (within 48 hours of rental).");
        return 0.0;
    }
}

    // Calculate extension cost
    public double calculateExtensionCost(String vesselCategory, Duration additionalDuration) {
        double hourlyRate = 0.0;
        String lowerCategory = vesselCategory.toLowerCase();

        if (lowerCategory.contains("yacht")) hourlyRate = 500.0;
        else if (lowerCategory.contains("fishing")) hourlyRate = 300.0;
        else if (lowerCategory.contains("jet ski")) hourlyRate = 150.0;
        else if (lowerCategory.contains("pontoon")) hourlyRate = 250.0;
        else if (lowerCategory.contains("boat")) hourlyRate = 200.0;

        return hourlyRate * additionalDuration.toHours() * 1.06; // including 6% tax
    }


    public double calculateDamageFee(String damageType, String severity) {
        double baseFee = 0.0;

        switch (damageType.toUpperCase()) {
            case "MINOR":
                baseFee = 200.0; // could be scaled up to 1000 based on severity
                break;
            case "MAJOR":
                baseFee = 5000.0; // placeholder for full repair
                break;
            case "CLEANING":
                baseFee = 200.0; // up to 500
                break;
            case "LOST_EQUIPMENT":
                baseFee = 150.0; // average value
                break;
        }

        double multiplier = 1.0;
        switch (severity.toUpperCase()) {
            case "MODERATE": multiplier = 1.5; break;
            case "SEVERE": multiplier = 2.0; break;
        }

        return baseFee * multiplier;
    }

}
