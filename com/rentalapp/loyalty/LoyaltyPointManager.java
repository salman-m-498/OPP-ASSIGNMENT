package com.rentalapp.loyalty;
import com.rentalapp.auth.MemberCustomer;

import java.time.LocalDateTime;
import java.util.*;

public class LoyaltyPointManager {
    private Map<String, LoyaltyAccount> loyaltyAccounts;
    private List<LoyaltyTransaction> transactions;
    private int transactionIdCounter;
    
    // Constants for loyalty system
    private static final int VIP_THRESHOLD_POINTS = 3000;
    private static final int VIP_THRESHOLD_RENTALS = 5;
    private static final int REVIEW_BONUS_POINTS = 25;
    
    public LoyaltyPointManager() {
        this.loyaltyAccounts = new HashMap<>();
        this.transactions = new ArrayList<>();
        this.transactionIdCounter = 1;
    }

    public static int getVipThresholdPoints() {
    return VIP_THRESHOLD_POINTS;
    }

   public static int getVipThresholdRentals() {
    return VIP_THRESHOLD_RENTALS;
}

    public LoyaltyAccount createLoyaltyAccount(String customerId, String customerName) {
        LoyaltyAccount account = new LoyaltyAccount(customerId, customerName);
        loyaltyAccounts.put(customerId, account);
        return account;
    }

    public int getPointsForVessel(String vesselCategory) {
    switch (vesselCategory.toLowerCase()) {
        case "jet ski": return 40;
        case "boat": return 175;
        case "pontoon": return 140;
        case "yacht": return 500;
        case "superyacht": return 750;
        case "fishing charter": return 165;
        default: return 50; // fallback
    }
   }


    public boolean addPoints(String customerId, int points) {
        return addPoints(customerId, points, "RENTAL_POINTS", "Points earned from rental");
    }

    public boolean addPoints(String customerId, int points, String transactionType, String description) {
        LoyaltyAccount account = loyaltyAccounts.get(customerId);
        if (account == null) {
            System.out.println("Loyalty account not found for customer: " + customerId);
            return false;
        }

        // Add points to account
        account.addPoints(points);
        
        // Create transaction record
        String transactionId = "LT" + (transactionIdCounter++);
        LoyaltyTransaction transaction = new LoyaltyTransaction(
            transactionId, customerId, points, transactionType, 
            description, LocalDateTime.now()
        );
        transactions.add(transaction);
        
        return true;
    }

    public boolean deductPoints(String customerId, int points) {
        return deductPoints(customerId, points, "POINTS_DEDUCTION", "Points deducted");
    }

    public boolean deductPoints(String customerId, int points, String transactionType, String description) {
        LoyaltyAccount account = loyaltyAccounts.get(customerId);
        if (account == null) {
            System.out.println("Loyalty account not found for customer: " + customerId);
            return false;
        }

        if (account.getCurrentPoints() < points) {
            System.out.println("Insufficient points. Available: " + account.getCurrentPoints() + 
                             ", Required: " + points);
            return false;
        }

        // Deduct points from account
        account.deductPoints(points);
        
        // Create transaction record
        String transactionId = "LT" + (transactionIdCounter++);
        LoyaltyTransaction transaction = new LoyaltyTransaction(
            transactionId, customerId, -points, transactionType, 
            description, LocalDateTime.now()
        );
        transactions.add(transaction);

        System.out.println("Deducted " + points + " points from " + account.getCustomerName() + 
                          ". Remaining: " + account.getCurrentPoints());
        
        return true;
    }

    public boolean redeemPoints(String customerId, int points, String rewardDescription) {
        return deductPoints(customerId, points, "REDEMPTION", "Redeemed: " + rewardDescription);
    }

    public boolean addReviewBonus(String customerId) {
        return addPoints(customerId, REVIEW_BONUS_POINTS, "REVIEW_BONUS", 
                        "Bonus points for providing review/feedback");
    }

    public LoyaltyAccount getLoyaltyAccount(String customerId) {
        return loyaltyAccounts.get(customerId);
    }

    public int getCustomerPoints(String customerId) {
        LoyaltyAccount account = loyaltyAccounts.get(customerId);
        return account != null ? account.getCurrentPoints() : 0;
    }

    public boolean isVipMember(String customerId) {
        LoyaltyAccount account = loyaltyAccounts.get(customerId);
        return account != null && account.isVipMember();
    }

    public List<LoyaltyTransaction> getCustomerTransactions(String customerId) {
        List<LoyaltyTransaction> customerTransactions = new ArrayList<>();
        for (LoyaltyTransaction transaction : transactions) {
            if (transaction.getCustomerId().equals(customerId)) {
                customerTransactions.add(transaction);
            }
        }
        return customerTransactions;
    }

    public void upgradeToVip(LoyaltyAccount account, MemberCustomer customer) {
    if (!account.isVipMember()) {
        // Update LoyaltyAccount
        account.upgradeToVip();

        // Update MemberCustomer 
        if (customer != null) {
            customer.setMembershipTier("VIP");
        }

        System.out.println("Congratulations! " + account.getCustomerName() +
                           " is now a VIP Member!");
        System.out.println("VIP Benefits Unlocked: +15% points, 15% discount on rentals, priority booking!");
    }
}

