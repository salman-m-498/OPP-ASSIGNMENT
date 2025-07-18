package com.rentalapp;

import java.util.Scanner;
import com.rentalapp.vehicle.VehicleManager;


/**
 * App class - Handles all menu operations and user interactions
 */
public class App {
    private Scanner scanner;
    private boolean running;
    private Object currentUser; // Using Object temporarily until User classes are properly defined
    private VehicleManager vehicleManager;

    public App() {
        this.scanner = new Scanner(System.in);
        this.running = true;
        this.currentUser = null;
        this.vehicleManager = new VehicleManager();
    }
    
    /**
     * Start the application
     */
    public void start() {
        while (running) {
            showMainMenu();
        }
        scanner.close();
    }
    
    /**
     * Display the main menu
     */
    public void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Please select an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                showLoginMenu();
                break;
            case 2:
                handleRegistration();
                break;
            case 3:
                System.out.println("Thank you for using Car Rental System!");
                running = false;
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    /**
     * Display the login menu
     */
    public void showLoginMenu() {
        System.out.println("\n=== LOGIN MENU ===");
        System.out.println("1. Admin Login");
        System.out.println("2. Customer Login");
        System.out.println("3. Back to Main Menu");
        System.out.print("Please select an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                handleAdminLogin();
                break;
            case 2:
                handleCustomerLogin();
                break;
            case 3:
                return; // Go back to main menu
            default:
                System.out.println("Invalid option. Please try again.");
                showLoginMenu();
        }
    }
    
