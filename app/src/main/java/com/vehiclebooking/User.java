package com.vehiclebooking;

public class User {
    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String password; // In production, this should be hashed
    private UserRole role;
    private String fullName;
    private long createdAt;
    private boolean isActive;
    
    // Driver-specific fields
    private String licenseNumber;
    private String vehicleDetails;
    private boolean isAvailable;
    
    public User() {
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
        this.isAvailable = false;
    }
    
    public User(String username, String email, String phoneNumber, String password, UserRole role, String fullName) {
        this();
        this.userId = "USER" + System.currentTimeMillis();
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    // Driver-specific getters and setters
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getVehicleDetails() { return vehicleDetails; }
    public void setVehicleDetails(String vehicleDetails) { this.vehicleDetails = vehicleDetails; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    // Utility methods
    public boolean isAdmin() { return role == UserRole.ADMIN; }
    public boolean isDriver() { return role == UserRole.DRIVER; }
    public boolean isPassenger() { return role == UserRole.PASSENGER; }
    
    @Override
    public String toString() {
        return fullName + " (" + role.getDisplayName() + ")";
    }
}