    public void displayLoyaltyStatus(String customerId) {
        LoyaltyAccount account = loyaltyAccounts.get(customerId);
        if (account == null) {
            System.out.println("Loyalty account not found for customer: " + customerId);
            return;
        }

        System.out.println("\n==================== LOYALTY STATUS ====================");
        System.out.println("Customer: " + account.getCustomerName());
        System.out.println("Customer ID: " + customerId);
        System.out.println("Current Points: " + account.getCurrentPoints());
        System.out.println("Total Vessel Rentals: " + account.getTotalRentals());
        System.out.println("Member Status: " + (account.isVipMember() ? "VIP MEMBER" : "STANDARD MEMBER"));
        System.out.println("Account Created: " + account.getAccountCreatedDate().toLocalDate());
        
        if (!account.isVipMember()) {
            int pointsToVip = VIP_THRESHOLD_POINTS - account.getCurrentPoints();
            int rentalsToVip = VIP_THRESHOLD_RENTALS - account.getTotalRentals();

            if (pointsToVip > 0 && rentalsToVip > 0) {
                System.out.println("VIP Requirements: " + Math.max(0, pointsToVip) + " more points OR " + 
                                 Math.max(0, rentalsToVip) + " more vessel rentals");
            } else {
                System.out.println("Eligible for VIP upgrade!");
            }
        } else {
            System.out.println("VIP Benefits: +15% points on rentals, 7% booking discount, priority booking");
            if (account.getVipUpgradeDate() != null) {
                System.out.println("VIP Since: " + account.getVipUpgradeDate().toLocalDate());
            }
        }
        
        // Show recent transactions
        List<LoyaltyTransaction> recentTransactions = getCustomerTransactions(customerId);
        if (!recentTransactions.isEmpty()) {
            System.out.println("\nRecent Transactions (Last 5):");
            recentTransactions.stream()
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .limit(5)
                .forEach(transaction -> 
                    System.out.println("- " + transaction.getTransactionDate().toLocalDate() + 
                                     " | " + (transaction.getPointsChange() > 0 ? "+" : "") + 
                                     transaction.getPointsChange() + " pts | " + 
                                     transaction.getDescription()));
        }
        
        System.out.println("========================================================\n");
    }

    public void displayAllLoyaltyAccounts() {
        if (loyaltyAccounts.isEmpty()) {
            System.out.println("No loyalty accounts found.");
            return;
        }

        System.out.println("\n==================== ALL LOYALTY ACCOUNTS ====================");
        System.out.printf("%-15s %-20s %-10s %-10s %-15s%n", 
                         "Customer ID", "Name", "Points", "Rentals", "Status");
        System.out.println("-".repeat(75));
        
        for (LoyaltyAccount account : loyaltyAccounts.values()) {
            System.out.printf("%-15s %-20s %-10d %-10d %-15s%n",
                             account.getCustomerId(),
                             account.getCustomerName(),
                             account.getCurrentPoints(),
                             account.getTotalRentals(),
                             account.isVipMember() ? "VIP" : "STANDARD");
        }
        System.out.println("==========================================================================\n");
    }

    public List<LoyaltyAccount> getTopLoyaltyMembers(int limit) {
        return loyaltyAccounts.values().stream()
                .sorted((a1, a2) -> Integer.compare(a2.getCurrentPoints(), a1.getCurrentPoints()))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    public Map<String, Integer> getLoyaltyStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        int totalMembers = loyaltyAccounts.size();
        int vipMembers = 0;
        int totalPoints = 0;
        int totalRentals = 0;
        
        for (LoyaltyAccount account : loyaltyAccounts.values()) {
            if (account.isVipMember()) vipMembers++;
            totalPoints += account.getCurrentPoints();
            totalRentals += account.getTotalRentals();
        }
        
        stats.put("TOTAL_MEMBERS", totalMembers);
        stats.put("VIP_MEMBERS", vipMembers);
        stats.put("STANDARD_MEMBERS", totalMembers - vipMembers);
        stats.put("TOTAL_POINTS_CIRCULATING", totalPoints);
        stats.put("TOTAL_VESSEL_RENTALS", totalRentals);
        stats.put("AVERAGE_POINTS_PER_MEMBER", totalMembers > 0 ? totalPoints / totalMembers : 0);
        stats.put("AVERAGE_RENTALS_PER_MEMBER", totalMembers > 0 ? totalRentals / totalMembers : 0);
        
        return stats;
    }