    /**
     * Display the admin menu
     */
    public void showAdminMenu() {
        while (currentUser != null && currentUser.getClass().getSimpleName().equals("Admin")) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Manage Vehicles");
            System.out.println("2. View All Rentals");
            System.out.println("3. Manage Customers");
            System.out.println("4. Generate Reports");
            System.out.println("5. System Settings");
            System.out.println("6. Logout");
            System.out.print("Please select an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    handleVehicleManagement();
                    break;
                case 2:
                    handleViewAllRentals();
                    break;
                case 3:
                    handleCustomerManagement();
                    break;
                case 4:
                    handleReportGeneration();
                    break;
                case 5:
                    handleSystemSettings();
                    break;
                case 6:
                    logout();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    /**
     * Display the customer menu
     */
    public void showCustomerMenu() {
        while (currentUser != null && currentUser.getClass().getSimpleName().contains("Customer")) {
            System.out.println("\n=== CUSTOMER MENU ===");
            System.out.println("1. Rental Services");
            System.out.println("2. Account Management");
            System.out.println("3. View Rental History");
            System.out.println("4. Logout");
            System.out.print("Please select an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    showRentalMenu();
                    break;
                case 2:
                    showAccountMenu();
                    break;
                case 3:
                    handleViewRentalHistory();
                    break;
                case 4:
                    logout();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    /**
     * Display the rental menu for customers
     */
    public void showRentalMenu() {
        System.out.println("\n=== RENTAL MENU ===");
        System.out.println("1. Browse Available Vehicles");
        System.out.println("2. Rent a Vehicle");
        System.out.println("3. Return a Vehicle");
        System.out.println("4. Extend Rental Period");
        System.out.println("5. Back to Customer Menu");
        System.out.print("Please select an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                handleBrowseVehicles();
                break;
            case 2:
                handleRentVehicle();
                break;
            case 3:
                handleReturnVehicle();
                break;
            case 4:
                handleExtendRental();
                break;
            case 5:
                return; // Go back to customer menu
            default:
                System.out.println("Invalid option. Please try again.");
                showRentalMenu();
        }
    }
    
    /**
     * Display the account menu for customers
     */
    public void showAccountMenu() {
        System.out.println("\n=== ACCOUNT MENU ===");
        System.out.println("1. View Profile");
        System.out.println("2. Update Profile");
        System.out.println("3. Change Password");
        System.out.println("4. View Loyalty Points");
        System.out.println("5. Payment Methods");
        System.out.println("6. Back to Customer Menu");
        System.out.print("Please select an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                handleViewProfile();
                break;
            case 2:
                handleUpdateProfile();
                break;
            case 3:
                handleChangePassword();
                break;
            case 4:
                handleViewLoyaltyPoints();
                break;
            case 5:
                handlePaymentMethods();
                break;
            case 6:
                return; // Go back to customer menu
            default:
                System.out.println("Invalid option. Please try again.");
                showAccountMenu();
        }
    }
    
    // Authentication Methods
    private void handleAdminLogin() {
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();
        
        // TODO: Implement actual authentication logic
        System.out.println("Admin login functionality to be implemented...");
        System.out.println("Username: " + username + " (authentication pending)");
        // For now, simulate successful login
        // currentUser = new Admin();
        // showAdminMenu();
    }
    
    private void handleCustomerLogin() {
        System.out.print("Enter customer username: ");
        String username = scanner.nextLine();
        System.out.print("Enter customer password: ");
        String password = scanner.nextLine();
        
        // TODO: Implement actual authentication logic
        System.out.println("Customer login functionality to be implemented...");
        System.out.println("Username: " + username + " (authentication pending)");
        // For now, simulate successful login
        // currentUser = new Customer();
        // showCustomerMenu();
    }
    
    private void handleRegistration() {
        System.out.println("Registration functionality to be implemented...");
        // TODO: Implement customer registration
    }
    
    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully!");
    }
    
    // Admin Menu Handlers
    private void handleVehicleManagement() {
        System.out.println("Vehicle management functionality to be implemented...");
    }
    
    private void handleViewAllRentals() {
        System.out.println("View all rentals functionality to be implemented...");
    }
    
    private void handleCustomerManagement() {
        System.out.println("Customer management functionality to be implemented...");
    }
    
    private void handleReportGeneration() {
        System.out.println("Report generation functionality to be implemented...");
    }
    
    private void handleSystemSettings() {
        System.out.println("System settings functionality to be implemented...");
    }
    
    // Customer Rental Menu Handlers
    private void handleBrowseVehicles() {
        System.out.println("\n=== BROWSE VEHICLES ===");
        System.out.println("1. Show All Available Vehicles");
        System.out.println("2. Filter by Category (Economy/Luxury)");
        System.out.println("3. Filter by Type (SUV, Sedan, etc.)");
        System.out.println("4. Filter by Minimum Seat Count");
        System.out.println("5. Back");
        System.out.print("Choose option: ");

        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
        case 1:
            vehicleManager.displayVehicles(vehicleManager.getAvailableVehicles());
            break;
        case 2:
            System.out.print("Enter category (Economy/Luxury): ");
            String cat = scanner.nextLine();
            vehicleManager.displayVehicles(vehicleManager.getVehiclesByCategory(cat));
            break;
        case 3:
            System.out.print("Enter type (SUV, Sedan, etc.): ");
            String type = scanner.nextLine();
            vehicleManager.displayVehicles(vehicleManager.getVehiclesByType(type));
            break;
        case 4:
            System.out.print("Enter minimum number of seats: ");
            int seats = scanner.nextInt();
            scanner.nextLine();
            vehicleManager.displayVehicles(vehicleManager.getVehiclesBySeats(seats));
            break;
        case 5:
            return;
        default:
            System.out.println("Invalid option.");
    }
    }
    
    private void handleRentVehicle() {
        System.out.println("Rent vehicle functionality to be implemented...");
    }
    
    private void handleReturnVehicle() {
        System.out.println("Return vehicle functionality to be implemented...");
    }
    
    private void handleExtendRental() {
        System.out.println("Extend rental functionality to be implemented...");
    }
    
    private void handleViewRentalHistory() {
        System.out.println("View rental history functionality to be implemented...");
    }
    
    // Customer Account Menu Handlers
    private void handleViewProfile() {
        System.out.println("View profile functionality to be implemented...");
    }
    
    private void handleUpdateProfile() {
        System.out.println("Update profile functionality to be implemented...");
    }
    
    private void handleChangePassword() {
        System.out.println("Change password functionality to be implemented...");
    }
    
    private void handleViewLoyaltyPoints() {
        System.out.println("View loyalty points functionality to be implemented...");
    }
    
    private void handlePaymentMethods() {
        System.out.println("Payment methods functionality to be implemented...");
    }
}
