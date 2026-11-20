package com.example.adboardapp.data.local

import androidx.room.Embedded
import androidx.room.Relation

// Ad together with its photos, loaded in one query
data class AdWithImages(
    @Embedded val ad: AdEntity,
    @Relation(parentColumn = "id", entityColumn = "adId")
    val images: List<AdImageEntity>
)
