package com.rentalapp;

import java.util.Scanner;
import com.rentalapp.vessel.VesselManager;
import com.rentalapp.auth.AuthenticationManager;
import com.rentalapp.auth.DashboardManager;
import com.rentalapp.auth.User;

/**
 * App class - Handles all menu operations and user interactions
 */
public class App {
    private Scanner scanner;
    private boolean running;
    private User currentUser;
    private VesselManager vesselManager;
    private AuthenticationManager authManager;
    private DashboardManager dashboardManager;

    public App() {
        this.scanner = new Scanner(System.in);
        this.running = true;
        this.currentUser = null;
         this.vesselManager = new VesselManager();
        this.authManager = new AuthenticationManager();
        this.dashboardManager = new DashboardManager(authManager);
    }
    
    /**
     * Start the application
     */
    public void start() {
        showWelcomeMessage();
        
        while (running) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                dashboardManager.showDashboard(currentUser);
                // After dashboard exits, logout the user
                authManager.logout();
                currentUser = null;
            }
        }
        
        showGoodbyeMessage();
        scanner.close();
    }
    
    /**
     * Display welcome message
     */
    private void showWelcomeMessage() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║             VESSEL RENTAL MANAGEMENT SYSTEM               ║");
        System.out.println("║                                                           ║");
        System.out.println("║          Welcome to our premium vessel rental service!    ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        showLoadingBar("Initializing system");
        System.out.println("\nSystem ready!");
        pauseForUser();
    }
    
    /**
     * Display goodbye message
     */
    private void showGoodbyeMessage() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║                   Thank you for choosing                  ║");
        System.out.println("║                    VESSEL RENTAL SYSTEM                   ║");
        System.out.println("║                                                           ║");
        System.out.println("║                       ENJOY!                              ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Display the main menu
     */
    public void showMainMenu() {
        clearScreen();
        System.out.println("\n╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║                        MAIN MENU                            ║");
        System.out.println("╠═════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                             ║");
        System.out.println("║  1. Login to Your Account                                   ║");
        System.out.println("║  2. Create New Account                                      ║");
        System.out.println("║  3. Browse Vessels (Guest)                                  ║");
        System.out.println("║  4. Contact Support                                         ║");
        System.out.println("║  5. Exit Application                                        ║");
        System.out.println("║                                                             ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
        System.out.print("Please select an option (1-5): ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "2":
                handleRegistration();
                break;
            case "3":
                handleGuestBrowsing();
                break;
            case "4":
                handleContactSupport();
                break;
            case "5":
                running = false;
                break;
            default:
                showError("Invalid option! Please choose 1-5.");
        }
    }
    
    /**
     * Handle user login
     */
    private void handleLogin() {
        User user = authManager.login();
        if (user != null) {
            currentUser = user;
        }
    }
    
    /**
     * Handle user registration
     */
    private void handleRegistration() {
        authManager.register();
    }
    
    /**
     * Handle guest vessel browsing
     */
    private void handleGuestBrowsing() {
        clearScreen();
        System.out.println("═".repeat(60));
        System.out.println("GUEST VESSEL BROWSING");
        System.out.println("═".repeat(60));
        
        System.out.println("Available VESSEL (preview for guests):");
        System.out.println("Create an account to rent vessels and enjoy exclusive benefits!");
        
        showLoadingBar("Loading vessel catalog");
        
        // Show a preview of available vehicles
        try {
            vesselManager.displayVessels(vesselManager.getAvailableVessels());
        } catch (Exception e) {
            System.out.println("Vessel browsing functionality will be fully available after login.");
        }
        
        System.out.println("\nReady to rent? Create an account or login to get started!");
        pauseForUser();
    }
    
    /**
     * Handle contact support
     */
    private void handleContactSupport() {
        clearScreen();
        System.out.println("═".repeat(60));
        System.out.println("CONTACT SUPPORT");
        System.out.println("═".repeat(60));
        
        System.out.println("Vessel Rental Support Center");
        System.out.println("Phone: +1-800-RENTAL (1-800-736-8254)");
        System.out.println("Email: support@rentalapp.com");
        System.out.println("Website: www.rentalapp.com");
        System.out.println("Hours: 24/7 Support Available");
        System.out.println("Live Chat: Available on our website");
        
        System.out.println("\nEmergency Assistance: +1-800-HELP-NOW");
        System.out.println("Technical Support: +1-800-TECH-HELP");
        
        pauseForUser();
    }
    
    /**
     * Utility methods for UI
     */
    private void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            // If clearing fails, just print some newlines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    private void showLoadingBar(String message) {
        System.out.print("\n" + message + " ");
        for (int i = 0; i < 20; i++) {
            System.out.print("█");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(" Complete!");
    }
    
    private void pauseForUser() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void showError(String message) {
        System.out.println("\n" + message);
        pauseForUser();
    }
}
