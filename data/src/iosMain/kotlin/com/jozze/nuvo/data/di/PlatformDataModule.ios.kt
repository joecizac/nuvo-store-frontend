package com.jozze.nuvo.data.di

import androidx.room.Room
import com.jozze.nuvo.data.local.NuvoDatabase
import com.jozze.nuvo.data.local.NuvoDatabaseConstructor
import platform.Foundation.NSHomeDirectory
import org.koin.dsl.module

actual val platformDataModule = module {
    single<NuvoDatabase> {
        val dbFilePath = NSHomeDirectory() + "/" + NuvoDatabase.DB_FILE_NAME
        Room.databaseBuilder<NuvoDatabase>(
            name = dbFilePath,
            factory = { NuvoDatabaseConstructor.initialize() }
        ).build()
    }
}
