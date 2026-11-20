package com.example.adboardapp.di

import com.example.adboardapp.data.AdRepositoryImpl
import com.example.adboardapp.domain.AdRepository
import org.koin.dsl.module

// Binds the repository interface to its implementation
val repositoryModule = module {
    single<AdRepository> { AdRepositoryImpl(get(), get()) }
}
