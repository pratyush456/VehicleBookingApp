package com.vehiclebooking.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.vehiclebooking.User;
import com.vehiclebooking.UserRole;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey
    @NonNull
    public String userId;
    public String username;
    public String email;
    public String phoneNumber;
    public String password;
    public UserRole role;
    public String fullName;
    public long createdAt;
    public boolean isActive;
    
    // Driver-specific fields
    public String licenseNumber;
    public String vehicleDetails;
    public boolean isAvailable;

    public UserEntity() {}

    // Constructor to convert from domain model
    public UserEntity(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.fullName = user.getFullName();
        this.createdAt = user.getCreatedAt();
        this.isActive = user.isActive();
        this.licenseNumber = user.getLicenseNumber();
        this.vehicleDetails = user.getVehicleDetails();
        this.isAvailable = user.isAvailable();
    }

    // Convert to domain model
    public User toUser() {
        User user = new User();
        user.setUserId(this.userId);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPhoneNumber(this.phoneNumber);
        user.setPassword(this.password);
        user.setRole(this.role);
        user.setFullName(this.fullName);
        user.setCreatedAt(this.createdAt);
        user.setActive(this.isActive);
        user.setLicenseNumber(this.licenseNumber);
        user.setVehicleDetails(this.vehicleDetails);
        user.setAvailable(this.isAvailable);
        return user;
    }
}