    public void generateLoyaltyReport() {
        Map<String, Integer> stats = getLoyaltyStatistics();
        
        System.out.println("\n==================== LOYALTY PROGRAM REPORT ====================");
        System.out.println("Total Members: " + stats.get("TOTAL_MEMBERS"));
        System.out.println("VIP Members: " + stats.get("VIP_MEMBERS"));
        System.out.println("Standard Members: " + stats.get("STANDARD_MEMBERS"));
        
        if (stats.get("TOTAL_MEMBERS") > 0) {
            double vipPercentage = (stats.get("VIP_MEMBERS") * 100.0) / stats.get("TOTAL_MEMBERS");
            System.out.println("VIP Percentage: " + String.format("%.1f%%", vipPercentage));
        }
        
        System.out.println("Total Points in Circulation: " + stats.get("TOTAL_POINTS_CIRCULATING"));
        System.out.println("Total Vessel Rentals: " + stats.get("TOTAL_VESSEL_RENTALS"));
        System.out.println("Average Points per Member: " + stats.get("AVERAGE_POINTS_PER_MEMBER"));
        System.out.println("Average Vessel Rentals per Member: " + stats.get("AVERAGE_RENTALS_PER_MEMBER"));
        
        // Transaction type breakdown
        Map<String, Integer> transactionTypes = new HashMap<>();
        for (LoyaltyTransaction transaction : transactions) {
            transactionTypes.put(transaction.getTransactionType(),
                               transactionTypes.getOrDefault(transaction.getTransactionType(), 0) + 1);
        }
        
        System.out.println("\nTransaction Type Breakdown:");
        transactionTypes.forEach((type, count) -> 
            System.out.println("- " + type + ": " + count + " transactions"));
        
        System.out.println("=================================================================\n");
    }

    public boolean transferPoints(String fromCustomerId, String toCustomerId, int points) {
        LoyaltyAccount fromAccount = loyaltyAccounts.get(fromCustomerId);
        LoyaltyAccount toAccount = loyaltyAccounts.get(toCustomerId);
        
        if (fromAccount == null || toAccount == null) {
            System.out.println("One or both loyalty accounts not found.");
            return false;
        }
        
        if (fromAccount.getCurrentPoints() < points) {
            System.out.println("Insufficient points for transfer. Available: " + 
                             fromAccount.getCurrentPoints());
            return false;
        }
        
        // Transfer points
        if (deductPoints(fromCustomerId, points, "TRANSFER_OUT", 
                        "Points transferred to " + toAccount.getCustomerName()) &&
            addPoints(toCustomerId, points, "TRANSFER_IN", 
                     "Points received from " + fromAccount.getCustomerName())) {
            
            System.out.println("Successfully transferred " + points + " points from " + 
                             fromAccount.getCustomerName() + " to " + toAccount.getCustomerName());
            return true;
        }
        
        return false;
    }

     public List<String> getAvailableRewards() {
        List<String> rewards = new ArrayList<>();
        rewards.add("Free Snorkel or Fishing Gear Rental (75 points)");
        rewards.add("Complimentary Beverage Cooler with Ice (100 points)");
        rewards.add("10% Rental Discount - Any Vessel, 1 day (200 points)");
        rewards.add("Fuel Surcharge Waiver - Small/Mid Vessels (250 points)");
        rewards.add("Free Photo/Video Capture Package (300 points)");
        rewards.add("Upgrade to Next Vessel Class - if available (400 points)");
        rewards.add("15% Rental Discount - Any Vessel, 1 day (500 points)");
        rewards.add("Extended Cruise Time +1 Hour - Fuel included (600 points)");
        rewards.add("VIP Event Pack - Décor, Sound, Lighting (800 points)");
        rewards.add("Exclusive Private Island Picnic/Fishing Spot Access (1,000 points)");
        return rewards;
    }

    public void displayRewardsMenu(String customerId) {
        LoyaltyAccount account = loyaltyAccounts.get(customerId);
        System.out.println("\n==================== REWARDS MENU ====================");
        List<String> rewards = getAvailableRewards();
        for (int i = 0; i < rewards.size(); i++) {
            System.out.println((i + 1) + ". " + rewards.get(i));
        }
        if (account != null && account.isVipMember()) {
        System.out.println("\nVIP Benefits (after 5 rentals OR 3,000 pts):");
        System.out.println("+15% points on each rental");
        System.out.println("Extra 7% off all bookings");
        System.out.println("Priority booking during peak seasons");
        System.out.println("Birthday-month bonus: 500 pts");
        System.out.println("=====================================================================\n");
     } else {
    System.out.println("You are not a VIP yet. Earn more points or complete more rentals to unlock benefits.");}
}


     public boolean isEligibleForVip(LoyaltyAccount account) {
     return !account.isVipMember() && 
           (account.getCurrentPoints() >= VIP_THRESHOLD_POINTS || 
            account.getTotalRentals() >= VIP_THRESHOLD_RENTALS);
    }


    public void expirePoints(String customerId, int daysOld) {
        LoyaltyAccount account = loyaltyAccounts.get(customerId);
        if (account != null) {
            // Example: expire points older than specified days
            System.out.println("Point expiration check for " + account.getCustomerName());
        }
    }

    public int getTotalTransactions() {
        return transactions.size();
    }

    public double getAveragePointsPerTransaction() {
        if (transactions.isEmpty()) return 0.0;
        
        double totalPoints = transactions.stream()
                .mapToInt(LoyaltyTransaction::getPointsChange)
                .sum();
        
        return totalPoints / transactions.size();
    }
}

