package com.example.adboardapp.ui

import com.example.adboardapp.MainDispatcherRule
import com.example.adboardapp.domain.AdRepository
import com.example.adboardapp.domain.DeleteAdUseCase
import com.example.adboardapp.domain.IncrementViewsUseCase
import com.example.adboardapp.domain.ObserveAdUseCase
import com.example.adboardapp.domain.ToggleFavoriteUseCase
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.AdSource
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author
import com.example.adboardapp.model.ErrorReason
import com.example.adboardapp.model.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAdRepository()

    private fun viewModel() = DetailViewModel(
        adId = AD_ID,
        observeAd = ObserveAdUseCase(repository),
        incrementViews = IncrementViewsUseCase(repository),
        toggleFavorite = ToggleFavoriteUseCase(repository),
        deleteAd = DeleteAdUseCase(repository)
    )

    @Test
    fun countsOneViewPerVisit() = runTest {
        repository.ad.value = ad()

        val viewModel = viewModel()
        viewModel.reload()

        assertEquals(1, repository.increments)
    }

    @Test
    fun doesNotCountViewsOfOwnAds() = runTest {
        repository.ad.value = ad(source = AdSource.LOCAL)

        viewModel()

        assertEquals(0, repository.increments)
    }

    @Test
    fun missingAdShowsNotFound() = runTest {
        repository.ad.value = null

        val viewModel = viewModel()

        assertEquals(ErrorReason.NOT_FOUND, viewModel.uiState.value.errorReason)
    }

    private fun ad(
        id: Int = AD_ID,
        source: AdSource = AdSource.REMOTE
    ) = Ad(
        id = id,
        title = "Ad $id",
        description = "Description",
        price = 100.0,
        category = "smartphones",
        city = "kyiv",
        condition = AdCondition.USED,
        views = 0,
        status = AdStatus.PUBLISHED,
        author = Author(id, "Seller", 4.0),
        publishedAt = "2026-11-01",
        source = source
    )

    private class FakeAdRepository : AdRepository {
        val ad = MutableStateFlow<Ad?>(null)
        var increments = 0

        override fun observeAds(): Flow<List<Ad>> =
            MutableStateFlow(ad.value?.let { listOf(it) } ?: emptyList())

        override suspend fun refresh(): Result<Unit> = Result.Success(Unit)

        override suspend fun getAdById(id: Int): Result<Ad> =
            ad.value?.let { Result.Success(it) } ?: Result.Error(ErrorReason.NOT_FOUND)

        override fun observeAd(id: Int): Flow<Ad?> = ad

        override suspend fun toggleFavorite(id: Int) = Unit

        override suspend fun incrementViews(id: Int) {
            increments++
        }

        override suspend fun saveAd(ad: Ad, publish: Boolean): Result<Int> =
            Result.Success(ad.id)

        override suspend fun deleteAd(id: Int): Result<Unit> = Result.Success(Unit)

        override fun observeLocalAds(): Flow<List<Ad>> = MutableStateFlow(emptyList())

        override fun observeFavorites(): Flow<List<Ad>> = MutableStateFlow(emptyList())

        override suspend fun updateStatus(id: Int, status: AdStatus): Result<Unit> =
            Result.Success(Unit)
    }

    private companion object {
        const val AD_ID = 7
    }
}
