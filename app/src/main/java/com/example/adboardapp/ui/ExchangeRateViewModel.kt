package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import com.example.adboardapp.data.rate.CurrencyRate
import com.example.adboardapp.data.rate.CurrencyRateStore
import kotlinx.coroutines.flow.StateFlow

// Exposes the exchange rate loaded by the repository
class ExchangeRateViewModel(rateStore: CurrencyRateStore) : ViewModel() {
    val rate: StateFlow<CurrencyRate?> = rateStore.rate
}
