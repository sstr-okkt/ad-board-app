package com.example.adboardapp.data.remote

import retrofit2.http.GET

// REST endpoints of the test store API
interface ProductsApi {

    @GET("products?limit=30")
    suspend fun getProducts(): ProductsResponse
}
