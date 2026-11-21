package com.example.adboardapp.data.rate

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Stores the exchange rate in the shared settings file
class PreferencesCurrencyRateStore(context: Context) : CurrencyRateStore {

    private val preferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _rate = MutableStateFlow(read())
    override val rate: StateFlow<CurrencyRate?> = _rate.asStateFlow()

    override fun save(rate: CurrencyRate) {
        preferences.edit()
            .putString(KEY_RATE, rate.usdToUah.toString())
            .putString(KEY_DATE, rate.date)
            .apply()
        _rate.value = rate
    }

    private fun read(): CurrencyRate? {
        val value = preferences.getString(KEY_RATE, null)?.toDoubleOrNull() ?: return null
        return CurrencyRate(
            usdToUah = value,
            date = preferences.getString(KEY_DATE, null)
        )
    }

    private companion object {
        const val KEY_RATE = "usd_to_uah"
        const val KEY_DATE = "usd_to_uah_date"
    }
}
