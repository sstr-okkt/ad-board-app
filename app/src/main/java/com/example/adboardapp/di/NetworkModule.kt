package com.example.adboardapp.di

import com.example.adboardapp.data.remote.ProductsApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

// Provides the network client
val networkModule = module {
    single<ProductsApi> { createProductsApi() }
}

private fun createProductsApi(): ProductsApi {
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
        .create(ProductsApi::class.java)
}
