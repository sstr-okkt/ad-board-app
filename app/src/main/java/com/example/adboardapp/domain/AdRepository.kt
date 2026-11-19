package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.Result
import kotlinx.coroutines.flow.Flow

// Contract for ad data, implemented in the data layer
interface AdRepository {

    // Always reads from the local cache
    fun observeAds(): Flow<List<Ad>>

    // Loads fresh data from the network and stores it in the cache
    suspend fun refresh(): Result<Unit>

    // Reads one ad from the local cache
    suspend fun getAdById(id: Int): Result<Ad>
}
