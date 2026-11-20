package com.example.adboardapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

// REST endpoints of the test store API
interface ProductsApi {

    @GET("products?limit=30")
    suspend fun getProducts(): ProductsResponse

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): ProductDto
}
