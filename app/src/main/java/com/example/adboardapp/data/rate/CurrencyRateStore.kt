package com.example.adboardapp.data.rate

import kotlinx.coroutines.flow.StateFlow

// Keeps the last loaded exchange rate between launches
interface CurrencyRateStore {

    val rate: StateFlow<CurrencyRate?>

    fun save(rate: CurrencyRate)
}
