package com.example.adboardapp.data

import com.example.adboardapp.data.mapper.toAd
import com.example.adboardapp.data.remote.NetworkModule
import com.example.adboardapp.data.remote.ProductsApi
import com.example.adboardapp.domain.AdRepository
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.Result

// Remote implementation, data comes from a REST API
class AdRepositoryImpl(
    private val api: ProductsApi = NetworkModule.productsApi
) : AdRepository {

    override suspend fun getAds(): Result<List<Ad>> = try {
        Result.Success(api.getProducts().products.map { it.toAd() })
    } catch (e: Exception) {
        Result.Error("Не вдалося завантажити оголошення: ${e.message ?: "невідома помилка"}")
    }

    override suspend fun getAdById(id: Int): Result<Ad> = try {
        Result.Success(api.getProduct(id).toAd())
    } catch (e: Exception) {
        Result.Error("Не вдалося завантажити оголошення: ${e.message ?: "невідома помилка"}")
    }
}
