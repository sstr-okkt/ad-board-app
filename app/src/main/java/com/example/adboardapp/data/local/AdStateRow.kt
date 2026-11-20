package com.example.adboardapp.data.local

// Local counters of a remote ad, kept between syncs
data class AdStateRow(
    val id: Int,
    val views: Int,
    val isFavorite: Boolean
)
