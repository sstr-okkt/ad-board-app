package com.example.adboardapp.domain

import com.example.adboardapp.model.Result

// Deletes a user ad with all its photos
class DeleteAdUseCase(private val repository: AdRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.deleteAd(id)
}
