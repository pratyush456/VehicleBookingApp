package com.vehiclebooking;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.vehiclebooking.data.AppDatabase;
import com.vehiclebooking.data.dao.UserDao;
import com.vehiclebooking.data.model.UserEntity;
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
        List<UserEntity> users = userDao.getAllUsers();
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
        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        UserEntity userEntity = userDao.getUserByUsername(username);
        
        if (userEntity != null && 
            userEntity.password.equals(password) && 
            userEntity.isActive) {
            
            currentUser = userEntity.toUser();
            saveCurrentUserSession();
            return true;
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
            UserDao userDao = AppDatabase.getDatabase(context).userDao();
            userDao.insertUser(new UserEntity(user));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all users
    private List<User> getAllUsers() {
        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        List<UserEntity> entities = userDao.getAllUsers();
        List<User> users = new ArrayList<>();
        for (UserEntity entity : entities) {
            users.add(entity.toUser());
        }
        return users;
    }
    
    // Check if username exists
    private boolean isUsernameExists(String username) {
        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        return userDao.getUserByUsername(username) != null;
    }
    
    // Check if email exists
    private boolean isEmailExists(String email) {
        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        return userDao.getUserByEmail(email) != null;
    }
    
    // Save current user session
    private void saveCurrentUserSession() {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = GsonProvider.getGson();
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
                Gson gson = GsonProvider.getGson();
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
            userDao.updateUser(new UserEntity(updatedUser));
            
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