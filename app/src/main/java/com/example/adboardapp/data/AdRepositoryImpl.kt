package com.example.adboardapp.data

import com.example.adboardapp.data.local.AdDao
import com.example.adboardapp.data.mapper.toDomain
import com.example.adboardapp.data.mapper.toEntity
import com.example.adboardapp.data.mapper.toImageEntities
import com.example.adboardapp.data.remote.ProductsApi
import com.example.adboardapp.domain.AdRepository
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Offline-first repository: Room is the source of truth, the network only refreshes it
class AdRepositoryImpl(
    private val api: ProductsApi,
    private val dao: AdDao
) : AdRepository {

    override fun observeAds(): Flow<List<Ad>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun refresh(): Result<Unit> = try {
        val products = api.getProducts().products
        // Keep the old cache if the response is empty
        if (products.isNotEmpty()) {
            dao.replaceRemote(
                ads = products.map { it.toEntity() },
                images = products.flatMap { it.toImageEntities() }
            )
        }
        Result.Success(Unit)
    } catch (e: Exception) {
        // User ads stay in the database, only the network part is refreshed
        Result.Error("Не вдалося синхронізувати оголошення: ${e.message ?: "невідома помилка"}")
    }

    override suspend fun getAdById(id: Int): Result<Ad> =
        dao.getById(id)?.toDomain()
            ?.let { Result.Success(it) }
            ?: Result.Error("Оголошення не знайдено в кеші")

    override suspend fun toggleFavorite(id: Int) = dao.toggleFavorite(id)
}
