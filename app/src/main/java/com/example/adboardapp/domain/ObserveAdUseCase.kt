package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad
import kotlinx.coroutines.flow.Flow

// Streams one cached ad, null when it is missing
class ObserveAdUseCase(private val repository: AdRepository) {
    operator fun invoke(id: Int): Flow<Ad?> = repository.observeAd(id)
}
