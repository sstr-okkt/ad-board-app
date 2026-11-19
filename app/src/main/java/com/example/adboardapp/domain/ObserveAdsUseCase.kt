package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad
import kotlinx.coroutines.flow.Flow

// Streams the cached ads for the first screen
class ObserveAdsUseCase(private val repository: AdRepository) {
    operator fun invoke(): Flow<List<Ad>> = repository.observeAds()
}
