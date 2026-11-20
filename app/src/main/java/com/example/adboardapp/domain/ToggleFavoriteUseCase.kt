package com.example.adboardapp.domain

// Adds or removes an ad from favorites
class ToggleFavoriteUseCase(private val repository: AdRepository) {
    suspend operator fun invoke(id: Int) = repository.toggleFavorite(id)
}
