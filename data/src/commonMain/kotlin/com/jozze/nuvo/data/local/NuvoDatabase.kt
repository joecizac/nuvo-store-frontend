package com.jozze.nuvo.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.jozze.nuvo.data.local.dao.CartDao
import com.jozze.nuvo.data.local.dao.FavouriteDao
import com.jozze.nuvo.data.local.entity.CartItemEntity
import com.jozze.nuvo.data.local.entity.FavouriteProductEntity
import com.jozze.nuvo.data.local.entity.FavouriteStoreEntity

@Database(entities = [CartItemEntity::class, FavouriteStoreEntity::class, FavouriteProductEntity::class], version = 2)
@ConstructedBy(NuvoDatabaseConstructor::class)
abstract class NuvoDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun favouriteDao(): FavouriteDao

    companion object {
        const val DB_FILE_NAME = "nuvo.db"
    }
}

// The Room compiler generates the actual implementation.
expect object NuvoDatabaseConstructor : RoomDatabaseConstructor<NuvoDatabase> {
    override fun initialize(): NuvoDatabase
}
