package com.jozze.nuvo.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    fun getFavouriteStores(): Flow<List<String>>
    suspend fun toggleStoreFavourite(storeId: String)
    fun isStoreFavourite(storeId: String): Flow<Boolean>

    fun getFavouriteProducts(): Flow<List<String>>
    suspend fun toggleProductFavourite(productId: String)
    fun isProductFavourite(productId: String): Flow<Boolean>
}
