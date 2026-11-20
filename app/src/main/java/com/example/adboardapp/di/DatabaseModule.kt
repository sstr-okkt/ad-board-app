package com.example.adboardapp.di

import androidx.room.Room
import com.example.adboardapp.data.local.AdDao
import com.example.adboardapp.data.local.AppDatabase
import com.example.adboardapp.data.local.MIGRATION_2_3
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

// Provides the local database and its DAO
val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app.db")
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single<AdDao> { get<AppDatabase>().adDao() }
}

