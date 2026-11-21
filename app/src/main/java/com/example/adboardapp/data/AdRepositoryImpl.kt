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
import com.example.adboardapp.model.ErrorReason
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
        Result.Error(ErrorReason.SYNC, e.message)
    }

    override suspend fun getAdById(id: Int): Result<Ad> =
        dao.getById(id)?.toDomain()
            ?.let { Result.Success(it) }
            ?: Result.Error(ErrorReason.NOT_FOUND)

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
        Result.Error(ErrorReason.SAVE, e.message)
    }

    override suspend fun deleteAd(id: Int): Result<Unit> = try {
        dao.deleteById(id)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(ErrorReason.DELETE, e.message)
    }

    override fun observeLocalAds(): Flow<List<Ad>> =
        dao.observeBySource(AdSource.LOCAL.name).map { rows ->
            rows.map { it.toDomain() }
        }

    override fun observeFavorites(): Flow<List<Ad>> =
        dao.observeFavorites().map { rows -> rows.map { it.toDomain() } }

    override suspend fun updateStatus(id: Int, status: AdStatus): Result<Unit> = try {
        val row = dao.getById(id)?.ad
            ?: return Result.Error(ErrorReason.NOT_FOUND)

        val publishedAt = if (status == AdStatus.PUBLISHED) {
            row.publishedAt ?: LocalDate.now().toString()
        } else {
            row.publishedAt
        }

        dao.update(row.copy(status = status.name, publishedAt = publishedAt))
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(ErrorReason.STATUS, e.message)
    }
}
