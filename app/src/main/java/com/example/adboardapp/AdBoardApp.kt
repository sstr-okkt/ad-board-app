package com.example.adboardapp

import android.app.Application
import com.example.adboardapp.di.databaseModule
import com.example.adboardapp.di.networkModule
import com.example.adboardapp.di.repositoryModule
import com.example.adboardapp.di.settingsModule
import com.example.adboardapp.di.useCaseModule
import com.example.adboardapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

// Starts Koin with all modules of the app
class AdBoardApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AdBoardApp)
            modules(
                networkModule,
                databaseModule,
                settingsModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}
