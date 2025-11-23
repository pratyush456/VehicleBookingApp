package com.vehiclebooking.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.vehiclebooking.UserRole;
import com.vehiclebooking.data.model.UserEntity;
import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();

    @Query("SELECT * FROM users WHERE userId = :userId")
    UserEntity getUserById(String userId);

    @Query("SELECT * FROM users WHERE username = :username")
    UserEntity getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE email = :email")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE role = :role")
    List<UserEntity> getUsersByRole(UserRole role);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(List<UserEntity> users);

    @Update
    void updateUser(UserEntity user);

    @Query("DELETE FROM users")
    void deleteAllUsers();
}
