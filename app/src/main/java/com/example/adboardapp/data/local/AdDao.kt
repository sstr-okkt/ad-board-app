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

    @Transaction
    @Query("SELECT * FROM ads WHERE id = :id")
    abstract fun observeById(id: Int): Flow<AdWithImages?>

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

    @Query("UPDATE ads SET isFavorite = NOT isFavorite WHERE id = :id")
    abstract suspend fun toggleFavorite(id: Int)

    @Query("UPDATE ads SET views = views + 1 WHERE id = :id")
    abstract suspend fun incrementViews(id: Int)

    @Query("SELECT MIN(id) FROM ads WHERE id < 0")
    abstract suspend fun minLocalId(): Int?

    @Query("SELECT id, views, isFavorite FROM ads WHERE source = 'REMOTE'")
    abstract suspend fun remoteStates(): List<AdStateRow>

    // Local ads get negative ids so they never clash with the remote ones
    suspend fun nextLocalId(): Int = (minLocalId() ?: 0) - 1

    // Replaces the network part of the cache and keeps the user ads
    @Transaction
    open suspend fun replaceRemote(ads: List<AdEntity>, images: List<AdImageEntity>) {
        // Favorites and views belong to the user, the network must not reset them
        val states = remoteStates().associateBy { it.id }
        deleteRemote()
        insertAll(
            ads.map { ad ->
                val state = states[ad.id]
                if (state == null) ad else ad.copy(
                    views = state.views,
                    isFavorite = state.isFavorite
                )
            }
        )
        insertImages(images)
    }

    // Replaces all photos of one ad
    @Transaction
    open suspend fun replaceAdImages(adId: Int, images: List<AdImageEntity>) {
        deleteImages(adId)
        insertImages(images)
    }
}
