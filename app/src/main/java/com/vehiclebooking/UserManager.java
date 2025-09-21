package com.vehiclebooking;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String PREFERENCES_NAME = "vehicle_booking_users";
    private static final String USERS_KEY = "users_list";
    private static final String CURRENT_USER_KEY = "current_user";
    private static final String IS_LOGGED_IN_KEY = "is_logged_in";
    
    private static UserManager instance;
    private Context context;
    private User currentUser;
    
    private UserManager(Context context) {
        this.context = context.getApplicationContext();
        loadCurrentUser();
    }
    
    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }
        return instance;
    }
    
    // Initialize with default admin user if no users exist
    public void initializeDefaultUsers() {
        List<User> users = getAllUsers();
        if (users.isEmpty()) {
            // Create default admin user
            User admin = new User("admin", "admin@vehiclebooking.com", "1234567890", 
                                "admin123", UserRole.ADMIN, "System Administrator");
            saveUser(admin);
        }
    }
    
    // User Registration
    public boolean registerUser(String username, String email, String phoneNumber, 
                               String password, UserRole role, String fullName) {
        // Check if username already exists
        if (isUsernameExists(username)) {
            return false;
        }
        
        // Check if email already exists
        if (isEmailExists(email)) {
            return false;
        }
        
        User newUser = new User(username, email, phoneNumber, password, role, fullName);
        return saveUser(newUser);
    }
    
    // User Login
    public boolean login(String username, String password) {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && 
                user.getPassword().equals(password) && 
                user.isActive()) {
                
                currentUser = user;
                saveCurrentUserSession();
                return true;
            }
        }
        return false;
    }
    
    // Logout
    public void logout() {
        currentUser = null;
        clearCurrentUserSession();
    }
    
    // Check if user is logged in
    public boolean isLoggedIn() {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(IS_LOGGED_IN_KEY, false) && currentUser != null;
    }
    
    // Get current logged in user
    public User getCurrentUser() {
        return currentUser;
    }
    
    // Save user to storage
    private boolean saveUser(User user) {
        try {
            List<User> users = getAllUsers();
            users.add(user);
            saveAllUsers(users);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all users
    private List<User> getAllUsers() {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String usersJson = prefs.getString(USERS_KEY, "[]");
        
        Gson gson = new Gson();
        Type listType = new TypeToken<List<User>>(){}.getType();
        
        List<User> users = gson.fromJson(usersJson, listType);
        return users != null ? users : new ArrayList<>();
    }
    
    // Save all users
    private void saveAllUsers(List<User> users) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String usersJson = gson.toJson(users);
        prefs.edit().putString(USERS_KEY, usersJson).apply();
    }
    
    // Check if username exists
    private boolean isUsernameExists(String username) {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    // Check if email exists
    private boolean isEmailExists(String email) {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
    
    // Save current user session
    private void saveCurrentUserSession() {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userJson = gson.toJson(currentUser);
        prefs.edit()
                .putString(CURRENT_USER_KEY, userJson)
                .putBoolean(IS_LOGGED_IN_KEY, true)
                .apply();
    }
    
    // Load current user session
    private void loadCurrentUser() {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(IS_LOGGED_IN_KEY, false)) {
            String userJson = prefs.getString(CURRENT_USER_KEY, "");
            if (!userJson.isEmpty()) {
                Gson gson = new Gson();
                currentUser = gson.fromJson(userJson, User.class);
            }
        }
    }
    
    // Clear current user session
    private void clearCurrentUserSession() {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(CURRENT_USER_KEY)
                .putBoolean(IS_LOGGED_IN_KEY, false)
                .apply();
    }
    
    // Get users by role (for admin to manage)
    public List<User> getUsersByRole(UserRole role) {
        List<User> allUsers = getAllUsers();
        List<User> filteredUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getRole() == role) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }
    
    // Update user (for profile updates)
    public boolean updateUser(User updatedUser) {
        List<User> users = getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(updatedUser.getUserId())) {
                users.set(i, updatedUser);
                saveAllUsers(users);
                if (currentUser != null && currentUser.getUserId().equals(updatedUser.getUserId())) {
                    currentUser = updatedUser;
                    saveCurrentUserSession();
                }
                return true;
            }
        }
        return false;
    }
}