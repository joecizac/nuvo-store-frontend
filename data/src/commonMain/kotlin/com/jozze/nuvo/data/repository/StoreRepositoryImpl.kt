package com.jozze.nuvo.data.repository

import com.jozze.nuvo.data.remote.StoreApi
import com.jozze.nuvo.domain.entity.Store
import com.jozze.nuvo.domain.repository.StoreRepository

class StoreRepositoryImpl(private val api: StoreApi) : StoreRepository {
    override suspend fun getNearbyStores(
        lat: Double,
        lng: Double,
        radius: Int
    ): Result<List<Store>> {
        return try {
            val response = api.getNearbyStores(lat, lng, radius)
            val data = response.data
            if (response.success && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to get nearby stores"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStoreById(id: String): Result<Store> {
        return try {
            val response = api.getStoreById(id)
            val data = response.data
            if (response.success && data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to get store details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
