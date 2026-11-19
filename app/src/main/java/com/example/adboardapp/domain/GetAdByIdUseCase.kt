package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad

// Loads one ad by id for the details screen
class GetAdByIdUseCase(private val repository: AdRepository) {
    suspend operator fun invoke(id: Int): Ad? = repository.getAdById(id)
}
