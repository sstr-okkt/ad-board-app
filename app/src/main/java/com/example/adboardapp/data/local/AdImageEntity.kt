package com.example.adboardapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// One photo of an ad, an ad can have several photos
@Entity(
    tableName = "ad_images",
    foreignKeys = [
        ForeignKey(
            entity = AdEntity::class,
            parentColumns = ["id"],
            childColumns = ["adId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("adId")]
)
data class AdImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adId: Int,
    val url: String,
    val position: Int
)
