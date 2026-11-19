package com.example.adboardapp

import android.app.Application
import androidx.room.Room
import com.example.adboardapp.data.AdRepositoryImpl
import com.example.adboardapp.data.local.AppDatabase
import com.example.adboardapp.data.remote.NetworkModule
import com.example.adboardapp.domain.AdRepository

// Holds app-wide singletons, a simple replacement for a DI framework
class AdBoardApp : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "app.db").build()
    }

    val repository: AdRepository by lazy {
        AdRepositoryImpl(NetworkModule.productsApi, database.adDao())
    }
}
