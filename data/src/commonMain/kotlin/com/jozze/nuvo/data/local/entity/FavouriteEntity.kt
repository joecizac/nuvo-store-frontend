package com.jozze.nuvo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_stores")
data class FavouriteStoreEntity(
    @PrimaryKey val storeId: String
)

@Entity(tableName = "favourite_products")
data class FavouriteProductEntity(
    @PrimaryKey val productId: String
)
