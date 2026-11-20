package com.example.adboardapp.data

import com.example.adboardapp.data.local.AdDao
import com.example.adboardapp.data.local.AdImageEntity
import com.example.adboardapp.data.mapper.toDomain
import com.example.adboardapp.data.mapper.toEntity
import com.example.adboardapp.data.mapper.toImageEntities
import com.example.adboardapp.data.remote.ProductsApi
import com.example.adboardapp.domain.AdRepository
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdSource
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

// Offline-first repository: Room is the source of truth, the network only refreshes it
class AdRepositoryImpl(
    private val api: ProductsApi,
    private val dao: AdDao
) : AdRepository {

    override fun observeAds(): Flow<List<Ad>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override fun observeAd(id: Int): Flow<Ad?> =
        dao.observeById(id).map { row -> row?.toDomain() }

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

    override suspend fun incrementViews(id: Int) = dao.incrementViews(id)

    override suspend fun saveAd(ad: Ad, publish: Boolean): Result<Int> = try {
        val id = if (ad.id == 0) dao.nextLocalId() else ad.id
        val entity = ad.copy(
            id = id,
            status = if (publish) AdStatus.PUBLISHED else AdStatus.DRAFT,
            publishedAt = if (publish) ad.publishedAt ?: LocalDate.now().toString() else null,
            source = AdSource.LOCAL
        ).toEntity()

        if (ad.id == 0) dao.insert(entity) else dao.update(entity)

        dao.replaceAdImages(
            adId = id,
            images = ad.imageUrls.mapIndexed { index, url ->
                AdImageEntity(adId = id, url = url, position = index)
            }
        )
        Result.Success(id)
    } catch (e: Exception) {
        Result.Error("Не вдалося зберегти оголошення: ${e.message ?: "невідома помилка"}")
    }

    override suspend fun deleteAd(id: Int): Result<Unit> = try {
        dao.deleteById(id)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error("Не вдалося видалити оголошення: ${e.message ?: "невідома помилка"}")
    }
}
