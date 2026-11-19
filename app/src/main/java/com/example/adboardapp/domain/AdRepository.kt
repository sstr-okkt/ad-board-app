package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.Result

// Contract for ad data, implemented in the data layer
interface AdRepository {
    suspend fun getAds(): Result<List<Ad>>
    suspend fun getAdById(id: Int): Result<Ad>
}
