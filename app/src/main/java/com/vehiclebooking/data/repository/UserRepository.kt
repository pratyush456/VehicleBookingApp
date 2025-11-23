package com.vehiclebooking.data.repository

import android.content.Context
import com.vehiclebooking.User
import com.vehiclebooking.data.AppDatabase
import com.vehiclebooking.data.dao.UserDao
import com.vehiclebooking.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(context: Context) {
    private val userDao: UserDao = AppDatabase.getDatabase(context).userDao()

    // Flow-based reactive queries
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
        .map { entities -> entities.map { it.toUser() } }

    val userCount: Flow<Int> = userDao.getUserCount()

    // Suspend functions for queries and write operations
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)?.toUser()
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)?.toUser()
    }

    suspend fun getUserByCredentials(username: String, password: String): User? {
        return userDao.getUserByCredentials(username, password)?.toUser()
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(UserEntity(user))
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(UserEntity(user))
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(UserEntity(user))
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }

    // Helper function to check if username exists
    suspend fun isUsernameExists(username: String): Boolean {
        return userDao.getUserByUsername(username) != null
    }

    // Helper function to check if email exists
    suspend fun isEmailExists(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }
}
