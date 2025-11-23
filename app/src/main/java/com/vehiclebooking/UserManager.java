package com.vehiclebooking;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.vehiclebooking.data.AppDatabase;
import com.vehiclebooking.data.dao.UserDao;
import com.vehiclebooking.data.model.UserEntity;
import com.vehiclebooking.security.PasswordHasher;
import com.vehiclebooking.security.SecurePreferences;
import com.vehiclebooking.security.SecurityLogger;
import com.vehiclebooking.utils.GsonProvider;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String PREFERENCES_NAME = "vehicle_booking_users";
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
        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        List<UserEntity> users = userDao.getAllUsersBlocking();
        if (users.isEmpty()) {
            // Create default admin user with hashed password
            String hashedPassword = PasswordHasher.INSTANCE.hash("admin123");
            User admin = new User("admin", "admin@vehiclebooking.com", "1234567890", 
                                hashedPassword, UserRole.ADMIN, "System Administrator");
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
        
        // Hash password before storing
        String hashedPassword = PasswordHasher.INSTANCE.hash(password);
        User newUser = new User(username, email, phoneNumber, hashedPassword, role, fullName);
        boolean success = saveUser(newUser);
        
        if (success) {
            SecurityLogger.INSTANCE.logEvent(context, 
                SecurityLogger.SecurityEvent.ACCOUNT_CREATED, username, "Role: " + role);
        }
        
        return success;
    }
    
    // User Login
    public boolean login(String username, String password) {
        // Check if account is locked
        if (SecurityLogger.INSTANCE.isAccountLocked(context, username)) {
            int remainingMinutes = SecurityLogger.INSTANCE.getRemainingLockoutTime(context, username);
            android.widget.Toast.makeText(context, 
                "Account locked. Try again in " + remainingMinutes + " minutes.", 
                android.widget.Toast.LENGTH_LONG).show();
            return false;
        }
        
        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        UserEntity userEntity = userDao.getUserByUsernameBlocking(username);
        
        if (userEntity != null && 
            userEntity.isActive && 
            PasswordHasher.INSTANCE.verify(password, userEntity.password)) {
            
            // Successful login
            currentUser = userEntity.toUser();
            saveCurrentUserSession();
            SecurityLogger.INSTANCE.logSuccessfulLogin(context, username);
            return true;
        }
        
        // Failed login
        SecurityLogger.INSTANCE.logFailedLogin(context, username, 
            userEntity == null ? "User not found" : "Invalid password");
        return false;
    }
    
    // Logout
    public void logout() {
        if (currentUser != null) {
            SecurityLogger.INSTANCE.logEvent(context, 
                SecurityLogger.SecurityEvent.LOGOUT, currentUser.getUsername());
        }
        currentUser = null;
        clearCurrentUserSession();
    }
    
    // Check if user is logged in
    public boolean isLoggedIn() {
        return SecurePreferences.INSTANCE.getBoolean(IS_LOGGED_IN_KEY, false) && currentUser != null;
    }
    
    // Get current logged in user
    public User getCurrentUser() {
        return currentUser;
    }
    
    // Save user to storage
    private boolean saveUser(User user) {
        try {
            UserDao userDao = AppDatabase.getDatabase(context).userDao();
            userDao.insertUserBlocking(new UserEntity(user));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all users
    private List<User> getAllUsers() {
        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        List<UserEntity> entities = userDao.getAllUsersBlocking();
        List<User> users = new ArrayList<>();
        for (UserEntity entity : entities) {
            users.add(entity.toUser());
        }
        return users;
    }
    
    // Check if username exists
    private boolean isUsernameExists(String username) {
        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        return userDao.getUserByUsernameBlocking(username) != null;
    }
    
    // Check if email exists
    private boolean isEmailExists(String email) {
        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        return userDao.getUserByEmailBlocking(email) != null;
    }
    
    // Save current user session
    private void saveCurrentUserSession() {
        Gson gson = GsonProvider.getGson();
        String userJson = gson.toJson(currentUser);
        SecurePreferences.INSTANCE.putString(CURRENT_USER_KEY, userJson);
        SecurePreferences.INSTANCE.putBoolean(IS_LOGGED_IN_KEY, true);
    }
    
    // Load current user session
    private void loadCurrentUser() {
        if (SecurePreferences.INSTANCE.getBoolean(IS_LOGGED_IN_KEY, false)) {
            String userJson = SecurePreferences.INSTANCE.getString(CURRENT_USER_KEY, "");
            if (userJson != null && !userJson.isEmpty()) {
                Gson gson = GsonProvider.getGson();
                currentUser = gson.fromJson(userJson, User.class);
            }
        }
    }
    
    // Clear current user session
    private void clearCurrentUserSession() {
        SecurePreferences.INSTANCE.remove(CURRENT_USER_KEY);
        SecurePreferences.INSTANCE.putBoolean(IS_LOGGED_IN_KEY, false);
    }
    
    // Get users by role (for admin to manage)
    public List<User> getUsersByRole(UserRole role) {
        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        List<UserEntity> entities = userDao.getUsersByRole(role);
        List<User> users = new ArrayList<>();
        for (UserEntity entity : entities) {
            users.add(entity.toUser());
        }
        return users;
    }
    
    // Update user (for profile updates)
    public boolean updateUser(User updatedUser) {
        try {
            UserDao userDao = AppDatabase.getDatabase(context).userDao();
            userDao.updateUserBlocking(new UserEntity(updatedUser));
            
            if (currentUser != null && currentUser.getUserId().equals(updatedUser.getUserId())) {
                currentUser = updatedUser;
                saveCurrentUserSession();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}