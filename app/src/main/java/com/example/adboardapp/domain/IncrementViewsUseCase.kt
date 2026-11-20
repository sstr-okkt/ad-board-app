package com.example.adboardapp.domain

// Counts one more view when the details screen is opened
class IncrementViewsUseCase(private val repository: AdRepository) {
    suspend operator fun invoke(id: Int) = repository.incrementViews(id)
}
