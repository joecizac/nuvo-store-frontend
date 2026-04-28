package com.jozze.nuvo.domain.repository

import com.jozze.nuvo.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun getToken(): String?
    suspend fun loginWithEmail(email: String, password: String): Result<User>
    suspend fun logout()
}
