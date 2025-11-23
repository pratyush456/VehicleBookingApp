package com.vehiclebooking.data.dao

import androidx.room.*
import com.vehiclebooking.UserRole
import com.vehiclebooking.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun getUserByCredentials(username: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: UserRole): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>
    
    // Blocking wrappers for Java compatibility
    fun getAllUsersBlocking(): List<UserEntity> = runBlocking {
        getAllUsers().first()
    }
    
    fun getUserByUsernameBlocking(username: String): UserEntity? = runBlocking {
        getUserByUsername(username)
    }
    
    fun getUserByEmailBlocking(email: String): UserEntity? = runBlocking {
        getUserByEmail(email)
    }
    
    fun insertUserBlocking(user: UserEntity) = runBlocking {
        insertUser(user)
    }
    
    fun updateUserBlocking(user: UserEntity) = runBlocking {
        updateUser(user)
    }
}
