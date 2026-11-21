package com.example.adboardapp.data.rate

// USD to UAH rate and the date the NBU published it
data class CurrencyRate(
    val usdToUah: Double,
    val date: String? = null
)
