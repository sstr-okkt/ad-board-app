package com.example.adboardapp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.example.adboardapp.di.databaseModule
import com.example.adboardapp.di.networkModule
import com.example.adboardapp.di.repositoryModule
import com.example.adboardapp.di.settingsModule
import com.example.adboardapp.di.useCaseModule
import com.example.adboardapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

// Starts Koin with all modules of the app
class AdBoardApp : Application(), SingletonImageLoader.Factory {

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

    // Smooth image appearance across all screens
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .crossfade(100)
            .build()
}
