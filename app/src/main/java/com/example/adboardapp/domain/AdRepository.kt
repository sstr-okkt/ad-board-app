package com.example.adboardapp.domain

import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdStatus
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

    // Streams one ad, null when it is missing
    fun observeAd(id: Int): Flow<Ad?>

    // Adds or removes the ad from favorites
    suspend fun toggleFavorite(id: Int)

    // Counts one more view of the ad
    suspend fun incrementViews(id: Int)

    // Saves a user ad, publish = true makes it visible on the board
    suspend fun saveAd(ad: Ad, publish: Boolean): Result<Int>

    // Removes a user ad with all its photos
    suspend fun deleteAd(id: Int): Result<Unit>

    // Streams the ads created on this device
    fun observeLocalAds(): Flow<List<Ad>>

    // Streams the favorite ads
    fun observeFavorites(): Flow<List<Ad>>

    // Changes the status of a user ad
    suspend fun updateStatus(id: Int, status: AdStatus): Result<Unit>
}
