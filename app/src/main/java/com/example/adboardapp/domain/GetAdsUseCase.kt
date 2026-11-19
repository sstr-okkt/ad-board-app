package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.Result

// Loads the list of ads for the first screen
class GetAdsUseCase(private val repository: AdRepository) {
    suspend operator fun invoke(): Result<List<Ad>> = repository.getAds()
}
