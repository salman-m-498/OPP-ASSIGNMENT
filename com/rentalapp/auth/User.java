package com.rentalapp.auth;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class User implements Serializable {
    protected String username;
    protected String hashedPassword;
    protected String name;
    protected String email;
    protected String phone;
    protected LocalDateTime dateCreated;
    protected boolean isActive;
    
    public User() {
        this.dateCreated = LocalDateTime.now();
        this.isActive = true;
    }
    
    public User(String username, String hashedPassword, String name, String email, String phone) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dateCreated = LocalDateTime.now();
        this.isActive = true;
    }
    
    // Getters
    public String getUsername() { return username; }
    public String getHashedPassword() { return hashedPassword; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDateTime getDateCreated() { return dateCreated; }
    public boolean isActive() { return isActive; }
    
    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setActive(boolean active) { this.isActive = active; }
    
    // Abstract method for dashboard access
    public abstract String getUserType();
    
    @Override
    public String toString() {
        return String.format("User{username='%s', name='%s', email='%s', type='%s'}", 
                           username, name, email, getUserType());
    }
}