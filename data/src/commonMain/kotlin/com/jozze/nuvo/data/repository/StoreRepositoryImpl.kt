package com.jozze.nuvo.data.repository

import com.jozze.nuvo.core.logging.NuvoLogger
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
                NuvoLogger.d(TAG) {
                    "Loaded nearby stores. lat=$lat, lng=$lng, radius=$radius, count=${data.size}"
                }
                Result.success(data)
            } else {
                val exception = Exception(response.message ?: "Failed to get nearby stores")
                NuvoLogger.e(TAG, exception) {
                    "Failed to load nearby stores. lat=$lat, lng=$lng, radius=$radius"
                }
                Result.failure(exception)
            }
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) {
                "Exception loading nearby stores. lat=$lat, lng=$lng, radius=$radius"
            }
            Result.failure(e)
        }
    }

    override suspend fun getStoreById(id: String): Result<Store> {
        return try {
            val response = api.getStoreById(id)
            val data = response.data
            if (response.success && data != null) {
                NuvoLogger.d(TAG) { "Loaded store. store=$id" }
                Result.success(data)
            } else {
                val exception = Exception(response.message ?: "Failed to get store details")
                NuvoLogger.e(TAG, exception) { "Failed to load store. store=$id" }
                Result.failure(exception)
            }
        } catch (e: Exception) {
            NuvoLogger.e(TAG, e) { "Exception loading store. store=$id" }
            Result.failure(e)
        }
    }

    private companion object {
        const val TAG = "StoreRepository"
    }
}
