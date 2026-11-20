package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.Result

// Creates or updates a user ad
class SaveAdUseCase(private val repository: AdRepository) {
    suspend operator fun invoke(ad: Ad, publish: Boolean): Result<Int> =
        repository.saveAd(ad, publish)
}
