package com.example.adboardapp.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.adboardapp.model.legacyCategoryKeys
import com.example.adboardapp.model.legacyCityKeys

// Replaces old Ukrainian category and city names with canonical keys
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        legacyCategoryKeys.forEach { (oldValue, key) ->
            db.execSQL(
                "UPDATE ads SET category = ? WHERE category = ?",
                arrayOf(key, oldValue)
            )
        }
        legacyCityKeys.forEach { (oldValue, key) ->
            db.execSQL(
                "UPDATE ads SET city = ? WHERE city = ?",
                arrayOf(key, oldValue)
            )
        }
    }
}
