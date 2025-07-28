package com.rentalapp.payment;

import com.rentalapp.rental.RentalRecord;
import com.rentalapp.auth.Customer;
import java.util.*;

public class PaymentCalculator {
     private static final Map<String, VehiclePrice> VEHICLE_PRICES = new HashMap<>();

     static {
        // Initialize Economy vehicles
        VEHICLE_PRICES.put("Perodua Axia", new VehiclePrice(100.0, "ECONOMY", 5));
        VEHICLE_PRICES.put("Proton Saga", new VehiclePrice(115.0, "ECONOMY", 6));
        VEHICLE_PRICES.put("Toyota Vios", new VehiclePrice(145.0, "ECONOMY", 7));
        VEHICLE_PRICES.put("Honda City", new VehiclePrice(155.0, "ECONOMY", 7));
        VEHICLE_PRICES.put("Perodua Myvi", new VehiclePrice(115.0, "ECONOMY", 6));
        VEHICLE_PRICES.put("Perodua Bezza", new VehiclePrice(110.0, "ECONOMY", 6));
        VEHICLE_PRICES.put("Proton X50", new VehiclePrice(165.0, "ECONOMY", 8));
        VEHICLE_PRICES.put("Perodua Ativa", new VehiclePrice(155.0, "ECONOMY", 8));
        VEHICLE_PRICES.put("Perodua Alza", new VehiclePrice(175.0, "ECONOMY", 9));
        VEHICLE_PRICES.put("Toyota Avanza", new VehiclePrice(195.0, "ECONOMY", 9));
        VEHICLE_PRICES.put("Toyota Hilux", new VehiclePrice(200.0, "ECONOMY", 10));
        VEHICLE_PRICES.put("Proton X70", new VehiclePrice(180.0, "ECONOMY", 9));
        VEHICLE_PRICES.put("Subaru XV", new VehiclePrice(185.0, "ECONOMY", 9));
        VEHICLE_PRICES.put("Nissan NV200", new VehiclePrice(200.0, "ECONOMY", 10));
        VEHICLE_PRICES.put("Toyota Corolla Altis Hybrid", new VehiclePrice(175.0, "ECONOMY", 8));
        
        // Initialize Luxury vehicles
        VEHICLE_PRICES.put("Mercedes-Benz C200", new VehiclePrice(500.0, "LUXURY", 20));
        VEHICLE_PRICES.put("BMW 320i", new VehiclePrice(550.0, "LUXURY", 22));
        VEHICLE_PRICES.put("BMW X5", new VehiclePrice(700.0, "LUXURY", 30));
        VEHICLE_PRICES.put("Mercedes-Benz GLC300", new VehiclePrice(675.0, "LUXURY", 28));
        VEHICLE_PRICES.put("Toyota Camry", new VehiclePrice(350.0, "LUXURY", 15));
        VEHICLE_PRICES.put("Honda Accord", new VehiclePrice(320.0, "LUXURY", 14));
        VEHICLE_PRICES.put("Toyota Vellfire", new VehiclePrice(850.0, "LUXURY", 35));
        VEHICLE_PRICES.put("Toyota Alphard", new VehiclePrice(875.0, "LUXURY", 36));
        VEHICLE_PRICES.put("Ford Mustang", new VehiclePrice(1100.0, "LUXURY", 40));
        VEHICLE_PRICES.put("Nissan 370Z", new VehiclePrice(800.0, "LUXURY", 32));
        VEHICLE_PRICES.put("Mazda MX-5", new VehiclePrice(700.0, "LUXURY", 28));
        VEHICLE_PRICES.put("Tesla Model 3", new VehiclePrice(600.0, "LUXURY", 25));
        VEHICLE_PRICES.put("BYD Seal Performance", new VehiclePrice(500.0, "LUXURY", 20));
        VEHICLE_PRICES.put("Range Rover Velar", new VehiclePrice(950.0, "LUXURY", 38));
        VEHICLE_PRICES.put("Lexus ES250", new VehiclePrice(575.0, "LUXURY", 23));
    }

    public double calculateBaseAmount(RentalRecord rental) {
        VehiclePrice vehiclePrice = VEHICLE_PRICES.get(rental.getVehicleModel());
        if (vehiclePrice == null) {
            System.out.println("Warning: Vehicle model not found in pricing database: " + 
                             rental.getVehicleModel());
            return 0.0;
        }
        
        return vehiclePrice.getDailyRate() * rental.getRentalDays();
    }

    public double calculateMemberDiscount(Customer customer, double baseAmount, RentalRecord rental) {
        if (!customer.isMember()) {
            return 0.0; // No discount for non-members
        }
        
        VehiclePrice vehiclePrice = VEHICLE_PRICES.get(rental.getVehicleModel());
        if (vehiclePrice == null) {
            return 0.0;
        }
        
        String vehicleCategory = vehiclePrice.getCategory();
        boolean isVip = customer.isVipMember();
        
        double discountRate = 0.0;
        
        if ("ECONOMY".equals(vehicleCategory)) {
            discountRate = isVip ? 0.15 : 0.10; // 15% VIP, 10% Standard
        } else if ("LUXURY".equals(vehicleCategory)) {
            discountRate = isVip ? 0.10 : 0.05; // 10% VIP, 5% Standard
        }
        
        return baseAmount * discountRate;
    }

    public int calculateLoyaltyPoints(RentalRecord rental) {
        VehiclePrice vehiclePrice = VEHICLE_PRICES.get(rental.getVehicleModel());
        if (vehiclePrice == null) {
            return 0;
        }
        
        return vehiclePrice.getLoyaltyPointsPerDay() * rental.getRentalDays();
    }

