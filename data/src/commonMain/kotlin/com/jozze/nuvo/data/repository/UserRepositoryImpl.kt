package com.jozze.nuvo.data.repository

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
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to get profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyAddresses(): Result<List<Address>> {
        return try {
            val response = api.getMyAddresses()
            val data = response.data
            if (response.success && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to get addresses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
