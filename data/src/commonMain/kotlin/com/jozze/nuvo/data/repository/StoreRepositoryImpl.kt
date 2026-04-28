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
        // Return mock data for testing
        val mockStores = listOf(
            Store(
                id = "1",
                name = "Premium Coffee House",
                description = "The best arabica beans in town.",
                imageUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93",
                latitude = -33.9249,
                longitude = 18.4241,
                rating = 4.8,
                distance = 1.2
            ),
            Store(
                id = "2",
                name = "Fresh Bakery",
                description = "Warm croissants and fresh bread daily.",
                imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff",
                latitude = -33.9250,
                longitude = 18.4250,
                rating = 4.5,
                distance = 2.5
            )
        )
        return Result.success(mockStores)
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
