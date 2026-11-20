package com.example.adboardapp.di

import com.example.adboardapp.domain.GetAdByIdUseCase
import com.example.adboardapp.domain.ObserveAdsUseCase
import com.example.adboardapp.domain.RefreshAdsUseCase
import com.example.adboardapp.domain.ToggleFavoriteUseCase
import org.koin.dsl.module

// Provides the use cases
val useCaseModule = module {
    factory { ObserveAdsUseCase(get()) }
    factory { RefreshAdsUseCase(get()) }
    factory { GetAdByIdUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
}
