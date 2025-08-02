package com.rentalapp.auth;

import com.rentalapp.rental.RentalController;
import com.rentalapp.rental.RentalService;
import com.rentalapp.payment.PaymentManager;
import com.rentalapp.vehicle.VehicleManager;
import com.rentalapp.maintenance.MaintenanceManager;
import com.rentalapp.loyalty.LoyaltyPointManager;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class DashboardManager {
    private Scanner scanner;
    private RentalController rentalController;
    private VehicleManager vehicleManager;
    private PaymentManager paymentManager;
    private RentalService rentalService;
    
    public DashboardManager() {
        this.scanner = new Scanner(System.in);
        
        // Initialize managers
        this.vehicleManager = new VehicleManager();
        LoyaltyPointManager loyaltyPointManager = new LoyaltyPointManager();
        this.paymentManager = new PaymentManager(loyaltyPointManager);
        MaintenanceManager maintenanceManager = new MaintenanceManager();
        this.rentalService = new RentalService(vehicleManager, maintenanceManager);
        
        // Initialize rental controller
        this.rentalController = new RentalController(scanner, rentalService, paymentManager, vehicleManager);
    }
    
    /**
     * Show appropriate dashboard based on user type
     */
    public void showDashboard(User user) {
        if (user instanceof Admin) {
            showAdminDashboard((Admin) user);
        } else if (user instanceof MemberCustomer) {
            showMemberCustomerDashboard((MemberCustomer) user);
        } else if (user instanceof NonMemberCustomer) {
            showNonMemberCustomerDashboard((NonMemberCustomer) user);
        }
    }
    
    /**
     * Admin Dashboard
     */
    private void showAdminDashboard(Admin admin) {
        boolean running = true;
        
        while (running) {
            clearScreen();
            printAdminHeader(admin);
            
            System.out.println("┌─────────────────────────────────────────────────────────┐");
            System.out.println("│                     ADMIN DASHBOARD                    │");
            System.out.println("├─────────────────────────────────────────────────────────┤");
            System.out.println("│ 1. Vehicle Management                                  │");
            System.out.println("│ 2. View All Rentals                                    │");
            System.out.println("│ 3. Customer Management                                 │");
            System.out.println("│ 4. Generate Reports                                    │");
            System.out.println("│ 5. System Settings                                     │");
            System.out.println("│ 6. View Admin Profile                                  │");
            System.out.println("│ 7. Change Password                                     │");
            System.out.println("│ 8. Logout                                              │");
            System.out.println("└─────────────────────────────────────────────────────────┘");
            System.out.print("Choose option (1-8): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    handleVehicleManagement();
                    break;
                case "2":
                    handleViewAllRentals();
                    break;
                case "3":
                    handleCustomerManagement();
                    break;
                case "4":
                    handleReportGeneration();
                    break;
                case "5":
                    handleSystemSettings();
                    break;
                case "6":
                    showAdminProfile(admin);
                    break;
                case "7":
                    handleChangePassword();
                    break;
                case "8":
                    running = false;
                    break;
                default:
                    showError("Invalid option! Please choose 1-8.");
            }
        }
    }
    
    /**
     * Member Customer Dashboard
     */
    private void showMemberCustomerDashboard(MemberCustomer customer) {
        boolean running = true;
        
        while (running) {
            clearScreen();
            printMemberHeader(customer);
            
            System.out.println("┌─────────────────────────────────────────────────────────┐");
            System.out.println("│                  MEMBER DASHBOARD                       │");
            System.out.println("├─────────────────────────────────────────────────────────┤");
            System.out.println("│ 1. Browse & Rent Vehicles                              │");
            System.out.println("│ 2. My Rental History                                   │");
            System.out.println("│ 3. View Loyalty Points & Benefits                      │");
            System.out.println("│ 4. Account Management                                  │");
            System.out.println("│ 5. Payment Methods                                     │");
            System.out.println("│ 6. Customer Support                                    │");
            System.out.println("│ 7. Change Password                                     │");
            System.out.println("│ 8. Logout                                              │");
            System.out.println("└─────────────────────────────────────────────────────────┘");
            System.out.print("Choose option (1-8): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    handleBrowseAndRentVehicles(customer);
                    break;
                case "2":
                    rentalController.viewRentalHistory(customer);
                    break;
                case "3":
                    showLoyaltyInfo(customer);
                    break;
                case "4":
                    handleAccountManagement();
                    break;
                case "5":
                    handlePaymentMethods();
                    break;
                case "6":
                    handleCustomerSupport();
                    break;
                case "7":
                    handleChangePassword();
                    break;
                case "8":
                    running = false;
                    break;
                default:
                    showError("Invalid option! Please choose 1-8.");
            }
        }
    }
    
    /**
     * Non-Member Customer Dashboard
     */
    private void showNonMemberCustomerDashboard(NonMemberCustomer customer) {
        boolean running = true;
        
        while (running) {
            clearScreen();
            printNonMemberHeader(customer);
            
            System.out.println("┌─────────────────────────────────────────────────────────┐");
            System.out.println("│                NON-MEMBER DASHBOARD                     │");
            System.out.println("├─────────────────────────────────────────────────────────┤");
            System.out.println("│ 1. Browse & Rent Vehicles                              │");
            System.out.println("│ 2. My Rental History                                   │");
            System.out.println("│ 3. Upgrade to Membership                               │");
            System.out.println("│ 4. Account Management                                  │");
            System.out.println("│ 5. Payment Methods                                     │");
            System.out.println("│ 6. Customer Support                                    │");
            System.out.println("│ 7. Change Password                                     │");
            System.out.println("│ 8. Logout                                              │");
            System.out.println("└─────────────────────────────────────────────────────────┘");
            System.out.print("Choose option (1-8): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    handleBrowseAndRentVehicles(customer);
                    break;
                case "2":
                    rentalController.viewRentalHistory(customer);
                    break;
                case "3":
                    handleMembershipUpgrade(customer);
                    break;
                case "4":
                    handleAccountManagement();
                    break;
                case "5":
                    handlePaymentMethods();
                    break;
                case "6":
                    handleCustomerSupport();
                    break;
                case "7":
                    handleChangePassword();
                    break;
                case "8":
                    running = false;
                    break;
                default:
                    showError("Invalid option! Please choose 1-8.");
            }
        }
    }
    
    /**
     * Header display methods
     */
    private void printAdminHeader(Admin admin) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String lastLogin = admin.getLastLogin() != null ? 
            admin.getLastLogin().format(formatter) : "First login";
            
        System.out.println("═".repeat(60));
        System.out.println("ADMIN PANEL - " + admin.getName());
        System.out.println("Department: " + admin.getDepartment() + " | ID: " + admin.getAdminId());
        System.out.println("Last Login: " + lastLogin);
        System.out.println("═".repeat(60));
    }
    
    private void printMemberHeader(MemberCustomer customer) {
        System.out.println("═".repeat(60));
        System.out.println("MEMBER DASHBOARD - " + customer.getName());
        System.out.println("Membership: " + customer.getMembershipTier() + 
                          " | Points: " + customer.getLoyaltyPoints());
        System.out.println("Discount Rate: " + (customer.getDiscountRate() * 100) + 
                          "% | Total Spent: $" + String.format("%.2f", customer.getTotalSpent()));
        System.out.println("═".repeat(60));
    }
    
    private void printNonMemberHeader(NonMemberCustomer customer) {
        System.out.println("═".repeat(60));
        System.out.println("CUSTOMER DASHBOARD - " + customer.getName());
        System.out.println("Account Type: Non-Member | Total Spent: $" + 
                          String.format("%.2f", customer.getTotalSpent()));
        if (customer.isEligibleForMembership()) {
            System.out.println("You're eligible for membership! Upgrade to earn rewards!");
        }
        System.out.println("═".repeat(60));
    }
    
    /**
     * Handler methods for various functionalities
     */
    private void handleVehicleManagement() {
        showLoadingMessage("Loading Vehicle Management");
        showInfo("Vehicle Management functionality will be implemented in VehicleManager.");
        pauseForUser();
    }
    
    private void handleViewAllRentals() {
        showLoadingMessage("Loading All Rentals");
        showInfo("View All Rentals functionality will be implemented in RentalManager.");
        pauseForUser();
    }
    
    private void handleCustomerManagement() {
        showLoadingMessage("Loading Customer Management");
        showInfo("Customer Management functionality will be implemented.");
        pauseForUser();
    }
    
    private void handleReportGeneration() {
        showLoadingMessage("Loading Report Generator");
        showInfo("Report Generation functionality will be implemented.");
        pauseForUser();
    }
    
    private void handleSystemSettings() {
        showLoadingMessage("Loading System Settings");
        showInfo("System Settings functionality will be implemented.");
        pauseForUser();
    }
    
    private void handleBrowseAndRentVehicles(Customer customer) {
        boolean running = true;
        
        while (running) {
            clearScreen();
            System.out.println("┌─────────────────────────────────────────────────────────┐");
            System.out.println("│                   RENTAL OPERATIONS                    │");
            System.out.println("├─────────────────────────────────────────────────────────┤");
            System.out.println("│ 1. Browse Available Vehicles                           │");
            System.out.println("│ 2. Rent a Vehicle                                      │");
            System.out.println("│ 3. Return Vehicle                                      │");
            System.out.println("│ 4. Extend Rental                                       │");
            System.out.println("│ 5. View My Active Rentals                              │");
            System.out.println("│ 6. Back to Main Menu                                   │");
            System.out.println("└─────────────────────────────────────────────────────────┘");
            System.out.print("Choose option (1-6): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    showAvailableVehicles();
                    break;
                case "2":
                    rentalController.processNewRental(customer);
                    break;
                case "3":
                    rentalController.processRentalReturn(customer);
                    break;
                case "4":
                    rentalController.extendRental(customer);
                    break;
                case "5":
                    showActiveRentals(customer);
                    break;
                case "6":
                    running = false;
                    break;
                default:
                    showError("Invalid option! Please choose 1-6.");
            }
        }
    }
    
    private void showAvailableVehicles() {
        clearScreen();
        showLoadingMessage("Loading Available Vehicles");
        
        try {
            vehicleManager.displayVehicles(vehicleManager.getAvailableVehicles());
        } catch (Exception e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
        
        pauseForUser();
    }
    
    private void showActiveRentals(Customer customer) {
        clearScreen();
        System.out.println("═".repeat(60));
        System.out.println("MY ACTIVE RENTALS - " + customer.getName());
        System.out.println("═".repeat(60));
        
        try {
            rentalService.displayAllActiveRentals();
        } catch (Exception e) {
            System.out.println("Error loading active rentals: " + e.getMessage());
        }
        
        pauseForUser();
    }
    

    private void showLoyaltyInfo(MemberCustomer customer) {
        clearScreen();
        System.out.println("═".repeat(60));
        System.out.println("LOYALTY PROGRAM - " + customer.getName());
        System.out.println("═".repeat(60));
        
        System.out.println("Current Tier: " + customer.getMembershipTier());
        System.out.println("Loyalty Points: " + customer.getLoyaltyPoints());
        System.out.println("Discount Rate: " + (customer.getDiscountRate() * 100) + "%");
        System.out.println("Eligible for Promos: " + (customer.isEligibleForPromo() ? "Yes" : "No"));
        
        System.out.println("\nTIER BENEFITS:");
        System.out.println("Bronze (0-499 points): 5% discount");
        System.out.println("Silver (500-1999 points): 10% discount");
        System.out.println("Gold (2000-4999 points): 15% discount");
        System.out.println("Platinum (5000+ points): 20% discount");
        
        if (customer.getLoyaltyPoints() < 5000) {
            int pointsNeeded = getNextTierPoints(customer.getLoyaltyPoints()) - customer.getLoyaltyPoints();
            System.out.println("\n" + pointsNeeded + " more points to reach the next tier!");
        }
        
        pauseForUser();
    }
    
    private int getNextTierPoints(int currentPoints) {
        if (currentPoints < 500) return 500;
        if (currentPoints < 2000) return 2000;
        if (currentPoints < 5000) return 5000;
        return currentPoints; // Already at highest tier
    }
    
    private void handleMembershipUpgrade(NonMemberCustomer customer) {
        clearScreen();
        System.out.println("═".repeat(60));
        System.out.println("MEMBERSHIP UPGRADE");
        System.out.println("═".repeat(60));
        
        System.out.println("Benefits of becoming a member:");
        System.out.println("• Earn loyalty points on every rental");
        System.out.println("• Exclusive discounts up to 20%");
        System.out.println("• Priority customer support");
        System.out.println("• Special member-only promotions");
        System.out.println("• Early access to new vehicles");
        
        System.out.print("\nWould you like to upgrade to membership? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        
        if ("y".equals(choice) || "yes".equals(choice)) {
            showLoadingMessage("Processing membership upgrade");
            showSuccess("Membership upgrade functionality will be implemented!");
            showInfo("You will be able to convert your account to a member account.");
        } else {
            showInfo("Maybe next time! You can upgrade anytime from your dashboard.");
        }
        
        pauseForUser();
    }
    
    private void handleAccountManagement() {
        showLoadingMessage("Loading Account Management");
        showInfo("Account Management functionality will be implemented.");
        pauseForUser();
    }
    
    private void handlePaymentMethods() {
        showLoadingMessage("Loading Payment Methods");
        showInfo("Payment Methods functionality will be implemented.");
        pauseForUser();
    }
    
    private void handleCustomerSupport() {
        clearScreen();
        System.out.println("═".repeat(60));
        System.out.println("CUSTOMER SUPPORT");
        System.out.println("═".repeat(60));
        
        System.out.println("Car Rental Support Center");
        System.out.println("Phone: +1-800-RENTAL (1-800-736-8254)");
        System.out.println("Email: support@rentalapp.com");
        System.out.println("Hours: 24/7 Support Available");
        System.out.println("Live Chat: Available on our website");
        
        System.out.println("\nCommon Issues:");
        System.out.println("• Vehicle booking problems");
        System.out.println("• Payment and billing questions");
        System.out.println("• Account and membership issues");
        System.out.println("• Emergency roadside assistance");
        
        pauseForUser();
    }
    
    private void handleChangePassword() {
        showLoadingMessage("Loading Password Change");
        showInfo("Change Password functionality will be implemented in AuthenticationManager.");
        pauseForUser();
    }
    
    private void showAdminProfile(Admin admin) {
        clearScreen();
        System.out.println("═".repeat(60));
        System.out.println("ADMIN PROFILE");
        System.out.println("═".repeat(60));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        System.out.println("Name: " + admin.getName());
        System.out.println("Username: " + admin.getUsername());
        System.out.println("Email: " + admin.getEmail());
        System.out.println("Phone: " + admin.getPhone());
        System.out.println("Admin ID: " + admin.getAdminId());
        System.out.println("Department: " + admin.getDepartment());
        System.out.println("Access Level: " + admin.getAccessLevel());
        System.out.println("Account Created: " + admin.getDateCreated().format(formatter));
        System.out.println("Last Login: " + (admin.getLastLogin() != null ? 
                          admin.getLastLogin().format(formatter) : "First login"));
        System.out.println("Status: " + (admin.isActive() ? "Active" : "Inactive"));
        
        pauseForUser();
    }
    
    /**
     * Utility methods for UI
     */
    private void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    private void showLoadingMessage(String message) {
        System.out.print("\n" + message);
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);
                System.out.print(".");
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
    
    private void showError(String message) {
        System.out.println("\n" + message);
        pauseForUser();
    }
    
    private void showInfo(String message) {
        System.out.println("\n" + message);
    }
    
    private void showSuccess(String message) {
        System.out.println("\n" + message);
    }
}
