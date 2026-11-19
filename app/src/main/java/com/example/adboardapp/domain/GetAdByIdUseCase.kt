package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.Result

// Loads one ad by id for the details screen
class GetAdByIdUseCase(private val repository: AdRepository) {
    suspend operator fun invoke(id: Int): Result<Ad> = repository.getAdById(id)
}
