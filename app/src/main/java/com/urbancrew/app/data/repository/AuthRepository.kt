package com.urbancrew.app.data.repository

import com.urbancrew.app.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface for Authentication operations.
 * Shared logic that will be mirrored in the iOS app.
 */
interface AuthRepository {
    val currentUser: Flow<User?>
    
    suspend fun loginWithGoogle(idToken: String, role: String): Result<User>
    
    suspend fun loginWithEmail(email: String, password: String): Result<User>
    
    suspend fun signUp(name: String, email: String, password: String, role: String): Result<User>
    
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    
    suspend fun logout()
    
    suspend fun isUserLoggedIn(): Boolean
}
