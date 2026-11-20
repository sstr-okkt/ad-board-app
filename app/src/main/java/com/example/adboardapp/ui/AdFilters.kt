package com.example.adboardapp.ui

import com.example.adboardapp.model.AdCondition

// Extra filters of the list, category and search are kept separately
data class AdFilters(
    val city: String? = null,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val condition: AdCondition? = null,
    val onlyWithPhoto: Boolean = false
) {
    // How many filters are set, used for the badge
    val activeCount: Int
        get() = listOfNotNull(
            city,
            minPrice,
            maxPrice,
            condition,
            if (onlyWithPhoto) true else null
        ).size
}
