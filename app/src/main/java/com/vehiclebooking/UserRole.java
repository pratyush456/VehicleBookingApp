package com.vehiclebooking;

public enum UserRole {
    ADMIN("Admin"),
    DRIVER("Driver"), 
    PASSENGER("Passenger");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}