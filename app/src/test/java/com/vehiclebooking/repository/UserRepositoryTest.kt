package com.vehiclebooking.repository

import android.content.Context
import com.vehiclebooking.User
import com.vehiclebooking.data.dao.UserDao
import com.vehiclebooking.data.model.UserEntity
import com.vehiclebooking.data.repository.UserRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserRepositoryTest {

    private lateinit var repository: UserRepository
    private lateinit var mockContext: Context
    private lateinit var mockDao: UserDao

    @BeforeEach
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockDao = mockk(relaxed = true)
        
        mockkStatic("com.vehiclebooking.data.AppDatabase")
        val mockDatabase = mockk<com.vehiclebooking.data.AppDatabase>(relaxed = true)
        every { com.vehiclebooking.data.AppDatabase.getDatabase(any()) } returns mockDatabase
        every { mockDatabase.userDao() } returns mockDao
        
        repository = UserRepository(mockContext)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUserByUsername returns user when exists`() = runTest {
        // Given
        val userEntity = UserEntity("testuser", "test@example.com", "password123")
        coEvery { mockDao.getUserByUsername("testuser") } returns userEntity

        // When
        val result = repository.getUserByUsername("testuser")

        // Then
        assertNotNull(result)
        assertEquals("testuser", result?.username)
        assertEquals("test@example.com", result?.email)
    }

    @Test
    fun `getUserByUsername returns null when not exists`() = runTest {
        // Given
        coEvery { mockDao.getUserByUsername("nonexistent") } returns null

        // When
        val result = repository.getUserByUsername("nonexistent")

        // Then
        assertNull(result)
    }

    @Test
    fun `insertUser calls dao insert`() = runTest {
        // Given
        val user = User("newuser", "new@example.com", "password123")
        coEvery { mockDao.insertUser(any()) } just Runs

        // When
        repository.insertUser(user)

        // Then
        coVerify(exactly = 1) { mockDao.insertUser(any()) }
    }

    @Test
    fun `isUsernameExists returns true when username exists`() = runTest {
        // Given
        val userEntity = UserEntity("existinguser", "test@example.com", "password123")
        coEvery { mockDao.getUserByUsername("existinguser") } returns userEntity

        // When
        val result = repository.isUsernameExists("existinguser")

        // Then
        assertTrue(result)
    }

    @Test
    fun `isUsernameExists returns false when username does not exist`() = runTest {
        // Given
        coEvery { mockDao.getUserByUsername("newuser") } returns null

        // When
        val result = repository.isUsernameExists("newuser")

        // Then
        assertFalse(result)
    }

    @Test
    fun `isEmailExists returns true when email exists`() = runTest {
        // Given
        val userEntity = UserEntity("user", "existing@example.com", "password123")
        coEvery { mockDao.getUserByEmail("existing@example.com") } returns userEntity

        // When
        val result = repository.isEmailExists("existing@example.com")

        // Then
        assertTrue(result)
    }

    @Test
    fun `getUserByCredentials returns user with valid credentials`() = runTest {
        // Given
        val userEntity = UserEntity("testuser", "test@example.com", "password123")
        coEvery { mockDao.getUserByCredentials("testuser", "password123") } returns userEntity

        // When
        val result = repository.getUserByCredentials("testuser", "password123")

        // Then
        assertNotNull(result)
        assertEquals("testuser", result?.username)
    }

    @Test
    fun `getUserByCredentials returns null with invalid credentials`() = runTest {
        // Given
        coEvery { mockDao.getUserByCredentials("testuser", "wrongpassword") } returns null

        // When
        val result = repository.getUserByCredentials("testuser", "wrongpassword")

        // Then
        assertNull(result)
    }
}
