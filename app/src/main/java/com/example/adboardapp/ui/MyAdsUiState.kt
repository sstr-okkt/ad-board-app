package com.example.adboardapp.ui

import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdStatus

// Tabs of the my ads screen
enum class MyAdsTab(val label: String, val status: AdStatus) {
    ACTIVE("Активні", AdStatus.PUBLISHED),
    DRAFTS("Чернетки", AdStatus.DRAFT),
    ARCHIVED("Архів", AdStatus.ARCHIVED)
}

// State of the my ads screen
data class MyAdsUiState(
    val ads: List<Ad> = emptyList(),
    val selectedTab: MyAdsTab = MyAdsTab.ACTIVE,
    val isLoading: Boolean = true
) {
    // Ads of the selected tab, newest first
    val visibleAds: List<Ad>
        get() = ads
            .filter { it.status == selectedTab.status }
            .sortedByDescending { it.publishedAt ?: "" }

    fun count(tab: MyAdsTab): Int = ads.count { it.status == tab.status }
}
