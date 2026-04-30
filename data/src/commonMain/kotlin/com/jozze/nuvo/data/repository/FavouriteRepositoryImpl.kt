package com.jozze.nuvo.data.repository

import com.jozze.nuvo.data.local.dao.FavouriteDao
import com.jozze.nuvo.data.local.entity.FavouriteProductEntity
import com.jozze.nuvo.data.local.entity.FavouriteStoreEntity
import com.jozze.nuvo.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavouriteRepositoryImpl(
    private val favouriteDao: FavouriteDao
) : FavouriteRepository {

    override fun getFavouriteStores(): Flow<List<String>> =
        favouriteDao.getFavouriteStores().map { entities -> entities.map { it.storeId } }

    override suspend fun toggleStoreFavourite(storeId: String) {
        val isFavourite = favouriteDao.isStoreFavourite(storeId).first()
        if (isFavourite) {
            favouriteDao.deleteStore(storeId)
        } else {
            favouriteDao.insertStore(FavouriteStoreEntity(storeId))
        }
    }

    override fun isStoreFavourite(storeId: String): Flow<Boolean> =
        favouriteDao.isStoreFavourite(storeId)

    override fun getFavouriteProducts(): Flow<List<String>> =
        favouriteDao.getFavouriteProducts().map { entities -> entities.map { it.productId } }

    override suspend fun toggleProductFavourite(productId: String) {
        val isFavourite = favouriteDao.isProductFavourite(productId).first()
        if (isFavourite) {
            favouriteDao.deleteProduct(productId)
        } else {
            favouriteDao.insertProduct(FavouriteProductEntity(productId))
        }
    }

    override fun isProductFavourite(productId: String): Flow<Boolean> =
        favouriteDao.isProductFavourite(productId)
}
