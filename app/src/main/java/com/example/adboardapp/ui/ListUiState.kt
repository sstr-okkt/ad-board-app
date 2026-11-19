package com.example.adboardapp.ui

import com.example.adboardapp.model.Ad

// Single state object for the list screen
data class ListUiState(
    val ads: List<Ad> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false
) {
    // Filtered list is derived from ads and query, no need to keep it in state
    val visibleAds: List<Ad>
        get() = ads.filter { it.title.contains(query.trim(), ignoreCase = true) }
}
