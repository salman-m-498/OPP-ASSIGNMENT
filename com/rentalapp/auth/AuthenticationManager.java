package com.rentalapp.auth;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AuthenticationManager {
    private Map<String, User> users;
    private final String USER_DATA_FILE = "users.dat";
    private User currentUser;
    private Scanner scanner;
    
    // Email and phone validation patterns
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^(\\+60|0)1\\d{8,15}$");
    private static final Pattern IC_PATTERN = Pattern.compile("^\\d{6}-\\d{2}-\\d{4}$");
    
    public AuthenticationManager() {
        this.users = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
        loadUsers();
        
        // Create default admin if no users exist
        if (users.isEmpty()) {
            createDefaultAdmin();
        }
    }
    
    /**
     * Create a default admin account
     */
    private void createDefaultAdmin() {
        String hashedPassword = hashPassword("admin123");
        Admin defaultAdmin = new Admin("admin", hashedPassword, "System Administrator", 
                                     "admin@rentalapp.com", "+1234567890", "ADM001", "IT");
        users.put("admin", defaultAdmin);
        saveUsers();
        System.out.println("Default admin account created (username: admin, password: admin123)");
    }
    
    /**
     * Register a new user
     */
    public boolean register() {
        clearScreen();
        printHeader("USER REGISTRATION");
        
        try {
            // Get user type
            String userType = getUserType();
            if (userType == null) return false;
            
            // Get basic information
            String username = getValidUsername();
            if (username == null) return false;
            
            String password = getValidPassword();
            if (password == null) return false;
            
            String name = getValidName();
            if (name == null) return false;
            
            String email = getValidEmail();
            if (email == null) return false;
            
            String phone = getValidPhone();
            if (phone == null) return false;
            
            // Hash password
            String hashedPassword = hashPassword(password);
            
            User newUser = null;
            
            if ("ADMIN".equals(userType)) {
                newUser = createAdminUser(username, hashedPassword, name, email, phone);
            } else {
                newUser = createCustomerUser(username, hashedPassword, name, email, phone, userType);
            }
            
            if (newUser != null) {
                users.put(username, newUser);
                saveUsers();
                
                showLoadingBar("Creating account");
                System.out.println("\nAccount created successfully!");
                System.out.println("User Type: " + newUser.getUserType());
                System.out.println("Welcome, " + name + "!");
                
                pauseForUser();
                return true;
            }
            
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
            pauseForUser();
        }
        
        return false;
    }
    
    /**
     * Login user
     */
    public User login() {
        clearScreen();
        printHeader("USER LOGIN");
        
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty!");
            pauseForUser();
            return null;
        }
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        showLoadingBar("Authenticating");
        
        User user = users.get(username);
        if (user != null && verifyPassword(password, user.getHashedPassword())) {
            if (!user.isActive()) {
                System.out.println("Account is deactivated. Please contact administrator.");
                pauseForUser();
                return null;
            }
            
            // Update last login for admin
            if (user instanceof Admin) {
                ((Admin) user).updateLastLogin();
                saveUsers();
            }
            
            currentUser = user;
            System.out.println("\nLogin successful!");
            System.out.println("Welcome back, " + user.getName() + "!");
            System.out.println("User Type: " + user.getUserType());
            pauseForUser();
            
            return user;
        } else {
            System.out.println("\nInvalid username or password!");
            pauseForUser();
            return null;
        }
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("Goodbye, " + currentUser.getName() + "!");
            currentUser = null;
        }
    }
    
    /**
     * Get user type for registration
     */
    private String getUserType() {
        while (true) {
            System.out.println("\nSelect User Type:");
            System.out.println("1. Customer (Non-Member)");
            System.out.println("2. Customer (Member)");
            System.out.println("3. Admin");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose option (1-4): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    return "NON_MEMBER";
                case "2":
                    return "MEMBER";
                case "3":
                    return "ADMIN";
                case "4":
                    return null;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
    
    /**
     * Create admin user with additional details
     */
    private Admin createAdminUser(String username, String hashedPassword, String name, String email, String phone) {
        System.out.print("Enter Admin ID: ");
        String adminId = scanner.nextLine().trim();
        
        System.out.print("Enter Department: ");
        String department = scanner.nextLine().trim();
        
        return new Admin(username, hashedPassword, name, email, phone, adminId, department);
    }
    
    /**
     * Create customer user with additional details
     */
    private Customer createCustomerUser(String username, String hashedPassword, String name, String email, String phone, String type) {
        String customerId = generateCustomerId();
        
        System.out.print("Enter address: ");
        String address = scanner.nextLine().trim();
        
        String icNumber = getValidICNumber();
        if (icNumber == null) return null;

        if ("MEMBER".equals(type)) {
        String membershipId = generateMembershipId();
        return new MemberCustomer(
            username, hashedPassword, name, email, phone,
            customerId, address, icNumber, membershipId, "Standard" // ✅ Added membershipTier
        );
       } else {
        return new NonMemberCustomer(
            username, hashedPassword, name, email, phone,
            customerId, address, icNumber
        );
       }
    }
    
    /**
     * Validation methods
     */
    private String getValidUsername() {
        while (true) {
            System.out.print("Enter username (3-20 characters): ");
            String username = scanner.nextLine().trim();
            
            if (username.length() < 3 || username.length() > 20) {
                System.out.println("Username must be 3-20 characters long!");
                continue;
            }
            
            if (users.containsKey(username)) {
                System.out.println("Username already exists! Please choose another.");
                continue;
            }
            
            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                System.out.println("Username can only contain letters, numbers, and underscores!");
                continue;
            }
            
            return username;
        }
    }
    
    private String getValidPassword() {
        while (true) {
            System.out.print("Enter password (min 6 characters): ");
            String password = scanner.nextLine();
            
            if (password.length() < 6) {
                System.out.println("Password must be at least 6 characters long!");
                continue;
            }
            
            // Check for at least one digit and one letter
            if (!password.matches(".*\\d.*") || !password.matches(".*[a-zA-Z].*")) {
                System.out.println("Password must contain at least one letter and one number!");
                continue;
            }
            
            System.out.print("Confirm password: ");
            String confirmPassword = scanner.nextLine();
            
            if (!password.equals(confirmPassword)) {
                System.out.println("Passwords do not match!");
                continue;
            }
            
            return password;
        }
    }
    
    private String getValidName() {
        while (true) {
            System.out.print("Enter full name: ");
            String name = scanner.nextLine().trim();
            
            if (name.length() < 2) {
                System.out.println("Name must be at least 2 characters long!");
                continue;
            }
            
            if (!name.matches("^[a-zA-Z\\s]+$")) {
                System.out.println("Name can only contain letters and spaces!");
                continue;
            }
            
            return name;
        }
    }
    
    private String getValidEmail() {
        while (true) {
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();
            
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("Invalid email format!");
                continue;
            }
            
            return email;
        }
    }
    
    private String getValidPhone() {
        while (true) {
            System.out.print("Enter phone number: ");
            String phone = scanner.nextLine().trim();
            
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                System.out.println("Invalid phone number format!");
                continue;
            }
            
            return phone;
        }
    }
    
    private String getValidICNumber() {
    while (true) {
        System.out.print("Enter Malaysian IC number (YYMMDD-XX-XXXX): ");
        String ic = scanner.nextLine().trim();

        if (!IC_PATTERN.matcher(ic).matches()) {
            System.out.println("Invalid IC format! Example: 990101-14-5678");
            continue;
        }

        return ic;
    }
}
    /**
     * Password hashing and verification
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    private boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }

    // Public method for checking the current user's password
    public boolean verifyCurrentUserPassword(String password) {
    if (currentUser == null) return false;
    return verifyPassword(password, currentUser.getHashedPassword());
    }

    public boolean changeCurrentUserPassword(String currentPassword, String newPassword) {
    if (currentUser == null) return false;

    // Verify current password
    if (!verifyPassword(currentPassword, currentUser.getHashedPassword())) {
        return false;
    }

    // Update password
    currentUser.setHashedPassword(hashPassword(newPassword));
    saveUsers();
    return true;
    }

    public boolean resetPasswordForUser(String username, String newPassword) {
    if (!(currentUser instanceof Admin)) return false;
    User user = users.get(username);
    if (user == null) return false;

    user.setHashedPassword(hashPassword(newPassword));
    saveUsers();
    return true;
    }
    
    /**
     * ID generation methods
     */
    public String generateCustomerId() {
        return "CUST" + System.currentTimeMillis() % 100000;
    }
    
    public String generateMembershipId() {
        return "MEM" + System.currentTimeMillis() % 100000;
    }

    
    /**
     * File operations
     */
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_DATA_FILE))) {
            users = (Map<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, start with empty map
            users = new HashMap<>();
        } catch (Exception e) {
            System.out.println("Error loading user data: " + e.getMessage());
            users = new HashMap<>();
        }
    }
    
    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DATA_FILE))) {
            oos.writeObject(users);
        } catch (Exception e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }
    
    /**
     * UI utility methods
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
    
    private void printHeader(String title) {
        System.out.println("═".repeat(60));
        System.out.println("    " + title);
        System.out.println("═".repeat(60));
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
    
    // Getters
    public User getCurrentUser() { return currentUser; }
    public Map<String, User> getAllUsers() { return new HashMap<>(users); }
    
    public User getUserByUsername(String username) {
    return users.get(username);
    }
    // Admin operations
    public boolean deleteUser(String username) {
        if (currentUser instanceof Admin && users.containsKey(username)) {
            users.remove(username);
            saveUsers();
            return true;
        }
        return false;
    }
    
    public boolean deactivateUser(String username) {
        User user = users.get(username);
        if (currentUser instanceof Admin && user != null) {
            user.setActive(false);
            saveUsers();
            return true;
        }
        return false;
    }
    
    public boolean activateUser(String username) {
        User user = users.get(username);
        if (currentUser instanceof Admin && user != null) {
            user.setActive(true);
            saveUsers();
            return true;
        }
        return false;
    }

    public boolean updateUser(String username, User updatedUser) {
    if (users.containsKey(username)) {
        users.put(username, updatedUser);
        saveUsers();
        return true;
    }
    return false;
}
}

