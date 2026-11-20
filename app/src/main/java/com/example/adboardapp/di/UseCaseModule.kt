package com.example.adboardapp.di

import com.example.adboardapp.domain.DeleteAdUseCase
import com.example.adboardapp.domain.GetAdByIdUseCase
import com.example.adboardapp.domain.IncrementViewsUseCase
import com.example.adboardapp.domain.ObserveAdUseCase
import com.example.adboardapp.domain.ObserveAdsUseCase
import com.example.adboardapp.domain.RefreshAdsUseCase
import com.example.adboardapp.domain.SaveAdUseCase
import com.example.adboardapp.domain.ToggleFavoriteUseCase
import org.koin.dsl.module

// Provides the use cases
val useCaseModule = module {
    factory { ObserveAdsUseCase(get()) }
    factory { ObserveAdUseCase(get()) }
    factory { RefreshAdsUseCase(get()) }
    factory { GetAdByIdUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    factory { IncrementViewsUseCase(get()) }
    factory { SaveAdUseCase(get()) }
    factory { DeleteAdUseCase(get()) }
}