    public double calculateLateFee(RentalRecord rental, int overdueDays) {
        if (overdueDays <= 0) {
            return 0.0;
        }
        
        VehiclePrice vehiclePrice = VEHICLE_PRICES.get(rental.getVehicleModel());
        if (vehiclePrice == null) {
            return 0.0;
        }
        
        // Late fee is 150% of daily rate for each overdue day
        double dailyRate = vehiclePrice.getDailyRate();
        return dailyRate * 1.5 * overdueDays;
    }

    public double calculateExtensionCost(String vehicleModel, int additionalDays) {
        VehiclePrice vehiclePrice = VEHICLE_PRICES.get(vehicleModel);
        if (vehiclePrice == null) {
            return 0.0;
        }
        
        return vehiclePrice.getDailyRate() * additionalDays;
    }

    public double calculateDamageFee(String damageType, String severity) {
        Map<String, Double> damageFees = new HashMap<>();
        
        // Base damage fees
        damageFees.put("MINOR_SCRATCH", 50.0);
        damageFees.put("MAJOR_SCRATCH", 150.0);
        damageFees.put("DENT_SMALL", 100.0);
        damageFees.put("DENT_LARGE", 300.0);
        damageFees.put("INTERIOR_DAMAGE", 200.0);
        damageFees.put("TIRE_DAMAGE", 250.0);
        damageFees.put("WINDSHIELD_CRACK", 400.0);
        damageFees.put("BUMPER_DAMAGE", 500.0);
        damageFees.put("TOTAL_LOSS", 10000.0);
        
        double baseFee = damageFees.getOrDefault(damageType, 0.0);
        
        // Apply severity multiplier
        double severityMultiplier = 1.0;
        switch (severity.toUpperCase()) {
            case "MINOR":
                severityMultiplier = 1.0;
                break;
            case "MODERATE":
                severityMultiplier = 1.5;
                break;
            case "SEVERE":
                severityMultiplier = 2.0;
                break;
        }
        
        return baseFee * severityMultiplier;
    }

    public PaymentBreakdown calculatePaymentBreakdown(RentalRecord rental, Customer customer) {
        double baseAmount = calculateBaseAmount(rental);
        double memberDiscount = calculateMemberDiscount(customer, baseAmount, rental);
        double subtotal = baseAmount - memberDiscount;
        
        // Calculate additional fees if any
        double lateFee = 0.0; // Will be calculated based on actual return date
        double damageFee = 0.0; // Will be added if there's damage
        double tax = subtotal * 0.06; // 6% SST (Service & Sales Tax)
        
        double finalAmount = subtotal + lateFee + damageFee + tax;
        int loyaltyPoints = calculateLoyaltyPoints(rental);
        
        return new PaymentBreakdown(
            baseAmount,
            memberDiscount,
            subtotal,
            lateFee,
            damageFee,
            tax,
            finalAmount,
            loyaltyPoints
        );
    }

    public VehiclePrice getVehiclePrice(String vehicleModel) {
        return VEHICLE_PRICES.get(vehicleModel);
    }

    public Set<String> getAvailableVehicleModels() {
        return VEHICLE_PRICES.keySet();
    }

    // Inner classes for data structure
    public static class VehiclePrice {
        private double dailyRate;
        private String category;
        private int loyaltyPointsPerDay;

        public VehiclePrice(double dailyRate, String category, int loyaltyPointsPerDay) {
            this.dailyRate = dailyRate;
            this.category = category;
            this.loyaltyPointsPerDay = loyaltyPointsPerDay;
        }

        public double getDailyRate() { return dailyRate; }
        public String getCategory() { return category; }
        public int getLoyaltyPointsPerDay() { return loyaltyPointsPerDay; }
    }

    public static class PaymentBreakdown {
        private double baseAmount;
        private double memberDiscount;
        private double subtotal;
        private double lateFee;
        private double damageFee;
        private double tax;
        private double finalAmount;
        private int loyaltyPoints;

        public PaymentBreakdown(double baseAmount, double memberDiscount, double subtotal,
                              double lateFee, double damageFee, double tax, 
                              double finalAmount, int loyaltyPoints) {
            this.baseAmount = baseAmount;
            this.memberDiscount = memberDiscount;
            this.subtotal = subtotal;
            this.lateFee = lateFee;
            this.damageFee = damageFee;
            this.tax = tax;
            this.finalAmount = finalAmount;
            this.loyaltyPoints = loyaltyPoints;
        }

        // Getters
        public double getBaseAmount() { return baseAmount; }
        public double getMemberDiscount() { return memberDiscount; }
        public double getSubtotal() { return subtotal; }
        public double getLateFee() { return lateFee; }
        public double getDamageFee() { return damageFee; }
        public double getTax() { return tax; }
        public double getFinalAmount() { return finalAmount; }
        public int getLoyaltyPoints() { return loyaltyPoints; }

        public void printBreakdown() {
            System.out.println("\n========== PAYMENT BREAKDOWN ==========");
            System.out.println("Base Amount: RM " + String.format("%.2f", baseAmount));
            System.out.println("Member Discount: -RM " + String.format("%.2f", memberDiscount));
            System.out.println("Subtotal: RM " + String.format("%.2f", subtotal));
            if (lateFee > 0) {
                System.out.println("Late Fee: RM " + String.format("%.2f", lateFee));
            }
            if (damageFee > 0) {
                System.out.println("Damage Fee: RM " + String.format("%.2f", damageFee));
            }
            System.out.println("Tax (6%): RM " + String.format("%.2f", tax));
            System.out.println("---------------------------------------");
            System.out.println("FINAL AMOUNT: RM " + String.format("%.2f", finalAmount));
            System.out.println("Loyalty Points Earned: " + loyaltyPoints);
            System.out.println("=======================================\n");
        }
    }

}
