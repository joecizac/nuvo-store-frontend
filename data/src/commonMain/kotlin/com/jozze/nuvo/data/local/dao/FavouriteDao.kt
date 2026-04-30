package com.jozze.nuvo.data.local.dao

import androidx.room.*
import com.jozze.nuvo.data.local.entity.FavouriteProductEntity
import com.jozze.nuvo.data.local.entity.FavouriteStoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Query("SELECT * FROM favourite_stores")
    fun getFavouriteStores(): Flow<List<FavouriteStoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: FavouriteStoreEntity)

    @Query("DELETE FROM favourite_stores WHERE storeId = :storeId")
    suspend fun deleteStore(storeId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_stores WHERE storeId = :storeId)")
    fun isStoreFavourite(storeId: String): Flow<Boolean>

    @Query("SELECT * FROM favourite_products")
    fun getFavouriteProducts(): Flow<List<FavouriteProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: FavouriteProductEntity)

    @Query("DELETE FROM favourite_products WHERE productId = :productId")
    suspend fun deleteProduct(productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_products WHERE productId = :productId)")
    fun isProductFavourite(productId: String): Flow<Boolean>
}
