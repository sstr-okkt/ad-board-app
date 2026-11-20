package com.example.adboardapp.di

import com.example.adboardapp.data.settings.LanguageManager
import com.example.adboardapp.data.settings.ThemeManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

// Provides the local settings
val settingsModule = module {
    single { ThemeManager(androidContext()) }
    single { LanguageManager(androidContext()) }
}
