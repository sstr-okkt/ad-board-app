package com.example.adboardapp.domain

import com.example.adboardapp.model.Result

// Triggers the offline-first synchronization
class RefreshAdsUseCase(private val repository: AdRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.refresh()
}
