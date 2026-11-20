package com.example.adboardapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Local access to the ads table
@Dao
abstract class AdDao {

    @Transaction
    @Query("SELECT * FROM ads ORDER BY publishedAt DESC, id")
    abstract fun observeAll(): Flow<List<AdWithImages>>

    @Transaction
    @Query("SELECT * FROM ads WHERE source = :source ORDER BY publishedAt DESC, id")
    abstract fun observeBySource(source: String): Flow<List<AdWithImages>>

    @Transaction
    @Query("SELECT * FROM ads WHERE isFavorite = 1 ORDER BY publishedAt DESC, id")
    abstract fun observeFavorites(): Flow<List<AdWithImages>>

    @Transaction
    @Query("SELECT * FROM ads WHERE id = :id")
    abstract suspend fun getById(id: Int): AdWithImages?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(ads: List<AdEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(ad: AdEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertImages(images: List<AdImageEntity>)

    @Update
    abstract suspend fun update(ad: AdEntity)

    @Query("DELETE FROM ads WHERE id = :id")
    abstract suspend fun deleteById(id: Int)

    @Query("DELETE FROM ads WHERE source = 'REMOTE'")
    abstract suspend fun deleteRemote()

    @Query("DELETE FROM ad_images WHERE adId = :adId")
    abstract suspend fun deleteImages(adId: Int)

    @Query("UPDATE ads SET isFavorite = :value WHERE id = :id")
    abstract suspend fun setFavorite(id: Int, value: Boolean)

    @Query("UPDATE ads SET views = views + 1 WHERE id = :id")
    abstract suspend fun incrementViews(id: Int)

    @Query("SELECT MIN(id) FROM ads WHERE id < 0")
    abstract suspend fun minLocalId(): Int?

    // Local ads get negative ids so they never clash with the remote ones
    suspend fun nextLocalId(): Int = (minLocalId() ?: 0) - 1

    // Replaces the network part of the cache and keeps the user ads
    @Transaction
    open suspend fun replaceRemote(ads: List<AdEntity>, images: List<AdImageEntity>) {
        deleteRemote()
        insertAll(ads)
        insertImages(images)
    }
}
