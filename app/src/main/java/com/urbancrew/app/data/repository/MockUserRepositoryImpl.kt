package com.urbancrew.app.data.repository

import com.urbancrew.app.data.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockUserRepositoryImpl @Inject constructor() : UserRepository {

    private val _userFlow = MutableStateFlow<User?>(null)

    override suspend fun getUserProfile(uid: String): Result<User> {
        delay(500)
        val user = User(uid, "test@urbancrew.com", "Test User", "Customer")
        return Result.success(user)
    }

    override suspend fun updateProfile(user: User): Result<Unit> {
        delay(500)
        _userFlow.value = user
        return Result.success(Unit)
    }

    override fun getProfileFlow(uid: String): Flow<User?> = _userFlow
}
