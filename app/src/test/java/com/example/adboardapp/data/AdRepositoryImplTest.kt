package com.example.adboardapp.data

import com.example.adboardapp.data.local.AdDao
import com.example.adboardapp.data.local.AdEntity
import com.example.adboardapp.data.local.AdImageEntity
import com.example.adboardapp.data.local.AdStateRow
import com.example.adboardapp.data.local.AdWithImages
import com.example.adboardapp.data.rate.CurrencyRate
import com.example.adboardapp.data.rate.CurrencyRateStore
import com.example.adboardapp.data.remote.ExchangeRateApi
import com.example.adboardapp.data.remote.NbuRateDto
import com.example.adboardapp.data.remote.ProductDto
import com.example.adboardapp.data.remote.ProductsApi
import com.example.adboardapp.data.remote.ProductsResponse
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.AdSource
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author
import com.example.adboardapp.model.ErrorReason
import com.example.adboardapp.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdRepositoryImplTest {

    private val dao = FakeAdDao()
    private val api = FakeProductsApi()
    private val rateApi = FakeRateApi()
    private val rateStore = FakeRateStore()
    private val repository = AdRepositoryImpl(
        api = api,
        dao = dao,
        rateApi = rateApi,
        rateStore = rateStore
    )

    @Test
    fun refreshStoresRemoteAds() = runTest {
        api.products = listOf(product(1), product(2))

        val result = repository.refresh()

        assertTrue(result is Result.Success)
        assertEquals(2, dao.rows.size)
        assertEquals(AdSource.REMOTE.name, dao.rows.getValue(1).source)
        assertEquals(2, dao.images[1]?.size)
    }

    @Test
    fun refreshConvertsPricesWithTheNbuRate() = runTest {
        api.products = listOf(product(1))
        rateApi.rate = 41.5

        repository.refresh()

        assertEquals(415.0, dao.rows.getValue(1).price, 0.01)
        assertEquals(41.5, rateStore.rate.value?.usdToUah)
        assertEquals("21.11.2026", rateStore.rate.value?.date)
    }

    @Test
    fun refreshUsesTheStoredRateWhenTheRateApiFails() = runTest {
        api.products = listOf(product(1))
        rateStore.save(CurrencyRate(42.0, "01.01.2026"))
        rateApi.failure = Exception("offline")

        repository.refresh()

        assertEquals(420.0, dao.rows.getValue(1).price, 0.01)
    }

    @Test
    fun refreshUsesTheDefaultRateWithoutAnyRate() = runTest {
        api.products = listOf(product(1))
        rateApi.failure = Exception("offline")

        repository.refresh()

        assertEquals(410.0, dao.rows.getValue(1).price, 0.01)
    }

    @Test
    fun refreshKeepsCacheOnNetworkError() = runTest {
        dao.insert(entity(5))
        api.failure = IllegalStateException("no network")

        val result = repository.refresh()

        assertTrue(result is Result.Error)
        assertTrue(dao.rows.containsKey(5))
    }

    @Test
    fun emptyResponseDoesNotClearCache() = runTest {
        dao.insert(entity(5))
        api.products = emptyList()

        repository.refresh()

        assertTrue(dao.rows.containsKey(5))
    }

    @Test
    fun getAdByIdReturnsErrorWhenMissing() = runTest {
        val result = repository.getAdById(404)

        assertEquals(ErrorReason.NOT_FOUND, (result as Result.Error).reason)
    }

    @Test
    fun saveAdUsesNegativeIdAndLocalSource() = runTest {
        val ad = userAd(id = 0, status = AdStatus.DRAFT, publishedAt = null)

        val result = repository.saveAd(ad, publish = true)

        assertTrue(result is Result.Success)
        val id = (result as Result.Success).data
        assertEquals(-1, id)
        val saved = dao.rows.getValue(-1)
        assertEquals(AdSource.LOCAL.name, saved.source)
        assertEquals(AdStatus.PUBLISHED.name, saved.status)
        assertNotNull(saved.publishedAt)
    }

    @Test
    fun favoriteCanBeToggled() = runTest {
        dao.insert(entity(1))

        repository.toggleFavorite(1)

        assertTrue(dao.rows.getValue(1).isFavorite)
    }

    @Test
    fun updateStatusPublishesDraft() = runTest {
        dao.insert(entity(-1, status = AdStatus.DRAFT, publishedAt = null))

        repository.updateStatus(-1, AdStatus.PUBLISHED)

        val updated = dao.rows.getValue(-1)
        assertEquals(AdStatus.PUBLISHED.name, updated.status)
        assertNotNull(updated.publishedAt)
    }

    private fun product(id: Int) = ProductDto(
        id = id,
        title = "Product $id",
        price = 10.0,
        category = "smartphones",
        images = listOf("a", "b")
    )

    private fun entity(
        id: Int,
        status: AdStatus = AdStatus.PUBLISHED,
        publishedAt: String? = "2026-01-01"
    ) = AdEntity(
        id = id,
        title = "Ad $id",
        description = "Description",
        price = 100.0,
        isNegotiable = false,
        category = "Смартфони",
        city = "Київ",
        condition = AdCondition.USED.name,
        views = 0,
        status = status.name,
        authorName = "Seller",
        authorRating = 4.0,
        authorContact = "+380671112233",
        publishedAt = publishedAt,
        source = AdSource.LOCAL.name,
        isFavorite = false
    )

    private fun userAd(
        id: Int,
        status: AdStatus,
        publishedAt: String?
    ) = Ad(
        id = id,
        title = "My ad",
        description = "Description",
        price = 500.0,
        category = "furniture",
        city = "kyiv",
        condition = AdCondition.USED,
        imageUrls = listOf("photo"),
        views = 0,
        status = status,
        author = Author(id, "Ви", 0.0, "+380671112233"),
        publishedAt = publishedAt,
        source = AdSource.LOCAL
    )

    private class FakeRateApi : ExchangeRateApi {
        var rate: Double? = null
        var failure: Exception? = null

        override suspend fun getUsdRate(url: String): List<NbuRateDto> {
            failure?.let { throw it }
            val value = rate ?: return emptyList()
            return listOf(NbuRateDto(rate = value, exchangedate = "21.11.2026"))
        }
    }

    private class FakeRateStore : CurrencyRateStore {
        private val _rate = MutableStateFlow<CurrencyRate?>(null)
        override val rate: StateFlow<CurrencyRate?> = _rate

        override fun save(rate: CurrencyRate) {
            _rate.value = rate
        }
    }

    private class FakeProductsApi : ProductsApi {
        var products: List<ProductDto> = emptyList()
        var failure: Exception? = null

        override suspend fun getProducts(): ProductsResponse {
            failure?.let { throw it }
            return ProductsResponse(products)
        }
    }

    private class FakeAdDao : AdDao() {
        val rows = mutableMapOf<Int, AdEntity>()
        val images = mutableMapOf<Int, MutableList<AdImageEntity>>()

        override fun observeAll(): Flow<List<AdWithImages>> =
            MutableStateFlow(rows.values.map { row(it) })

        override fun observeBySource(source: String): Flow<List<AdWithImages>> =
            MutableStateFlow(rows.values.filter { it.source == source }.map { row(it) })

        override fun observeFavorites(): Flow<List<AdWithImages>> =
            MutableStateFlow(rows.values.filter { it.isFavorite }.map { row(it) })

        override suspend fun getById(id: Int): AdWithImages? =
            rows[id]?.let { row(it) }

        override fun observeById(id: Int): Flow<AdWithImages?> =
            MutableStateFlow(rows[id]?.let { row(it) })

        override suspend fun insertAll(ads: List<AdEntity>) {
            ads.forEach { rows[it.id] = it }
        }

        override suspend fun insert(ad: AdEntity) {
            rows[ad.id] = ad
        }

        override suspend fun insertImages(images: List<AdImageEntity>) {
            images.forEach { image ->
                this.images.getOrPut(image.adId) { mutableListOf() }.add(image)
            }
        }

        override suspend fun update(ad: AdEntity) {
            rows[ad.id] = ad
        }

        override suspend fun deleteById(id: Int) {
            rows.remove(id)
            images.remove(id)
        }

        override suspend fun deleteRemote() {
            rows.values.filter { it.source == AdSource.REMOTE.name }
                .forEach { deleteById(it.id) }
        }

        override suspend fun deleteImages(adId: Int) {
            images.remove(adId)
        }

        override suspend fun toggleFavorite(id: Int) {
            rows[id]?.let { rows[id] = it.copy(isFavorite = !it.isFavorite) }
        }

        override suspend fun incrementViews(id: Int) {
            rows[id]?.let { rows[id] = it.copy(views = it.views + 1) }
        }

        override suspend fun minLocalId(): Int? =
            rows.keys.filter { it < 0 }.minOrNull()

        override suspend fun remoteStates(): List<AdStateRow> =
            rows.values
                .filter { it.source == AdSource.REMOTE.name }
                .map { AdStateRow(it.id, it.views, it.isFavorite) }

        override suspend fun replaceRemote(
            ads: List<AdEntity>,
            images: List<AdImageEntity>
        ) {
            deleteRemote()
            insertAll(ads)
            insertImages(images)
        }

        private fun row(ad: AdEntity) = AdWithImages(
            ad = ad,
            images = images[ad.id].orEmpty()
        )
    }
}
