package com.example.adboardapp.ui

import com.example.adboardapp.model.Ad

// Sort order of the ad list
enum class AdSort(val label: String) {
    NEWEST("Новіші"),
    CHEAPEST("Дешевші"),
    EXPENSIVE("Дорожчі")
}

// Single state object for the list screen
data class ListUiState(
    val ads: List<Ad> = emptyList(),
    val query: String = "",
    val selectedCategory: String? = null,
    val sort: AdSort = AdSort.NEWEST,
    val isLoading: Boolean = true,
    val syncError: Boolean = false
) {
    // Categories available on the board
    val categories: List<String>
        get() = ads.map { it.category }.distinct().sorted()

    // Filtered and sorted list, derived from ads, query, category and sort
    val visibleAds: List<Ad>
        get() = ads
            .filter { selectedCategory == null || it.category == selectedCategory }
            .filter { matches(it) }
            .let { list ->
                when (sort) {
                    AdSort.NEWEST -> list.sortedByDescending { it.publishedAt ?: "" }
                    AdSort.CHEAPEST -> list.sortedBy { it.price }
                    AdSort.EXPENSIVE -> list.sortedByDescending { it.price }
                }
            }

    // Search looks through the fields a user sees on the card
    private fun matches(ad: Ad): Boolean {
        val text = query.trim()
        if (text.isEmpty()) return true
        return ad.title.contains(text, ignoreCase = true) ||
            ad.description.contains(text, ignoreCase = true) ||
            ad.city.contains(text, ignoreCase = true) ||
            ad.category.contains(text, ignoreCase = true)
    }
}
