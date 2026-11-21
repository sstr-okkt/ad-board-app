package com.example.adboardapp.data.remote

import kotlinx.serialization.Serializable

// One currency of the NBU exchange list
@Serializable
data class NbuRateDto(
    val rate: Double = 0.0,
    val exchangedate: String? = null,
    val cc: String? = null
)
