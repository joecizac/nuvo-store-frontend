package com.jozze.nuvo.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.jozze.nuvo.data.local.dao.CartDao
import com.jozze.nuvo.data.local.entity.CartItemEntity

@Database(entities = [CartItemEntity::class], version = 1)
@ConstructedBy(NuvoDatabaseConstructor::class)
abstract class NuvoDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    companion object {
        const val DB_FILE_NAME = "nuvo.db"
    }
}

// The Room compiler generates the actual implementation.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object NuvoDatabaseConstructor : RoomDatabaseConstructor<NuvoDatabase> {
    override fun initialize(): NuvoDatabase
}
