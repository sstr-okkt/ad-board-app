package com.example.adboardapp.domain

import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Result

// Publishes or archives a user ad
class UpdateAdStatusUseCase(private val repository: AdRepository) {
    suspend operator fun invoke(id: Int, status: AdStatus): Result<Unit> =
        repository.updateStatus(id, status)
}
