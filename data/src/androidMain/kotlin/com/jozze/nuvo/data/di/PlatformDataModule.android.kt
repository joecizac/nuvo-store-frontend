package com.jozze.nuvo.data.di

import androidx.room.Room
import com.jozze.nuvo.data.local.NuvoDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformDataModule = module {
    single<NuvoDatabase> {
        val context = androidContext()
        val dbFile = context.getDatabasePath(NuvoDatabase.DB_FILE_NAME)
        Room.databaseBuilder<NuvoDatabase>(
            context = context,
            name = dbFile.absolutePath
        ).build()
    }
}
