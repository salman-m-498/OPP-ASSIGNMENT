package com.rentalapp.auth;

import java.time.LocalDateTime;

public class Admin extends User {
    private String adminId;
    private String department;
    private int accessLevel;
    private LocalDateTime lastLogin;
    
    public Admin() {
        super();
        this.accessLevel = 5; // Highest access level
    }
    
    public Admin(String username, String hashedPassword, String name, String email, String phone, String adminId, String department) {
        super(username, hashedPassword, name, email, phone);
        this.adminId = adminId;
        this.department = department;
        this.accessLevel = 5; // Highest access level
    }
    
    // Getters
    public String getAdminId() { return adminId; }
    public String getDepartment() { return department; }
    public int getAccessLevel() { return accessLevel; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    
    // Setters
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public void setDepartment(String department) { this.department = department; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    @Override
    public String getUserType() {
        return "ADMIN";
    }
    
    // Admin specific methods
    public boolean hasHighestAccess() {
        return accessLevel >= 5;
    }
    
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return String.format("Admin{username='%s', name='%s', department='%s', adminId='%s'}", 
                           username, name, department, adminId);
    }
}