package com.example.adboardapp.ui

import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ListUiStateTest {

    private fun ad(
        id: Int,
        title: String,
        price: Double,
        city: String,
        category: String,
        condition: AdCondition,
        images: List<String>,
        publishedAt: String
    ) = Ad(
        id = id,
        title = title,
        description = "",
        price = price,
        isNegotiable = false,
        category = category,
        city = city,
        condition = condition,
        imageUrls = images,
        views = 0,
        status = AdStatus.PUBLISHED,
        author = Author(id, "Seller", 4.0),
        publishedAt = publishedAt
    )

    private val ads = listOf(
        ad(1, "iPhone 13", 15000.0, "odesa", "smartphones", AdCondition.USED, listOf("a"), "2026-11-15"),
        ad(2, "Samsung Galaxy", 8000.0, "kyiv", "smartphones", AdCondition.NEW, emptyList(), "2026-11-20"),
        ad(3, "Крісло офісне", 2300.0, "odesa", "furniture", AdCondition.USED, listOf("b"), "2026-11-10"),
        ad(4, "Ноутбук Lenovo", 25000.0, "lviv", "laptops", AdCondition.NEW, listOf("c"), "2026-11-18")
    )

    private fun state(
        query: String = "",
        category: String? = null,
        filters: AdFilters = AdFilters(),
        sort: AdSort = AdSort.NEWEST
    ) = ListUiState(
        ads = ads,
        query = query,
        selectedCategory = category,
        filters = filters,
        sort = sort,
        isLoading = false
    )

    @Test
    fun searchFindsByTitle() {
        assertEquals(listOf(1), state(query = "iphone").visibleAds.map { it.id })
    }

    @Test
    fun searchFindsByCityAndCategory() {
        assertEquals(listOf(2), state(query = "kyiv").visibleAds.map { it.id })
        assertEquals(listOf(2), state(query = "Київ").visibleAds.map { it.id })
        assertEquals(listOf(2, 1), state(query = "смартфони").visibleAds.map { it.id })
        assertEquals(listOf(2, 1), state(query = "smartphones").visibleAds.map { it.id })
    }

    @Test
    fun categoryFilterWorks() {
        assertEquals(listOf(2, 1), state(category = "smartphones").visibleAds.map { it.id })
    }

    @Test
    fun cityAndPriceFiltersWork() {
        val filters = AdFilters(city = "odesa", minPrice = 10000, maxPrice = 20000)
        assertEquals(listOf(1), state(filters = filters).visibleAds.map { it.id })
    }

    @Test
    fun conditionFilterWorks() {
        val filters = AdFilters(condition = AdCondition.NEW)
        assertEquals(listOf(2, 4), state(filters = filters).visibleAds.map { it.id })
    }

    @Test
    fun onlyWithPhotoFilterWorks() {
        val filters = AdFilters(onlyWithPhoto = true)
        assertEquals(listOf(4, 1, 3), state(filters = filters).visibleAds.map { it.id })
    }

    @Test
    fun sortingWorks() {
        assertEquals(3, state(sort = AdSort.CHEAPEST).visibleAds.first().id)
        assertEquals(4, state(sort = AdSort.EXPENSIVE).visibleAds.first().id)
        assertEquals(2, state(sort = AdSort.NEWEST).visibleAds.first().id)
    }

    @Test
    fun activeFilterCountIncludesCategoryAndExtraFilters() {
        val filters = AdFilters(city = "odesa", minPrice = 2000, onlyWithPhoto = true)
        val current = state(category = "furniture", filters = filters)

        assertEquals(4, current.activeFilterCount)
        assertTrue(current.hasActiveFilters)
        assertFalse(state().hasActiveFilters)
    }
}
