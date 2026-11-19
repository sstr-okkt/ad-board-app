package com.example.adboardapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room representation of an ad, author fields are flattened
@Entity(tableName = "ads")
data class AdEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val price: Double,
    val category: String,
    val city: String,
    val views: Int,
    val status: String,
    val authorName: String,
    val authorRating: Double,
    val authorContact: String?,
    val publishedAt: String?
)
