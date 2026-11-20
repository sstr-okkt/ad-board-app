package com.example.adboardapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Local database of the app
@Database(
    entities = [AdEntity::class, AdImageEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun adDao(): AdDao
}
