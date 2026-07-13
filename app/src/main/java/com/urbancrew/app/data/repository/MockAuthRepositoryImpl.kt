package com.urbancrew.app.data.repository

import com.urbancrew.app.data.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        delay(1000)
        val user = User("mock_123", "google_user@gmail.com", "Google User", "Customer")
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        delay(1000)
        if (email.contains("error")) return Result.failure(Exception("Invalid credentials"))
        val user = User("mock_123", email, "Test User", "Customer")
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun signUp(name: String, email: String, password: String, role: String): Result<User> {
        delay(1000)
        val user = User("mock_123", email, name, role)
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        delay(1000)
        return Result.success(Unit)
    }

    override suspend fun logout() {
        _currentUser.value = null
    }

    override suspend fun isUserLoggedIn(): Boolean = _currentUser.value != null
}
