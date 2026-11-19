package com.example.adboardapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Local access to the ads table
@Dao
interface AdDao {

    @Query("SELECT * FROM ads ORDER BY id")
    fun getAll(): Flow<List<AdEntity>>

    @Query("SELECT * FROM ads WHERE id = :id")
    suspend fun getById(id: Int): AdEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ads: List<AdEntity>)

    @Query("DELETE FROM ads")
    suspend fun clear()
}
