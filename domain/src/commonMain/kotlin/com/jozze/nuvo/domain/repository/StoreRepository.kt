package com.jozze.nuvo.domain.repository

import com.jozze.nuvo.domain.entity.Store

interface StoreRepository {
    suspend fun getNearbyStores(lat: Double, lng: Double, radius: Int): Result<List<Store>>
    suspend fun getStoreById(id: String): Result<Store>
}
