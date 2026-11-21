package com.example.adboardapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Url

// Public exchange rates of the National Bank of Ukraine
interface ExchangeRateApi {

    @GET
    suspend fun getUsdRate(@Url url: String): List<NbuRateDto>
}
