package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad
import kotlinx.coroutines.flow.Flow

// Streams the favorite ads
class ObserveFavoritesUseCase(private val repository: AdRepository) {
    operator fun invoke(): Flow<List<Ad>> = repository.observeFavorites()
}
