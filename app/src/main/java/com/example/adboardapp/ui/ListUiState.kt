package com.example.adboardapp.ui

import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.categorySearchNames
import com.example.adboardapp.model.citySearchNames

// Ads are shown in pages of ten
const val PAGE_SIZE = 10

// Sort order of the ad list
enum class AdSort {
    NEWEST,
    CHEAPEST,
    EXPENSIVE
}

// Single state object for the list screen
data class ListUiState(
    val ads: List<Ad> = emptyList(),
    val query: String = "",
    val selectedCategory: String? = null,
    val sort: AdSort = AdSort.NEWEST,
    val filters: AdFilters = AdFilters(),
    val page: Int = 0,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val syncError: Boolean = false
) {
    // Categories available on the board
    val categories: List<String>
        get() = ads.map { it.category }.distinct().sorted()

    // Cities available on the board
    val cities: List<String>
        get() = ads.map { it.city }.distinct().sorted()

    // Category, search and extra filters together
    val activeFilterCount: Int
        get() = filters.activeCount + if (selectedCategory != null) 1 else 0

    val hasActiveFilters: Boolean
        get() = activeFilterCount > 0

    // Filtered and sorted list, derived from ads, query, category and filters
    val visibleAds: List<Ad>
        get() = ads
            .filter { selectedCategory == null || it.category == selectedCategory }
            .filter { matchesQuery(it) }
            .filter { matchesFilters(it) }
            .let { list ->
                when (sort) {
                    AdSort.NEWEST -> list.sortedByDescending { it.publishedAt ?: "" }
                    AdSort.CHEAPEST -> list.sortedBy { it.price }
                    AdSort.EXPENSIVE -> list.sortedByDescending { it.price }
                }
            }

    // Number of pages in the filtered list, at least one
    val pageCount: Int
        get() = maxOf(1, (visibleAds.size + PAGE_SIZE - 1) / PAGE_SIZE)

    // Page kept inside the list, the filters can shrink it
    val currentPage: Int
        get() = page.coerceIn(0, pageCount - 1)

    // Ads of the current page
    val pageAds: List<Ad>
        get() = visibleAds.drop(currentPage * PAGE_SIZE).take(PAGE_SIZE)

    // Search looks through the fields a user sees on the card
    private fun matchesQuery(ad: Ad): Boolean {
        val text = query.trim()
        if (text.isEmpty()) return true
        return ad.title.contains(text, ignoreCase = true) ||
            ad.description.contains(text, ignoreCase = true) ||
            categorySearchNames(ad.category).any { it.contains(text, ignoreCase = true) } ||
            citySearchNames(ad.city).any { it.contains(text, ignoreCase = true) } ||
            ad.category.contains(text, ignoreCase = true) ||
            ad.city.contains(text, ignoreCase = true)
    }

    private fun matchesFilters(ad: Ad): Boolean {
        if (filters.city != null && ad.city != filters.city) return false

        filters.minPrice?.let { min ->
            if (ad.price < min) return false
        }
        filters.maxPrice?.let { max ->
            if (ad.price > max) return false
        }

        if (filters.condition != null && ad.condition != filters.condition) return false
        if (filters.onlyWithPhoto && ad.imageUrls.isEmpty()) return false

        return true
    }
}
