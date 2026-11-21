package com.example.adboardapp.di

import com.example.adboardapp.data.remote.ExchangeRateApi
import com.example.adboardapp.data.remote.ProductsApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

// Provides the network client and the remote APIs
val networkModule = module {
    single<Retrofit> { createRetrofit() }
    single<ProductsApi> { get<Retrofit>().create(ProductsApi::class.java) }
    single<ExchangeRateApi> { get<Retrofit>().create(ExchangeRateApi::class.java) }
}

private fun createRetrofit(): Retrofit {
    val json = Json { ignoreUnknownKeys = true }

    val client = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        )
        .build()

    return Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}
