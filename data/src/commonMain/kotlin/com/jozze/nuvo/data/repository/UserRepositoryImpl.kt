package com.jozze.nuvo.data.repository

import com.jozze.nuvo.core.logging.NuvoLogger
import com.jozze.nuvo.data.remote.UserApi
import com.jozze.nuvo.domain.entity.Address
import com.jozze.nuvo.domain.entity.User
import com.jozze.nuvo.domain.repository.UserRepository

class UserRepositoryImpl(private val api: UserApi) : UserRepository {
    override suspend fun getMyProfile(): Result<User> {
        return try {
            val response = api.getMyProfile()
            val data = response.data
            if (response.success && data != null) {
                NuvoLogger.d(TAG) { "Loaded user profile. uid=${data.uid}" }
                Result.success(data)
            } else {
                val exception = Exception(response.message ?: "Failed to get profile")
                NuvoLogger.e(TAG, exception) { "Failed to load user profile" }
                Result.failure(exception)
            }
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) { "Exception loading user profile" }
            Result.failure(e)
        }
    }

    override suspend fun getMyAddresses(): Result<List<Address>> {
        return try {
            val response = api.getMyAddresses()
            val data = response.data
            if (response.success && data != null) {
                NuvoLogger.d(TAG) { "Loaded addresses. count=${data.size}" }
                Result.success(data)
            } else {
                val exception = Exception(response.message ?: "Failed to get addresses")
                NuvoLogger.e(TAG, exception) { "Failed to load addresses" }
                Result.failure(exception)
            }
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) { "Exception loading addresses" }
            Result.failure(e)
        }
    }

    private companion object {
        const val TAG = "UserRepository"
    }
}
