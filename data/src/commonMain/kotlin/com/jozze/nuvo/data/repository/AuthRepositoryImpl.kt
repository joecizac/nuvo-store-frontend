package com.jozze.nuvo.data.repository

import com.jozze.nuvo.core.logging.NuvoLogger
import com.jozze.nuvo.core.network.TokenProvider
import com.jozze.nuvo.domain.entity.User
import com.jozze.nuvo.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlin.math.absoluteValue

class AuthRepositoryImpl : AuthRepository, TokenProvider {
    private val _currentUser = MutableStateFlow<User?>(null)

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun getToken(): String? {
        val token = if (_currentUser.value != null) {
            "mock-jwt-token-${kotlin.random.Random.nextLong().absoluteValue}"
        } else {
            null
        }
        NuvoLogger.d(TAG) { "Token requested. available=${token != null}" }
        return token
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        NuvoLogger.d(TAG) { "Login requested for email=$email" }
        // Mock login
        val user = User(
            uid = "123",
            email = email,
            name = "Test User",
            phoneNumber = null,
            profileImageUrl = null
        )
        _currentUser.value = user
        NuvoLogger.i(TAG) { "Login succeeded for uid=${user.uid}" }
        return Result.success(user)
    }

    override suspend fun logout() {
        _currentUser.value = null
        NuvoLogger.i(TAG) { "User logged out" }
    }

    private companion object {
        const val TAG = "AuthRepository"
    }
}
