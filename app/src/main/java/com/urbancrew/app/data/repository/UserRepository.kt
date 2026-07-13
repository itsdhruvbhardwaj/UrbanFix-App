package com.urbancrew.app.data.repository

import com.urbancrew.app.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Handles shared user profile data. 
 * Any data updated here will be immediately reflected in the iOS app as well.
 */
interface UserRepository {
    suspend fun getUserProfile(uid: String): Result<User>
    suspend fun updateProfile(user: User): Result<Unit>
    fun getProfileFlow(uid: String): Flow<User?>
}
