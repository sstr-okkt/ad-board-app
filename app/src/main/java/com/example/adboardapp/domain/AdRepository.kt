package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad

// Contract for ad data, implemented in the data layer
interface AdRepository {
    suspend fun getAds(): List<Ad>
    suspend fun getAdById(id: Int): Ad?
}
