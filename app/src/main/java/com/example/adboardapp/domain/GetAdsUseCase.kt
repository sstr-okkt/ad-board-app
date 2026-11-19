package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad

// Loads the list of ads for the first screen
class GetAdsUseCase(private val repository: AdRepository) {
    suspend operator fun invoke(): List<Ad> = repository.getAds()
}
