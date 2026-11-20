package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad
import kotlinx.coroutines.flow.Flow

// Streams the ads created on this device
class ObserveLocalAdsUseCase(private val repository: AdRepository) {
    operator fun invoke(): Flow<List<Ad>> = repository.observeLocalAds()
}
