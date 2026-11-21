package com.example.adboardapp.ui

import com.example.adboardapp.MainDispatcherRule
import com.example.adboardapp.domain.AdRepository
import com.example.adboardapp.domain.ObserveAdsUseCase
import com.example.adboardapp.domain.RefreshAdsUseCase
import com.example.adboardapp.domain.ToggleFavoriteUseCase
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author
import com.example.adboardapp.model.ErrorReason
import com.example.adboardapp.model.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAdRepository()

    private fun viewModel() = ListViewModel(
        observeAds = ObserveAdsUseCase(repository),
        refreshAds = RefreshAdsUseCase(repository),
        toggleFavorite = ToggleFavoriteUseCase(repository)
    )

    @Test
    fun showsPublishedAdsAndHidesDrafts() = runTest {
        repository.ads.value = listOf(ad(1), ad(2, status = AdStatus.DRAFT))

        val state = viewModel().uiState.value

        assertEquals(listOf(1), state.ads.map { it.id })
        assertFalse(state.isLoading)
    }

    @Test
    fun queryFiltersVisibleAds() = runTest {
        repository.ads.value = listOf(ad(1, title = "iPhone 13"), ad(2, title = "Крісло"))
        val viewModel = viewModel()

        viewModel.onQueryChange("iphone")

        assertEquals(listOf(1), viewModel.uiState.value.visibleAds.map { it.id })
    }

    @Test
    fun refreshErrorShowsSyncBanner() = runTest {
        repository.refreshResult = Result.Error(ErrorReason.SYNC, "no network")

        val state = viewModel().uiState.value

        assertTrue(state.syncError)
    }

    @Test
    fun favoriteClickCallsUseCase() = runTest {
        repository.ads.value = listOf(ad(7))
        val viewModel = viewModel()

        viewModel.onFavoriteClick(7)

        assertEquals(7, repository.toggledId)
    }

    @Test
    fun refreshIsCalledOnStart() = runTest {
        viewModel()

        assertEquals(1, repository.refreshCalls)
    }

    private fun ad(
        id: Int,
        title: String = "Ad $id",
        status: AdStatus = AdStatus.PUBLISHED
    ) = Ad(
        id = id,
        title = title,
        description = "Description",
        price = 100.0,
        category = "Смартфони",
        city = "Київ",
        condition = AdCondition.USED,
        views = 0,
        status = status,
        author = Author(id, "Seller", 4.0),
        publishedAt = "2026-11-01"
    )

    private class FakeAdRepository : AdRepository {
        val ads = MutableStateFlow<List<Ad>>(emptyList())
        var refreshResult: Result<Unit> = Result.Success(Unit)
        var refreshCalls = 0
        var toggledId: Int? = null

        override fun observeAds(): Flow<List<Ad>> = ads

        override suspend fun refresh(): Result<Unit> {
            refreshCalls++
            return refreshResult
        }

        override suspend fun getAdById(id: Int): Result<Ad> =
            Result.Error(ErrorReason.NOT_FOUND)

        override fun observeAd(id: Int): Flow<Ad?> =
            ads.map { list -> list.find { it.id == id } }

        override suspend fun toggleFavorite(id: Int) {
            toggledId = id
        }

        override suspend fun incrementViews(id: Int) = Unit

        override suspend fun saveAd(ad: Ad, publish: Boolean): Result<Int> =
            Result.Error(ErrorReason.NOT_FOUND)

        override suspend fun deleteAd(id: Int): Result<Unit> =
            Result.Error(ErrorReason.NOT_FOUND)

        override fun observeLocalAds(): Flow<List<Ad>> = ads

        override fun observeFavorites(): Flow<List<Ad>> = ads

        override suspend fun updateStatus(id: Int, status: AdStatus): Result<Unit> =
            Result.Error(ErrorReason.NOT_FOUND)
    }
}
