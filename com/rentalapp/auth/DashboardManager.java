package com.rentalapp.auth;

import com.rentalapp.rental.RentalController;
import com.rentalapp.rental.RentalHistory;
import com.rentalapp.rental.RentalService;
import com.rentalapp.payment.PaymentCalculator;
import com.rentalapp.payment.PaymentManager;
import com.rentalapp.payment.PaymentSummary;
import com.rentalapp.payment.Receipt;
import com.rentalapp.vessel.Vessel;
import com.rentalapp.vessel.VesselManager;
import com.rentalapp.maintenance.MaintenanceManager;
import com.rentalapp.maintenance.MaintenanceRecord;
import com.rentalapp.maintenance.MaintenanceStatus;
import com.rentalapp.maintenance.MaintenanceType;
import com.rentalapp.loyalty.LoyaltyAccount;
import com.rentalapp.loyalty.LoyaltyPointManager;
import java.time.format.DateTimeFormatter;
import com.rentalapp.review.ReviewManager;
import com.rentalapp.utils.VesselDataLoader;
import com.rentalapp.review.Review;
import com.rentalapp.rental.RentalRecord;
import com.rentalapp.rental.RentalManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;

public class DashboardManager {
    private Scanner scanner;
    private RentalController rentalController;
    private VesselManager  vesselManager;
    private PaymentManager paymentManager;
    private RentalService rentalService;
    private LoyaltyPointManager loyaltyPointManager;
    private ReviewManager reviewManager;
    private MaintenanceManager maintenanceManager;
    private RentalManager rentalManager;
    private final AuthenticationManager authManager;
    private PaymentCalculator paymentCalculator;
    private RentalHistory rentalHistory;

    
    public DashboardManager(AuthenticationManager authManager) {
        this.authManager = authManager;
        this.scanner = new Scanner(System.in);
        
        // Initialize managers
        this.vesselManager = new VesselManager();
        this.loyaltyPointManager = new LoyaltyPointManager();
        this.paymentManager = new PaymentManager(this.loyaltyPointManager);
        this.maintenanceManager = new MaintenanceManager();
        this.rentalHistory = new RentalHistory();
        this.reviewManager = new ReviewManager(loyaltyPointManager);
        this.paymentCalculator = new PaymentCalculator();
        this.rentalService = new RentalService(vesselManager, maintenanceManager, rentalHistory);
        this.rentalManager = new RentalManager(rentalService,rentalHistory,paymentCalculator,paymentManager,loyaltyPointManager,maintenanceManager);
        
        // Initialize rental controller
       this.rentalController = new RentalController(
    rentalService,
    paymentManager,
    paymentCalculator,
    vesselManager,   // vesselManager
    rentalManager,
    loyaltyPointManager   // rentalManager
);
    
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
            System.out.println("│                     ADMIN DASHBOARD                     │");
            System.out.println("├─────────────────────────────────────────────────────────┤");
            System.out.println("│ 1. Vessel Management                                    │");
            System.out.println("│ 2. View All Rentals                                     │");
            System.out.println("│ 3. Customer Management                                  │");
            System.out.println("│ 4. Generate Reports                                     │");
            System.out.println("│ 5. View Admin Profile                                   │");
            System.out.println("│ 6. View Reviews                                         │");
            System.out.println("│ 7. Change Password                                      │");
            System.out.println("│ 8. Maintenance Management                               │");
            System.out.println("│ 9. Logout                                               │");
            System.out.println("└─────────────────────────────────────────────────────────┘");
            System.out.print("Choose option (1-9): ");
        
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    handleVesselManagement();
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
                    showAdminProfile(admin);
                    break;
                case "6":
                    handleReviews();
                    break;
                case "7":
                    handleChangePassword();
                    break;
                 case "8":
                    handleMaintenanceManagement();
                    break;
                case "9":
                    running = false;
                    break;
                default:
                    showError("Invalid option! Please choose 1-9.");
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
            System.out.println("│ 1. Browse & Rent Vessels                                │");
            System.out.println("│ 2. My Rental History                                    │");
            System.out.println("│ 3. Leave a Review                                       │");
            System.out.println("│ 4. View Loyalty Points & Rewards                        │");
            System.out.println("│ 5. Account Management                                   │");
            System.out.println("│ 6. Payment Management                                   │");
            System.out.println("│ 7. Customer Supports                                    │");
            System.out.println("│ 8. Change Password                                      │");
            System.out.println("│ 9. Logout                                               │");
            System.out.println("└─────────────────────────────────────────────────────────┘");
            System.out.print("Choose option (1-9): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    handleBrowseAndRentVessels(customer);
                    break;
                case "2":
                    rentalController.viewRentalHistory(customer);
                    break;
                case "3":
                    handleLeaveReview(customer);
                    break;
                case "4":
                    showLoyaltyInfo(customer);
                    break;
                case "5":
                    handleAccountManagement();
                    break;
                case "6":
                    handlePaymentMethods(customer);
                    break;
                case "7":
                    handleCustomerSupport();
                    break;
                case "8":
                    handleChangePassword();
                    break;
                case "9":
                    running = false;
                    break;
                default:
                    showError("Invalid option! Please choose 1-9.");
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
            System.out.println("│ 1. Browse & Rent Vessels                                │");
            System.out.println("│ 2. My Rental History                                    │");
            System.out.println("│ 3. Leave a Review                                       │");
            System.out.println("│ 4. Upgrade to Membership                                │");
            System.out.println("│ 5. Account Management                                   │");
            System.out.println("│ 6. Payment Management                                   │");
            System.out.println("│ 7. Customer Support                                     │");
            System.out.println("│ 8. Change Password                                      │");
            System.out.println("│ 9. Logout                                               │");
            System.out.println("└─────────────────────────────────────────────────────────┘");
            System.out.print("Choose option (1-9): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    handleBrowseAndRentVessels(customer);
                    break;
                case "2":
                    rentalController.viewRentalHistory(customer);
                    break;
                case "3":
                    handleLeaveReview(customer);
                    break;
                case "4":
                    handleMembershipUpgrade(customer);
                    break;
                case "5":
                    handleAccountManagement();
                    break;
                case "6":
                    handlePaymentMethods(customer);
                    break;
                case "7":
                    handleCustomerSupport();
                    break;
                case "8":
                    handleChangePassword();
                    break;
                case "9":
                    running = false;
                    break;
                default:
                    showError("Invalid option! Please choose 1-9.");
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
        int points = loyaltyPointManager.getCustomerPoints(customer.getCustomerId());
        System.out.println("═".repeat(60));
        System.out.println("MEMBER DASHBOARD - " + customer.getName());
        System.out.println("Membership: " + customer.getMembershipTier() + 
                          " | Points: " + points);
        System.out.println("Discount Rate: " + (customer.getDiscountRate() * 100) + 
                          "% | Total Spent: RM" + String.format("%.2f", customer.getTotalSpent()));
        System.out.println("═".repeat(60));
    }
    
    private void printNonMemberHeader(NonMemberCustomer customer) {
        System.out.println("═".repeat(60));
        System.out.println("CUSTOMER DASHBOARD - " + customer.getName());
        System.out.println("Account Type: Non-Member | Total Spent: RM" + 
                          String.format("%.2f", customer.getTotalSpent()));
        System.out.println("═".repeat(60));
    }
    
    /**
     * Handler methods for various functionalities
     */
   private void handleVesselManagement() {
    boolean running = true;
    while (running) {
        clearScreen();
        printHeader("VESSEL MANAGEMENT");

        System.out.println("┌────────────────────────────────────────────────┐");
        System.out.println("│            VESSEL MANAGEMENT                   │");
        System.out.println("├────────────────────────────────────────────────┤");
        System.out.println("│ 1. View All Vessels                            │");
        System.out.println("│ 2. Add New Vessel                              │");
        System.out.println("│ 3. Update Vessel                               │");
        System.out.println("│ 4. Delete Vessel                               │");
        System.out.println("│ 5. View Popular Vessels                        │");
        System.out.println("│ 6. Back to Dashboard                           │");
        System.out.println("└────────────────────────────────────────────────┘");

        System.out.print("\nChoose option (1-6): ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                vesselManager.displayVessels(vesselManager.getAllVessels());
                pauseForUser();
                break;
            case "2":
                addNewVessel();
                break;
            case "3":
                updateVessel();
                break;
            case "4":
                deleteVessel();
                break;
            case "5":
                vesselManager.displayPopularVessels();
                pauseForUser();
                break;
            case "6":
                running = false;
                break;
            default:
                showError("Invalid option! Please choose 1-6.");
        }
    }
}
    
    private void handleViewAllRentals() {
        clearScreen();
        printHeader("ALL RENTALS");

        List<RentalRecord> allRentals = rentalController.getAllRentals();

    if (allRentals.isEmpty()) {
        System.out.println("No rental records found.");
    } else {
        for (RentalRecord r : allRentals) {
            System.out.printf("Rental ID: %s | Customer: %s | Vessel: %s | Status: %s%n",
                    r.getRentalId(), r.getCustomerName(), r.getVesselType(), r.getStatus());
        }
        System.out.println("=====================================================\n");
    }
    pauseForUser();
}

    
    private void handleCustomerManagement() {
    boolean managing = true;

    while (managing) {
        clearScreen();
        System.out.println("┌──────────────────────────────────────────────┐");
        System.out.println("│              CUSTOMER MANAGEMENT             │");
        System.out.println("├──────────────────────────────────────────────┤");
        System.out.println("│ 1. View All Customers                        │");
        System.out.println("│ 2. Search Customer                           │");
        System.out.println("│ 3. Activate Customer Account                 │");
        System.out.println("│ 4. Deactivate Customer Account               │");
        System.out.println("│ 5. Delete Customer Account                   │");
        System.out.println("│ 6. Upgrade Non-Member to Member              │");
        System.out.println("│ 7. Back to Admin Dashboard                   │");
        System.out.println("└──────────────────────────────────────────────┘");
        System.out.print("Choose option (1-7): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                viewAllCustomers();
                break;
            case "2":
                searchCustomer();
                break;
            case "3":
                activateCustomer();
                break;
            case "4":
                deactivateCustomer();
                break;
            case "5":
                deleteCustomer();
                break;
            case "6":
                upgradeToMember();
                break;
            case "7":
                managing = false;
                break;
            default:
                showError("Invalid option! Please choose 1-7.");
        }
    }
}

    
    private void handleReportGeneration() {
    boolean reporting = true;

    while (reporting) {
        clearScreen();
        System.out.println("┌──────────────────────────────────────────────┐");
        System.out.println("│              REPORT GENERATION               │");
        System.out.println("├──────────────────────────────────────────────┤");
        System.out.println("│ 1. Loyalty Program Report                    │");
        System.out.println("│ 2. Maintenance Report                        │");
        System.out.println("│ 3. Payment Report                            │");
        System.out.println("│ 4. Rental Report                             │");
        System.out.println("│ 5. Back to Admin Dashboard                   │");
        System.out.println("└──────────────────────────────────────────────┘");
        System.out.print("Choose option (1-5): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                loyaltyPointManager.generateLoyaltyReport();
                break;
            case "2":
                maintenanceManager.generateMaintenanceReport();
                break;
            case "3":
                paymentManager.generateMonthlyReport();
                break;
            case "4":
                rentalManager.generateRentalReport();
                break;
            case "5":
                reporting = false;
                break;
            default:
                showError("Invalid option! Please choose 1-5.");
        }
        pauseForUser();
    }
}

     private void showAdminProfile(Admin admin) {
        clearScreen();
         printHeader("ADMIN PROFILE ");
        
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

    private void handleReviews() {
    boolean running = true;

    while (running) {
        clearScreen();
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.println("│              REVIEWS MENU               │");
        System.out.println("├─────────────────────────────────────────┤");
        System.out.println("│ 1. View Aggregated Reviews (by Vessel)  │");
        System.out.println("│ 2. View All Individual Reviews          │");
        System.out.println("│ 3. Back to Dashboard                    │");
        System.out.println("└─────────────────────────────────────────┘");
        System.out.print("Choose option (1-3): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                clearScreen();
                reviewManager.displayAggregatedReviews();
                pauseForUser();
                break;
            case "2":
                clearScreen();
                reviewManager.displayAllReviews();
                pauseForUser();
                break;
            case "3":
                running = false;
                break;
            default:
                showError("Invalid option! Please choose 1-3.");
        }
    }
}
private void handleMaintenanceManagement() {
    boolean managing = true;

    while (managing) {
        clearScreen();
        System.out.println("┌────────────────────────────────────────────────┐");
        System.out.println("│             MAINTENANCE MANAGEMENT             │");
        System.out.println("├────────────────────────────────────────────────┤");
        System.out.println("│ 1. View All Maintenance Records                │");
        System.out.println("│ 2. Schedule New Maintenance                    │");
        System.out.println("│ 3. Start Maintenance                           │");
        System.out.println("│ 4. Complete Maintenance                        │");
        System.out.println("│ 5. Cancel Maintenance                          │");
        System.out.println("│ 6. Reschedule Maintenance                      │");
        System.out.println("│ 7. View Upcoming Maintenance                   │");
        System.out.println("│ 8. View Overdue Maintenance                    │");
        System.out.println("│ 9. Back to Admin Dashboard                     │");
        System.out.println("└────────────────────────────────────────────────┘");
        System.out.print("Choose option (1-9): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                maintenanceManager.getAllMaintenanceRecords()
                        .forEach(MaintenanceRecord::printSummary);
                break;
            case "2":
                scheduleMaintenance();
                break;
            case "3":
                startMaintenance();
                break;
            case "4":
                completeMaintenance();
                break;
            case "5":
                cancelMaintenance();
                break;
            case "6":
                rescheduleMaintenance();
                break;
            case "7":
                maintenanceManager.displayUpcomingMaintenance();
                break;
            case "8":
                maintenanceManager.displayOverdueMaintenance();
                break;
            case "9":
                managing = false;
                continue;
            default:
                showError("Invalid option! Please choose 1-9.");
        }
        pauseForUser();
    }
}

private void scheduleMaintenance() {
    clearScreen();
    printHeader("SCHEDULE NEW MAINTENANCE");
    
    // First, show vessel filtering options
    List<Vessel> selectedVessels = displayVesselSelectionMenu();
    
    if (selectedVessels.isEmpty()) {
        System.out.println("No vessels to display. Returning to menu.");
        return;
    }
    
    // Display the filtered vessels
    System.out.println("\n" + "=".repeat(80));
    System.out.println("AVAILABLE VESSELS FOR MAINTENANCE");
    System.out.println("=".repeat(80));
    vesselManager.displayVessels(selectedVessels);
    
    // Now ask for vessel selection
    System.out.print("\nEnter Vessel ID from the list above: ");
    String vesselId = scanner.nextLine().trim();
    
    Vessel vessel = vesselManager.getVesselById(vesselId);
    if (vessel == null) {
        showError("Vessel not found. Please enter a valid Vessel ID from the list.");
        return;
    }
    
    // Verify the vessel is in the filtered list
    boolean vesselInList = selectedVessels.stream()
            .anyMatch(v -> v.getId().equals(vesselId));
    
    if (!vesselInList) {
        showError("Please select a vessel from the displayed list.");
        return;
    }
    
    // Show vessel maintenance history
    displayVesselMaintenanceInfo(vessel);
    
    // Proceed with maintenance scheduling
    scheduleMaintenanceForVessel(vessel);
}

private List<Vessel> displayVesselSelectionMenu() {
    System.out.println("\nHow would you like to filter vessels?");
    System.out.println("┌────────────────────────────────────────────┐");
    System.out.println("│           VESSEL FILTER OPTIONS            │");
    System.out.println("├────────────────────────────────────────────┤");
    System.out.println("│ 1. All Vessels                             │");
    System.out.println("│ 2. Available Vessels Only                  │");
    System.out.println("│ 3. Unavailable Vessels Only                │");
    System.out.println("│ 4. Filter by Category                      │");
    System.out.println("│ 5. Filter by Location                      │");
    System.out.println("│ 6. Vessels Due for Maintenance             │");
    System.out.println("└────────────────────────────────────────────┘");
    System.out.print("Choose filter option (1-6): ");
    
    String choice = scanner.nextLine().trim();
    
    switch (choice) {
        case "1":
            return vesselManager.getAllVessels();
        case "2":
            return vesselManager.getAvailableVessels();
        case "3":
            return vesselManager.getAllVessels().stream()
                    .filter(v -> !v.isAvailable())
                    .collect(Collectors.toList());
        case "4":
            return filterVesselsByCategory();
        case "5":
            return filterVesselsByLocation();
        case "6":
            return getVesselsDueForMaintenance();
        default:
            System.out.println("Invalid option, showing all vessels.");
            return vesselManager.getAllVessels();
    }
}

private List<Vessel> filterVesselsByCategory() {
    System.out.println("\nSelect vessel category:");
    System.out.println("1. Yacht");
    System.out.println("2. Boat");
    System.out.println("3. Pontoon");
    System.out.println("4. Jet Ski");
    System.out.println("5. Fishing Charter");
    System.out.print("Choose category (1-5): ");
    
    String categoryChoice = scanner.nextLine().trim();
    String category = getCategoryFromChoice(categoryChoice);
    
    if (category != null) {
        return vesselManager.getVesselsByCategory(category);
    } else {
        System.out.println("Invalid category, showing all vessels.");
        return vesselManager.getAllVessels();
    }
}

private List<Vessel> filterVesselsByLocation() {
    System.out.print("Enter location to filter by: ");
    String location = scanner.nextLine().trim();
    
    if (!location.isEmpty()) {
        return vesselManager.getVesselsByLocation(location);
    } else {
        return vesselManager.getAllVessels();
    }
}

private List<Vessel> getVesselsDueForMaintenance() {
    // Get vessels that haven't had maintenance recently or have high rental counts
    List<Vessel> allVessels = vesselManager.getAllVessels();
    List<Vessel> dueForMaintenance = new ArrayList<>();
    
    for (Vessel vessel : allVessels) {
        // Check if vessel has recent maintenance
        List<MaintenanceRecord> vesselMaintenance = maintenanceManager.getVesselMaintenance(vessel.getId());
        
        boolean needsMaintenance = false;
        
        // If no maintenance records, vessel needs maintenance
        if (vesselMaintenance.isEmpty()) {
            needsMaintenance = true;
        } else {
            // Check if last completed maintenance was more than 30 days ago
            LocalDate lastMaintenance = vesselMaintenance.stream()
                    .filter(r -> r.getStatus() == MaintenanceStatus.COMPLETED)
                    .map(MaintenanceRecord::getActualCompletionDate)
                    .filter(Objects::nonNull)
                    .max(LocalDate::compareTo)
                    .orElse(null);
            
            if (lastMaintenance == null || 
                java.time.temporal.ChronoUnit.DAYS.between(lastMaintenance, LocalDate.now()) > 30) {
                needsMaintenance = true;
            }
        }
        
        // Also check rental count (if vessel has high rental activity)
        if (vessel.getRentalCount() > 10) {
            needsMaintenance = true;
        }
        
        if (needsMaintenance) {
            dueForMaintenance.add(vessel);
        }
    }
    
    return dueForMaintenance;
}

private void displayVesselMaintenanceInfo(Vessel vessel) {
    System.out.println("\n" + "=".repeat(60));
    System.out.println("VESSEL MAINTENANCE INFORMATION");
    System.out.println("=".repeat(60));
    System.out.printf("Vessel: %s (%s)\n", vessel.getVesselType(), vessel.getId());
    System.out.printf("Location: %s\n", vessel.getLocation());
    System.out.printf("Capacity: %d\n", vessel.getCapacity());
    System.out.printf("Current Status: %s\n", vessel.isAvailable() ? "Available" : "Unavailable");
    System.out.printf("Total Rentals: %d\n", vessel.getRentalCount());
    
    // Show maintenance history summary
    List<MaintenanceRecord> vesselMaintenance = maintenanceManager.getVesselMaintenance(vessel.getId());
    System.out.printf("Total Maintenance Records: %d\n", vesselMaintenance.size());
    
    double totalCost = maintenanceManager.getMaintenanceCostByVessel(vessel.getId());
    System.out.printf("Total Maintenance Cost: RM%.2f\n", totalCost);
    
    // Show last maintenance date
    LocalDate lastMaintenance = vesselMaintenance.stream()
            .filter(r -> r.getStatus() == MaintenanceStatus.COMPLETED)
            .map(MaintenanceRecord::getActualCompletionDate)
            .filter(Objects::nonNull)
            .max(LocalDate::compareTo)
            .orElse(null);
    
    if (lastMaintenance != null) {
        long daysSinceLastMaintenance = java.time.temporal.ChronoUnit.DAYS.between(lastMaintenance, LocalDate.now());
        System.out.printf("Last Maintenance: %s (%d days ago)\n", lastMaintenance, daysSinceLastMaintenance);
    } else {
        System.out.println("Last Maintenance: No completed maintenance found");
    }
    
    // Show pending maintenance
    long pendingMaintenance = vesselMaintenance.stream()
            .filter(r -> r.getStatus() == MaintenanceStatus.SCHEDULED || r.getStatus() == MaintenanceStatus.IN_PROGRESS)
            .count();
    
    if (pendingMaintenance > 0) {
        System.out.printf("Pending Maintenance: %d record(s)\n", pendingMaintenance);
        
        // Show upcoming scheduled maintenance
        vesselMaintenance.stream()
                .filter(r -> r.getStatus() == MaintenanceStatus.SCHEDULED)
                .sorted(Comparator.comparing(MaintenanceRecord::getScheduledDate))
                .limit(3)
                .forEach(r -> System.out.printf("  - %s scheduled for %s\n", r.getType(), r.getScheduledDate()));
    }
    
    System.out.println("=".repeat(60));
}

private void scheduleMaintenanceForVessel(Vessel vessel) {
    System.out.println("\n" + "=".repeat(50));
    System.out.println("SCHEDULING MAINTENANCE FOR: " + vessel.getVesselType() + " (" + vessel.getId() + ")");
    System.out.println("=".repeat(50));
    
    System.out.println("\nSelect Maintenance Type:");
    MaintenanceType[] types = MaintenanceType.values();
    for (int i = 0; i < types.length; i++) {
        System.out.printf("%d. %s\n", i + 1, types[i]);
    }
    System.out.print("Choose maintenance type (1-" + types.length + "): ");
    
    String typeChoice = scanner.nextLine().trim();
    MaintenanceType type;
    
    try {
        int index = Integer.parseInt(typeChoice) - 1;
        if (index >= 0 && index < types.length) {
            type = types[index];
        } else {
            System.out.println("Invalid selection, using GENERAL_INSPECTION as default.");
            type = MaintenanceType.GENERAL_INSPECTION;
        }
    } catch (NumberFormatException e) {
        System.out.println("Invalid input, using GENERAL_INSPECTION as default.");
        type = MaintenanceType.GENERAL_INSPECTION;
    }

    System.out.print("\nEnter scheduled date (yyyy-MM-dd) or press Enter for tomorrow: ");
    String dateInput = scanner.nextLine().trim();
    LocalDate date;
    
    if (dateInput.isEmpty()) {
        date = LocalDate.now().plusDays(1);
        System.out.println("Using tomorrow's date: " + date);
    } else {
        date = readDateInput("Enter scheduled date (yyyy-MM-dd): ", dateInput);
    }

    System.out.print("Enter description (optional): ");
    String description = scanner.nextLine().trim();
    
    if (description.isEmpty()) {
        description = "Scheduled " + type.toString().toLowerCase().replace("_", " ") + " maintenance";
    }

    // Show confirmation
    System.out.println("\n" + "=".repeat(50));
    System.out.println("MAINTENANCE SCHEDULING CONFIRMATION");
    System.out.println("=".repeat(50));
    System.out.printf("Vessel: %s (%s)\n", vessel.getVesselType(), vessel.getId());
    System.out.printf("Maintenance Type: %s\n", type);
    System.out.printf("Scheduled Date: %s\n", date);
    System.out.printf("Description: %s\n", description);
    System.out.printf("Priority Level: %s\n", getPriorityForType(type));
    
    if (vessel.isAvailable()) {
        System.out.println("\nNote: Vessel will be marked as unavailable once maintenance is scheduled.");
    }
    
    System.out.print("\nConfirm scheduling? (y/n): ");
    String confirm = scanner.nextLine().trim().toLowerCase();
    
    if (confirm.startsWith("y")) {
        maintenanceManager.scheduleMaintenance(vessel.getId(), vessel.getVesselType(), type, date, description);
        vessel.setAvailable(false); // Mark vessel as unavailable
        System.out.println("\nMaintenance scheduled successfully!");
        System.out.println("Vessel marked as unavailable");
    } else {
        System.out.println("Maintenance scheduling cancelled.");
    }
}

private String getPriorityForType(MaintenanceType type) {
    switch (type) {
        case EMERGENCY_REPAIR:
            return "CRITICAL";
        case ENGINE_SERVICE:
        case SAFETY_EQUIPMENT_CHECK:
        case HULL_INSPECTION:
            return "HIGH";
        case ELECTRICAL_SYSTEM_CHECK:
        case PROPELLER_MAINTENANCE:
        case FUEL_SYSTEM_SERVICE:
            return "MEDIUM";
        case NAVIGATION_SYSTEM_CHECK:
        case DEEP_CLEANING:
        case GENERAL_INSPECTION:
            return "LOW";
        default:
            return "MEDIUM";
    }
}


private LocalDate readDateInput(String prompt, String input) {
    try {
        return LocalDate.parse(input);
    } catch (Exception e) {
        System.out.println("Invalid date format, using tomorrow's date.");
        return LocalDate.now().plusDays(1);
    }
}

private void startMaintenance() {
    clearScreen();
    printHeader("START MAINTENANCE");
    
    // Display scheduled maintenance records
    List<MaintenanceRecord> scheduledRecords = maintenanceManager.getMaintenanceByStatus(MaintenanceStatus.SCHEDULED);
    
    if (scheduledRecords.isEmpty()) {
        System.out.println("No scheduled maintenance records found.");
        pauseForUser();
        return;
    }
    
    System.out.println("SCHEDULED MAINTENANCE RECORDS:");
    System.out.println("=".repeat(75));
    System.out.printf("%-10s %-8s %-20s %-15s %-10s%n", 
                     "Record ID", "Vessel", "Type", "Maintenance", "Due Date");
    System.out.println("-".repeat(75));
    
    for (MaintenanceRecord record : scheduledRecords) {
        String maintenanceType = record.getType().toString().replace("_", " ");
        if (maintenanceType.length() > 14) {
            maintenanceType = maintenanceType.substring(0, 11) + "...";
        }
        
        System.out.printf("%-10s %-8s %-20s %-15s %-10s%n",
                         record.getRecordId(),
                         record.getVesselId(),
                         record.getVesselType(),
                         maintenanceType,
                         record.getScheduledDate());
    }
    System.out.println("=".repeat(75));
    
    System.out.print("\nEnter Record ID to start: ");
    String recordId = scanner.nextLine().trim();
    
    // Validate record ID exists in the displayed list
    boolean validId = scheduledRecords.stream()
            .anyMatch(r -> r.getRecordId().equals(recordId));
    
    if (!validId) {
        showError("Invalid Record ID. Please select from the displayed list.");
        return;
    }
    
    boolean success = maintenanceManager.startMaintenance(recordId);
    if (success) {
        System.out.println("\nMaintenance started successfully!");
        System.out.println("Status updated to IN_PROGRESS with today's date.");
    }
}

private void completeMaintenance() {
    clearScreen();
    printHeader("COMPLETE MAINTENANCE");
    
    // Display in-progress maintenance records
    List<MaintenanceRecord> inProgressRecords = maintenanceManager.getMaintenanceByStatus(MaintenanceStatus.IN_PROGRESS);
    
    if (inProgressRecords.isEmpty()) {
        System.out.println("No maintenance records currently in progress.");
        pauseForUser();
        return;
    }
    
    System.out.println("IN-PROGRESS MAINTENANCE RECORDS:");
    System.out.println("=".repeat(75));
    System.out.printf("%-10s %-8s %-20s %-15s %-10s%n", 
                     "Record ID", "Vessel", "Type", "Maintenance", "Started");
    System.out.println("-".repeat(75));
    
    for (MaintenanceRecord record : inProgressRecords) {
        String maintenanceType = record.getType().toString().replace("_", " ");
        if (maintenanceType.length() > 14) {
            maintenanceType = maintenanceType.substring(0, 11) + "...";
        }
        
        String startedDate = record.getActualStartDate() != null ? 
                           record.getActualStartDate().toString() : "N/A";
        
        System.out.printf("%-10s %-8s %-20s %-15s %-10s%n",
                         record.getRecordId(),
                         record.getVesselId(),
                         record.getVesselType(),
                         maintenanceType,
                         startedDate);
    }
    System.out.println("=".repeat(75));
    
    System.out.print("\nEnter Record ID to complete: ");
    String recordId = scanner.nextLine().trim();
    
    // Validate record ID exists in the displayed list
    boolean validId = inProgressRecords.stream()
            .anyMatch(r -> r.getRecordId().equals(recordId));
    
    if (!validId) {
        showError("Invalid Record ID. Please select from the displayed list.");
        return;
    }
    
    // Get completion details
    System.out.print("Enter cost (RM): ");
    double cost;
    try {
        cost = Double.parseDouble(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        showError("Invalid cost amount.");
        return;
    }
    
    System.out.print("Enter technician name: ");
    String technician = scanner.nextLine().trim();
    
    System.out.print("Enter completion notes: ");
    String notes = scanner.nextLine().trim();
    
    boolean success = maintenanceManager.completeMaintenance(recordId, cost, technician, notes);
    if (success) {
        System.out.println("\nMaintenance completed successfully!");
        
        // Ask if vessel should be made available again
        MaintenanceRecord record = inProgressRecords.stream()
                .filter(r -> r.getRecordId().equals(recordId))
                .findFirst()
                .orElse(null);
        
        if (record != null) {
            Vessel vessel = vesselManager.getVesselById(record.getVesselId());
            if (vessel != null && !vessel.isAvailable()) {
                System.out.print("Make vessel available for rental again? (y/n): ");
                String makeAvailable = scanner.nextLine().trim().toLowerCase();
                if (makeAvailable.startsWith("y")) {
                    vessel.setAvailable(true);
                    System.out.println("Vessel marked as available");
                }
            }
        }
    }
}

private void cancelMaintenance() {
    clearScreen();
    printHeader("CANCEL MAINTENANCE");
    
    // Display unfinished maintenance records (scheduled or in-progress only)
    List<MaintenanceRecord> unfinishedRecords = maintenanceManager.getAllMaintenanceRecords().stream()
            .filter(r -> r.getStatus() == MaintenanceStatus.SCHEDULED || r.getStatus() == MaintenanceStatus.IN_PROGRESS)
            .collect(Collectors.toList());
    
    if (unfinishedRecords.isEmpty()) {
        System.out.println("No unfinished maintenance records available for cancellation.");
        pauseForUser();
        return;
    }
    
    System.out.println("UNFINISHED MAINTENANCE RECORDS:");
    System.out.println("=".repeat(75));
    System.out.printf("%-10s %-8s %-20s %-15s %-10s%n", 
                     "Record ID", "Vessel", "Type", "Maintenance", "Status");
    System.out.println("-".repeat(75));
    
    for (MaintenanceRecord record : unfinishedRecords) {
        String maintenanceType = record.getType().toString().replace("_", " ");
        if (maintenanceType.length() > 14) {
            maintenanceType = maintenanceType.substring(0, 11) + "...";
        }
        
        String status = record.getStatus().toString().replace("_", " ");
        
        System.out.printf("%-10s %-8s %-20s %-15s %-10s%n",
                         record.getRecordId(),
                         record.getVesselId(),
                         record.getVesselType(),
                         maintenanceType,
                         status);
    }
    System.out.println("=".repeat(75));
    
    System.out.print("\nEnter Record ID to cancel: ");
    String recordId = scanner.nextLine().trim();
    
    // Validate record ID exists in the displayed list
    MaintenanceRecord recordToCancel = unfinishedRecords.stream()
            .filter(r -> r.getRecordId().equals(recordId))
            .findFirst()
            .orElse(null);
    
    if (recordToCancel == null) {
        showError("Invalid Record ID. Please select from the displayed list.");
        return;
    }
    
    System.out.print("Enter cancellation reason: ");
    String reason = scanner.nextLine().trim();
    
    if (reason.isEmpty()) {
        reason = "Cancelled by admin";
    }
    
    boolean success = maintenanceManager.cancelMaintenance(recordId, reason);
    if (success) {
        System.out.println("\nMaintenance cancelled successfully!");
        
        // Ask if vessel should be made available again if it's unavailable
        Vessel vessel = vesselManager.getVesselById(recordToCancel.getVesselId());
        if (vessel != null && !vessel.isAvailable()) {
            System.out.print("Make vessel available for rental again? (y/n): ");
            String makeAvailable = scanner.nextLine().trim().toLowerCase();
            if (makeAvailable.startsWith("y")) {
                vessel.setAvailable(true);
                System.out.println("Vessel marked as available");
            }
        }
    }
}

private void rescheduleMaintenance() {
    clearScreen();
    printHeader("RESCHEDULE MAINTENANCE");
    
    // Display only scheduled maintenance records (unfinished)
    List<MaintenanceRecord> scheduledRecords = maintenanceManager.getMaintenanceByStatus(MaintenanceStatus.SCHEDULED);
    
    if (scheduledRecords.isEmpty()) {
        System.out.println("No scheduled maintenance records found for rescheduling.");
        pauseForUser();
        return;
    }
    
    System.out.println("SCHEDULED MAINTENANCE RECORDS:");
    System.out.println("=".repeat(75));
    System.out.printf("%-10s %-8s %-20s %-15s %-10s%n", 
                     "Record ID", "Vessel", "Type", "Maintenance", "Due Date");
    System.out.println("-".repeat(75));
    
    for (MaintenanceRecord record : scheduledRecords) {
        String maintenanceType = record.getType().toString().replace("_", " ");
        if (maintenanceType.length() > 14) {
            maintenanceType = maintenanceType.substring(0, 11) + "...";
        }
        
        System.out.printf("%-10s %-8s %-20s %-15s %-10s%n",
                         record.getRecordId(),
                         record.getVesselId(),
                         record.getVesselType(),
                         maintenanceType,
                         record.getScheduledDate());
    }
    System.out.println("=".repeat(75));
    
    System.out.print("\nEnter Record ID to reschedule: ");
    String recordId = scanner.nextLine().trim();
    
    // Validate record ID exists in the displayed list
    MaintenanceRecord recordToReschedule = scheduledRecords.stream()
            .filter(r -> r.getRecordId().equals(recordId))
            .findFirst()
            .orElse(null);
    
    if (recordToReschedule == null) {
        showError("Invalid Record ID. Please select from the displayed list.");
        return;
    }
    
    System.out.printf("Current date: %s%n", recordToReschedule.getScheduledDate());
    System.out.print("Enter new date (yyyy-MM-dd): ");
    String newDateStr = scanner.nextLine().trim();
    
    LocalDate newDate;
    try {
        newDate = LocalDate.parse(newDateStr);
    } catch (Exception e) {
        showError("Invalid date format. Please use yyyy-MM-dd format.");
        return;
    }
    
    // Validate new date is not in the past
    LocalDate today = LocalDate.now();
    if (newDate.isBefore(today)) {
        System.out.print("Warning: New date is in the past. Continue anyway? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.startsWith("y")) {
            System.out.println("Rescheduling cancelled.");
            return;
        }
    }
    
    boolean success = maintenanceManager.rescheduleMaintenance(recordId, newDate);
    if (success) {
        System.out.println("\nMaintenance rescheduled successfully!");
        System.out.printf("Old date: %s -> New date: %s%n", 
                         recordToReschedule.getScheduledDate(), newDate);
    }
}


    /**
 * View all customers in the system.
   */

   private void printAllCustomers() {
    for (var entry : authManager.getAllUsers().entrySet()) {
        String username = entry.getKey();
        var user = entry.getValue();

        if (user instanceof Customer c) {
            System.out.println("ID: " + c.getCustomerId()
                + " | Username: " + username
                + " | Name: " + c.getName()
                + " | Member: " + (c instanceof MemberCustomer)
                + " | Active: " + c.isActive());
        }
    }
}

    private void viewAllCustomers() {
    clearScreen();
    System.out.println("─── All Customers ───");
    printAllCustomers();
    pauseForUser();
}
    /**
    * Search for a customer by username.
    */
private void searchCustomer() {
    clearScreen();
    System.out.println("─── All Customers ───");
    printAllCustomers();  

    System.out.print("Enter username to search: ");
    String username = scanner.nextLine().trim();

    var user = authManager.getUserByUsername(username);
    if (user instanceof Customer c) {
        System.out.println("─── Customer Details ───");
        System.out.println("ID: " + c.getCustomerId());
        System.out.println("Username: " + c.getUsername());
        System.out.println("Name: " + c.getName());
        System.out.println("Email: " + c.getEmail());
        System.out.println("Phone: " + c.getPhone());
        System.out.println("Type: " + (c instanceof MemberCustomer ? "Member" : "Non-Member"));
        System.out.println("Active: " + c.isActive());
    } else {
        System.out.println("Customer not found.");
    }
    pauseForUser();
}
    /**
    * Activate a customer account.
    */
    private void activateCustomer() {
    clearScreen();
    System.out.println("─── All Customers ───");
    printAllCustomers();  
    System.out.print("Enter username to activate: ");
    String username = scanner.nextLine().trim();

    if (authManager.activateUser(username)) {
        System.out.println("Customer account activated.");
    } else {
        System.out.println("Activation failed. Customer not found.");
    }
    pauseForUser();
}

    /**
    * Deactivate a customer account.
    */
    private void deactivateCustomer() {
    clearScreen();
    System.out.println("─── All Customers ───");
    printAllCustomers();  
    System.out.print("Enter username to deactivate: ");
    String username = scanner.nextLine().trim();

    if (authManager.deactivateUser(username)) {
        System.out.println("Customer account deactivated.");
    } else {
        System.out.println("Deactivation failed. Customer not found.");
    }
    pauseForUser();
    }

    /**
    * Delete a customer account.
     */
    private void deleteCustomer() {
    clearScreen();
    System.out.println("─── All Customers ───");
    printAllCustomers();  
    System.out.print("Enter username to delete: ");
    String username = scanner.nextLine().trim();

    if (authManager.deleteUser(username)) {
        System.out.println("Customer account deleted.");
    } else {
        System.out.println("Deletion failed. Customer not found.");
    }
    pauseForUser();
}

    /**
    * Upgrade Non-Member to Member.
    */
    private void upgradeToMember() {
    clearScreen();
    System.out.println("─── All Customers ───");
    printAllCustomers();  
    System.out.print("Enter username to upgrade: ");
    String username = scanner.nextLine().trim();

    User user = authManager.getUserByUsername(username); // authManager is your AuthenticationManager instance
    if (user instanceof NonMemberCustomer) {
        NonMemberCustomer nonMember = (NonMemberCustomer) user;
        
        // Generate membership ID using AuthenticationManager
        String newMembershipId = authManager.generateMembershipId(); 
        MemberCustomer member = nonMember.convertToMember(newMembershipId);

        // Update the user in the AuthenticationManager
        authManager.updateUser(username, member);

        System.out.println("Customer upgraded to Member successfully.");
    } else if (user instanceof MemberCustomer) {
        System.out.println("Customer is already a Member.");
    } else {
        System.out.println("User not found or not a customer.");
    }
    pauseForUser();
} 

    private void addNewVessel() {
    clearScreen();
    printHeader("ADD NEW VESSEL");
    
    try {
        System.out.println("Select vessel category:");
        System.out.println("1. Yacht");
        System.out.println("2. Boat");
        System.out.println("3. Pontoon");
        System.out.println("4. Jet Ski");
        System.out.println("5. Fishing Charter");
        System.out.print("Choose category (1-5): ");
        
        String categoryChoice = scanner.nextLine().trim();
        String category = getCategoryFromChoice(categoryChoice);
        
        if (category == null) {
            showError("Invalid category selection!");
            pauseForUser();
            return;
        }
        
        System.out.print("Enter vessel ID: ");
        String id = scanner.nextLine().trim();
        
        // Check if ID already exists
        if (vesselManager.getVesselById(id) != null) {
            showError("Vessel ID already exists! Please use a unique ID.");
            pauseForUser();
            return;
        }
        
        System.out.print("Enter vessel type: ");
        String vesselType = scanner.nextLine().trim();
        
        System.out.print("Enter location: ");
        String location = scanner.nextLine().trim();
        
        System.out.print("Enter purpose: ");
        String purpose = scanner.nextLine().trim();
        
        System.out.print("Enter capacity: ");
        int capacity = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Enter duration (e.g., 2h, 30min, 1.5h): ");
        String durationStr = scanner.nextLine().trim();
        Duration duration = VesselDataLoader.parseDuration(durationStr);
        
        System.out.print("Enter base price (RM): ");
        double basePrice = Double.parseDouble(scanner.nextLine().trim());
        
        System.out.print("Is vessel available? (y/n): ");
        boolean available = scanner.nextLine().trim().toLowerCase().startsWith("y");
        
        boolean success = vesselManager.addVessel(category, id, vesselType, location, 
                                                 purpose, capacity, duration, basePrice, available);
        
        if (success) {
            System.out.println("\nVessel added successfully!");
        } else {
            showError("Failed to add vessel!");
        }
        
    } catch (Exception e) {
        showError("Error adding vessel: " + e.getMessage());
    }
    
    pauseForUser();
}

    private void updateVessel() {
    clearScreen();
    printHeader("UPDATE VESSEL");
    
    System.out.print("Enter vessel ID to update: ");
    String id = scanner.nextLine().trim();
    
    Vessel vessel = vesselManager.getVesselById(id);
    if (vessel == null) {
        showError("Vessel not found!");
        pauseForUser();
        return;
    }
    
    System.out.println("\nCurrent vessel details:");
    System.out.printf("ID: %s\nType: %s\nLocation: %s\nPurpose: %s\nCapacity: %d\nPrice: RM%.2f\nAvailable: %s\n",
                     vessel.getId(), vessel.getVesselType(), vessel.getLocation(), 
                     vessel.getPurpose(), vessel.getCapacity(), vessel.getBasePrice(),
                     vessel.isAvailable() ? "Yes" : "No");
    
    System.out.println("\nWhat would you like to update?");
    System.out.println("1. Vessel Type");
    System.out.println("2. Location");
    System.out.println("3. Purpose");
    System.out.println("4. Capacity");
    System.out.println("5. Base Price");
    System.out.println("6. Availability");
    System.out.println("7. Update All Fields");
    System.out.print("Choose option (1-7): ");
    
    String choice = scanner.nextLine().trim();
    
    try {
        switch (choice) {
            case "1":
                System.out.print("Enter new vessel type: ");
                String newType = scanner.nextLine().trim();
                vesselManager.updateVesselField(id, "type", newType);
                break;
            case "2":
                System.out.print("Enter new location: ");
                String newLocation = scanner.nextLine().trim();
                vesselManager.updateVesselField(id, "location", newLocation);
                break;
            case "3":
                System.out.print("Enter new purpose: ");
                String newPurpose = scanner.nextLine().trim();
                vesselManager.updateVesselField(id, "purpose", newPurpose);
                break;
            case "4":
                System.out.print("Enter new capacity: ");
                int newCapacity = Integer.parseInt(scanner.nextLine().trim());
                vesselManager.updateVesselField(id, "capacity", String.valueOf(newCapacity));
                break;
            case "5":
                System.out.print("Enter new base price (RM): ");
                double newPrice = Double.parseDouble(scanner.nextLine().trim());
                vesselManager.updateVesselField(id, "price", String.valueOf(newPrice));
                break;
            case "6":
                System.out.print("Is vessel available? (y/n): ");
                boolean newAvailability = scanner.nextLine().trim().toLowerCase().startsWith("y");
                vesselManager.updateVesselField(id, "availability", String.valueOf(newAvailability));
                break;
            case "7":
                updateAllVesselFields(id);
                break;
            default:
                showError("Invalid option!");
                pauseForUser();
                return;
        }
        
        System.out.println("\n Vessel updated successfully!");
        
    } catch (Exception e) {
        showError("Error updating vessel: " + e.getMessage());
    }
    
    pauseForUser();
}

private void updateAllVesselFields(String id) {
    System.out.print("Enter new vessel type: ");
    String type = scanner.nextLine().trim();
    vesselManager.updateVesselField(id, "type", type);
    
    System.out.print("Enter new location: ");
    String location = scanner.nextLine().trim();
    vesselManager.updateVesselField(id, "location", location);
    
    System.out.print("Enter new purpose: ");
    String purpose = scanner.nextLine().trim();
    vesselManager.updateVesselField(id, "purpose", purpose);
    
    System.out.print("Enter new capacity: ");
    int capacity = Integer.parseInt(scanner.nextLine().trim());
    vesselManager.updateVesselField(id, "capacity", String.valueOf(capacity));
    
    System.out.print("Enter new base price (RM): ");
    double price = Double.parseDouble(scanner.nextLine().trim());
    vesselManager.updateVesselField(id, "price", String.valueOf(price));
    
    System.out.print("Is vessel available? (y/n): ");
    boolean availability = scanner.nextLine().trim().toLowerCase().startsWith("y");
    vesselManager.updateVesselField(id, "availability", String.valueOf(availability));
}

private void deleteVessel() {
    clearScreen();
    printHeader("DELETE VESSEL");
    
    System.out.print("Enter vessel ID to delete: ");
    String id = scanner.nextLine().trim();
    
    Vessel vessel = vesselManager.getVesselById(id);
    if (vessel == null) {
        showError("Vessel not found!");
        pauseForUser();
        return;
    }
    
    System.out.println("\nVessel to delete:");
    System.out.printf("ID: %s\nType: %s\nLocation: %s\nPrice: RM%.2f\n",
                     vessel.getId(), vessel.getVesselType(), 
                     vessel.getLocation(), vessel.getBasePrice());
    
    System.out.print("\nAre you sure you want to delete this vessel? (y/n): ");
    String confirm = scanner.nextLine().trim().toLowerCase();
    
    if (confirm.startsWith("y")) {
        boolean success = vesselManager.deleteVessel(id);
        if (success) {
            System.out.println("\n✓ Vessel deleted successfully!");
        } else {
            showError("Failed to delete vessel!");
        }
    } else {
        System.out.println("Delete operation cancelled.");
    }
    
    pauseForUser();
}

private String getCategoryFromChoice(String choice) {
    switch (choice) {
        case "1": return "Yacht";
        case "2": return "Boat";
        case "3": return "Pontoon";
        case "4": return "Jet Ski";
        case "5": return "Fishing Charter";
        default: return null;
    }
}



    
    private void handleBrowseAndRentVessels(Customer customer) {
        boolean running = true;
        
        while (running) {
            clearScreen();
            System.out.println("┌─────────────────────────────────────────────────────────┐");
            System.out.println("│                   RENTAL OPERATIONS                     │");
            System.out.println("├─────────────────────────────────────────────────────────┤");
            System.out.println("│ 1. Browse Available Vessels                             │");
            System.out.println("│ 2. Rent a Vessels                                       │");
            System.out.println("│ 3. Return Vessels                                       │");
            System.out.println("│ 4. Extend Rental                                        │");
            System.out.println("│ 5. Cancel / Refund Rental                               │");
            System.out.println("│ 6. View My Active Rentals                               │");
            System.out.println("│ 7. Back to Main Menu                                    │");
            System.out.println("└─────────────────────────────────────────────────────────┘");
            System.out.print("Choose option (1-7): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    showAvailableVessels();
                    break;
                case "2":
                    rentalController.processNewRental(customer);
                    break;
                case "3":
                    rentalController.processVesselReturn(customer);
                    break;
                case "4":
                    rentalController.extendRental(customer);
                    break;
                case "5":
                    rentalController.cancelRental(customer);
                    break;
                case "6":
                    showActiveRentals(customer);
                    break;
                case "7":
                    running = false;
                    break;
                default:
                    showError("Invalid option! Please choose 1-7.");
            }
        }
    }
    
    private void showAvailableVessels() {
        clearScreen();
        showLoadingMessage("Loading Available Vehicles");
        
        try {
            vesselManager.displayVessels(vesselManager.getAvailableVessels());
        } catch (Exception e) {
            System.out.println("Error loading vessels: " + e.getMessage());
        }
        
        pauseForUser();
    }
    
    private void showActiveRentals(Customer customer) {
        clearScreen();
        printHeader("MY ACTIVE RENTALS - " + customer.getName());
        
        try {
            rentalService.displayAllActiveRentals();
        } catch (Exception e) {
            System.out.println("Error loading active rentals: " + e.getMessage());
        }
        
        pauseForUser();
    }


private void showLoyaltyInfo(MemberCustomer customer) {
    clearScreen();
    printHeader("LOYALTY PROGRAM - " + customer.getName());

    // Ensure loyalty account exists
    LoyaltyAccount account = loyaltyPointManager.getLoyaltyAccount(customer.getCustomerId());
    if (account == null) {
        account = loyaltyPointManager.createLoyaltyAccount(customer.getCustomerId(), customer.getName());
    }

    loyaltyPointManager.displayLoyaltyStatus(customer.getCustomerId());

     if (!account.isVipMember() && loyaltyPointManager.isEligibleForVip(account)) {
        System.out.print("You are eligible for VIP upgrade! Would you like to upgrade now? (y/n): ");
        String upgradeChoice = scanner.nextLine().trim().toLowerCase();
        if (upgradeChoice.equals("y")) {
             loyaltyPointManager.upgradeToVip(account, customer); 
        } else {
            System.out.println("No worries, you can upgrade anytime from this menu.");
        }
    }

    //  Show rewards menu
    loyaltyPointManager.displayRewardsMenu();

    System.out.print("Would you like to redeem a reward? (y/n): ");
    String choice = scanner.nextLine().trim().toLowerCase();

    if (choice.equals("y")) {
        List<String> rewards = loyaltyPointManager.getAvailableRewards();
        System.out.print("Select reward number: ");
        try {
            int rewardIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (rewardIndex >= 0 && rewardIndex < rewards.size()) {
                String reward = rewards.get(rewardIndex);
                int cost = extractPointsCost(reward); // helper below
                if (loyaltyPointManager.redeemPoints(customer.getCustomerId(), cost, reward)) {
                    System.out.println("Successfully redeemed: " + reward);
                } else {
                    System.out.println("Not enough points for this reward.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    pauseForUser();
}

// helper: parse cost from reward string
private int extractPointsCost(String rewardText) {
    try {
        return Integer.parseInt(rewardText.replaceAll("[^0-9]", ""));
    } catch (Exception e) {
        return 0;
    }
}

    private void handleMembershipUpgrade(NonMemberCustomer customer) {
    clearScreen();
    printHeader("MEMBERSHIP UPGRADE");
    
    System.out.println("Benefits of becoming a member:");
    System.out.println("Earn loyalty points on every rental");
    System.out.println("Standard member discounts: up to 8%");
    System.out.println("Upgrade to VIP for even higher discounts (up to 15%)");
    System.out.println("Priority customer support");
    System.out.println("Special member-only promotions");
    System.out.println("Early access to new vessels");
    
    final double MEMBERSHIP_FEE = 100.0;
    final int MIN_COMPLETED_RENTALS = 2;

    System.out.println("\nUpgrade Requirements:");
    System.out.println("One-time fee: RM" + MEMBERSHIP_FEE);
    System.out.println("At least " + MIN_COMPLETED_RENTALS + " completed rentals");

    // check rental history
    int completedRentals = rentalService.getCompletedRentalsCount(customer.getCustomerId());
    if (completedRentals < MIN_COMPLETED_RENTALS) {
        showError("You need at least " + MIN_COMPLETED_RENTALS + 
                  " completed rentals before you can upgrade. " + 
                  "(Currently: " + completedRentals + ")");
        pauseForUser();
        return;
    }

    System.out.print("\nWould you like to pay RM" + MEMBERSHIP_FEE + " and upgrade? (y/n): ");
    String choice = scanner.nextLine().trim().toLowerCase();

    if (!("y".equals(choice) || "yes".equals(choice))) {
        showInfo("Upgrade cancelled.");
        pauseForUser();
        return;
    }

    // ✅ Use your existing payment input function
    PaymentInput paymentInput = collectPaymentInput(MEMBERSHIP_FEE);

    // ✅ Process membership payment (no rental, so pass null)
    Receipt receipt = paymentManager.processCustomPayment(
            null,
            customer,
            MEMBERSHIP_FEE,
            paymentInput.paymentMethod(),
            paymentInput.maskedCard(),
            paymentInput.eWalletPhone()
    );

    if (receipt == null) {
        showError("Payment failed. Membership upgrade cancelled.");
        pauseForUser();
        return;
    }

    showLoadingMessage("Processing membership upgrade...");

    // ✅ Upgrade customer
    String membershipId = authManager.generateMembershipId();
    MemberCustomer newMember = customer.convertToMember(membershipId);

    if (newMember != null) {
        authManager.updateUser(customer.getUsername(), newMember);
        loyaltyPointManager.createLoyaltyAccount(newMember.getCustomerId(), newMember.getName());

        showSuccess("Membership upgrade successful!");
        showInfo("Welcome to the Membership Program! You are now a Standard Member.");
        showInfo("Your Membership ID: " + membershipId);
        showInfo("As a welcome gift, you have received 100 loyalty points!");
        loyaltyPointManager.addPoints(newMember.getCustomerId(), 100);
    } else {
        showError("You are not eligible for membership upgrade at this time.");
    }

    pauseForUser();
}
     private void handleLeaveReview(Customer customer) {
        clearScreen();
        printHeader("LEAVE A REVIEW -" + customer.getName() );

        // Get completed rentals without reviews
        List<RentalRecord> eligibleRentals = new ArrayList<>();
        for (RentalRecord r : rentalService.getCustomerFinishedRentals(customer.getCustomerId())) {
        if (r.getReview() == null) {
            eligibleRentals.add(r);
            }
        }
        
        if (eligibleRentals.isEmpty()) {
            System.out.println("No completed rentals available for review.");
            pauseForUser();
            return;
        }

        // Show list
        for (int i = 0; i < eligibleRentals.size(); i++) {
            RentalRecord r = eligibleRentals.get(i);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            System.out.printf("%d. Rental ID: %s | Vessel: %s | Schedule Time: %s%n",
                i + 1, r.getRentalId(), r.getVesselType(), r.getScheduledStart().format(dtf) + " to " + r.getScheduledEnd().format(dtf));
        }

        System.out.print("\nSelect rental to review (1-" + eligibleRentals.size() + "): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            showError("Invalid choice.");
            return;
        }
        if (choice < 0 || choice >= eligibleRentals.size()) {
            showError("Invalid rental selection.");
            return;
        }

        RentalRecord selectedRental = eligibleRentals.get(choice);

        // Get rating & comment
        System.out.print("Enter rating (1-5): ");
        int rating;
        try {
            rating = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            showError("Invalid rating.");
            return;
        }
        System.out.print("Enter your comment: ");
        String comment = scanner.nextLine();

        // Add review
        Review review = reviewManager.addReview(customer, selectedRental, rating, comment);
        if (review != null) {
            showSuccess("Review submitted successfully!");
            if (customer instanceof MemberCustomer) {
                showInfo("+25 Loyalty Points awarded!");
            }
        } else {
            showError("Failed to submit review.");
        }

        pauseForUser();
    }

    
    private void handleAccountManagement() {
    User user = authManager.getCurrentUser();
    if (user == null || user instanceof Admin) {
        System.out.println("This menu is only available for Members and Non-Members.");
        pauseForUser();
        return;
    }

    Scanner scanner = new Scanner(System.in);
    String choice;

    do {
        clearScreen();
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.println("│           ACCOUNT MANAGEMENT            │");
        System.out.println("├─────────────────────────────────────────┤");
        System.out.println("│ 1. View Personal Info                   │");
        System.out.println("│ 2. Update Personal Info                 │");
        System.out.println("│ 3. Back to Dashboard                    │");
        System.out.println("└─────────────────────────────────────────┘");
        System.out.print("Choose an option: ");
        choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                viewPersonalInfo(user);
                break;
            case "2":
                updatePersonalInfo(user);
                break;
            case "3":
                System.out.println("Returning to dashboard...");
                break;
            default:
                System.out.println("Invalid choice. Try again.");
                pauseForUser();
        }
    } while (!choice.equals("3"));
}

    private void viewPersonalInfo(User user) {
    clearScreen();
    printHeader("PERSONAL INFO");

    System.out.println("Username       : " + user.getUsername());
    System.out.println("Name           : " + user.getName());
    System.out.println("Email          : " + user.getEmail());
    System.out.println("Phone          : " + user.getPhone());

    if (user instanceof MemberCustomer member) {
        System.out.println("Customer ID    : " + member.getCustomerId());
        System.out.println("Address        : " + member.getAddress());
        System.out.println("IC Number      : " + member.getIcNumber());
        System.out.println("Membership ID  : " + member.getMembershipId());
        System.out.println("Membership Tier: " + member.getMembershipTier());
    } else if (user instanceof NonMemberCustomer nonMember) {
        System.out.println("Customer ID    : " + nonMember.getCustomerId());
        System.out.println("Address        : " + nonMember.getAddress());
        System.out.println("IC Number      : " + nonMember.getIcNumber());
    }

    pauseForUser();
}

   // Update personal information
   private void updatePersonalInfo(User user) {
    Scanner scanner = new Scanner(System.in);
    clearScreen();
    System.out.println("UPDATE PERSONAL INFO");

    System.out.print("Enter new name (leave blank to keep current): ");
    String newName = scanner.nextLine().trim();
    if (!newName.isEmpty()) user.setName(newName);

    System.out.print("Enter new email (leave blank to keep current): ");
    String newEmail = scanner.nextLine().trim();
    if (!newEmail.isEmpty()) user.setEmail(newEmail);

    System.out.print("Enter new phone (leave blank to keep current): ");
    String newPhone = scanner.nextLine().trim();
    if (!newPhone.isEmpty()) user.setPhone(newPhone);

    if (user instanceof MemberCustomer member) {
        System.out.print("Enter new address (leave blank to keep current): ");
        String newAddress = scanner.nextLine().trim();
        if (!newAddress.isEmpty()) member.setAddress(newAddress);
    } else if (user instanceof NonMemberCustomer nonMember) {
        System.out.print("Enter new address (leave blank to keep current): ");
        String newAddress = scanner.nextLine().trim();
        if (!newAddress.isEmpty()) nonMember.setAddress(newAddress);
    }

    authManager.updateUser(user.getUsername(), user); // save changes
    System.out.println("\nPersonal info updated successfully!");
    pauseForUser();
  }
    
    private void handlePaymentMethods(Customer customer) {
    Scanner scanner = new Scanner(System.in);
    while (true) {
        clearScreen();
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.println("│          PAYMENT MANAGEMENT             │");
        System.out.println("├─────────────────────────────────────────┤");
        System.out.println("│ 1. View Payment History & Details       │");
        System.out.println("│ 2. View Payment Summary                 │");
        System.out.println("│ 3. Back to Dashboard                    │");
        System.out.println("└─────────────────────────────────────────┘");
        System.out.print("Choose an option (1-3): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                // Show summary list
                List<Receipt> receipts = paymentManager.getCustomerReceipts(customer.getCustomerId());
                if (receipts.isEmpty()) {
                    System.out.println("No payment history found.");
                    pauseForUser();
                    break;
                }

                boolean viewing = true;
                while (viewing) {
                clearScreen();
                System.out.println("\n===== Payment History =====");
                System.out.println("ReceiptID | Customer | Vessel | Amount | Method");
                System.out.println("---------------------------------------------------");
                for (Receipt r : receipts) {
                r.printSummaryReceipt();
                }

                // Prompt for details
                System.out.print("\nEnter a Receipt ID to view details (or press Enter to skip): ");
                String receiptId = scanner.nextLine().trim();
                if (receiptId.isEmpty()) {
                    viewing = false; // exit loop
                } else {
                Receipt detail = paymentManager.getReceiptById(receiptId);
                if (detail != null) {
                clearScreen();
                detail.printReceipt();
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                } else {
                System.out.println("Receipt not found: " + receiptId);
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }
    break;

            case "2":
                // Show aggregated summary
                PaymentSummary summary = paymentManager.getPaymentSummary(customer.getCustomerId());
                if (summary == null) {
                    System.out.println("No payment summary available.");
                } else {
                    summary.printSummary();
                }
                pauseForUser();
                break;

            case "3":
                return; // back to dashboard

            default:
                System.out.println("Invalid choice. Please try again.");
                pauseForUser();
                break;
        }
    }
}
    private void handleCustomerSupport() {
        clearScreen();
        printHeader("CUSTOMER SUPPORT");
    
        System.out.println("Vessel Rental Support Center");
        System.out.println("Phone: +1-800-RENTAL (1-800-736-8254)");
        System.out.println("Email: support@rentalapp.com");
        System.out.println("Hours: 24/7 Support Available");
        System.out.println("Live Chat: Available on our website");
        
        System.out.println("\nCommon Issues:");
        System.out.println("Vessel booking problems");
        System.out.println("Payment and billing questions");
        System.out.println("Account and membership issues");
        System.out.println("Emergency assistance");
        
        pauseForUser();
    }
    
    private void handleChangePassword() {
    showLoadingMessage("Loading Password Change");
    Scanner scanner = new Scanner(System.in);
    User currentUser = authManager.getCurrentUser();

    if (currentUser == null) {
        System.out.println("No user is currently logged in!");
        pauseForUser();
        return;
    }

    if (currentUser instanceof Admin) {
        System.out.println("\n1. Change your own password");
        System.out.println("2. Reset another user's password");
        System.out.print("Choose an option (1-2): ");
        String choice = scanner.nextLine().trim();

        if ("2".equals(choice)) {
            System.out.print("Enter username to reset password: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine();

            if (authManager.resetPasswordForUser(username, newPassword)) {
                System.out.println("Password reset successfully for " + username);
            } else {
                System.out.println("Failed to reset password. Check username or permissions.");
            }
            pauseForUser();
            return;
        }
    }


    // Self password change
    String currentPassword;
    while (true) {
        System.out.print("Enter current password: ");
        currentPassword = scanner.nextLine();
        if (authManager.verifyCurrentUserPassword(currentPassword)) {
            break;
        }
        System.out.println("Incorrect current password. Try again.");
    }

    String newPassword;
    while (true) {
        System.out.print("Enter new password (min 6 chars, letters & numbers): ");
        newPassword = scanner.nextLine();
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match!");
            continue;
        }
        break;
    }

    if (authManager.changeCurrentUserPassword(currentPassword, newPassword)) {
        System.out.println("Password changed successfully!");
    } else {
        System.out.println("Failed to change password.");
    }

    pauseForUser(); 
    }   
    
    private String maskCardNumber(String cardNumber) {
    if (cardNumber.length() < 4) return "****"; // safety check
    String last4 = cardNumber.substring(cardNumber.length() - 4);
    return "**** **** **** " + last4;
    }

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
        System.out.print("Select payment method (1-3): ");
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
                System.out.println(" PIN must be 6 digits.");
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

    private void printHeader(String title) {
        System.out.println("═".repeat(60));
        System.out.println("    " + title);
        System.out.println("═".repeat(60));
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

    private LocalDate readDateInput(String prompt) {
    while (true) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("Date cannot be empty. Please try again.");
            continue;
        }

        try {
            return LocalDate.parse(input); // expects yyyy-MM-dd
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        }
    }
}

}
