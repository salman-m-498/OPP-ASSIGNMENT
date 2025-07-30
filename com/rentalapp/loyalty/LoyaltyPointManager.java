package com.rentalapp.loyalty;

import com.rentalapp.auth.Customer;
import java.time.LocalDateTime;
import java.util.*;

public class LoyaltyPointManager {
    private Map<String, LoyaltyAccount> loyaltyAccounts;
    private List<LoyaltyTransaction> transactions;
    private int transactionIdCounter;
    
    // Constants for loyalty system
    private static final int VIP_THRESHOLD = 500;
    private static final int REVIEW_BONUS_POINTS = 25;
    
    public LoyaltyPointManager() {
        this.loyaltyAccounts = new HashMap<>();
        this.transactions = new ArrayList<>();
        this.transactionIdCounter = 1;
    }

    public LoyaltyAccount createLoyaltyAccount(String customerId, String customerName) {
        LoyaltyAccount account = new LoyaltyAccount(customerId, customerName);
        loyaltyAccounts.put(customerId, account);
        return account;
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

        // Check for VIP upgrade
        checkVipUpgrade(account);

        System.out.println("Added " + points + " points to " + account.getCustomerName() + 
                          ". Total: " + account.getCurrentPoints());
        
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
        System.out.println("Member Status: " + (account.isVipMember() ? "VIP MEMBER" : "STANDARD MEMBER"));
        System.out.println("Account Created: " + account.getAccountCreatedDate().toLocalDate());
        
        if (!account.isVipMember()) {
            int pointsToVip = VIP_THRESHOLD - account.getCurrentPoints();
            if (pointsToVip > 0) {
                System.out.println("Points to VIP: " + pointsToVip);
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
        System.out.printf("%-15s %-20s %-10s %-15s %-10s%n", 
                         "Customer ID", "Name", "Points", "Lifetime", "Status");
        System.out.println("-".repeat(70));
        
        for (LoyaltyAccount account : loyaltyAccounts.values()) {
            System.out.printf("%-15s %-20s %-10d %-15d %-10s%n",
                             account.getCustomerId(),
                             account.getCustomerName(),
                             account.getCurrentPoints(),
                             account.isVipMember() ? "VIP" : "STANDARD");
        }
        System.out.println("===============================================================\n");
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
        
        for (LoyaltyAccount account : loyaltyAccounts.values()) {
            if (account.isVipMember()) vipMembers++;
            totalPoints += account.getCurrentPoints();
        }
        
        stats.put("TOTAL_MEMBERS", totalMembers);
        stats.put("VIP_MEMBERS", vipMembers);
        stats.put("STANDARD_MEMBERS", totalMembers - vipMembers);
        stats.put("TOTAL_POINTS_CIRCULATING", totalPoints);
        stats.put("AVERAGE_POINTS_PER_MEMBER", totalMembers > 0 ? totalPoints / totalMembers : 0);
        
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
        System.out.println("Average Points per Member: " + stats.get("AVERAGE_POINTS_PER_MEMBER"));
        
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
        rewards.add("Free Car Wash (50 points)");
        rewards.add("1 Day Rental Discount 10% (100 points)");
        rewards.add("Free GPS Navigation (75 points)");
        rewards.add("Baby Car Seat (25 points)");
        rewards.add("Extended Insurance Coverage (150 points)");
        rewards.add("Priority Customer Support (200 points)");
        rewards.add("Free Vehicle Upgrade (300 points)");
        rewards.add("2 Day Rental Discount 15% (400 points)");
        rewards.add("VIP Lounge Access (500 points)");
        return rewards;
    }

    public void displayRewardsMenu() {
        System.out.println("\n==================== REWARDS MENU ====================");
        List<String> rewards = getAvailableRewards();
        for (int i = 0; i < rewards.size(); i++) {
            System.out.println((i + 1) + ". " + rewards.get(i));
        }
        System.out.println("======================================================\n");
    }

    private void checkVipUpgrade(LoyaltyAccount account) {
        if (!account.isVipMember() && account.getCurrentPoints() >= VIP_THRESHOLD) {
            account.upgradeToVip();
            System.out.println("🎉 Congratulations! " + account.getCustomerName() + 
                             " has been upgraded to VIP Member!");
        }
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
