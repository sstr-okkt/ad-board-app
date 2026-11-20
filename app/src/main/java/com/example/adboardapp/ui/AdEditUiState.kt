package com.example.adboardapp.ui

import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.adCategories
import com.example.adboardapp.model.adCities

// How many photos one ad can have
const val MAX_PHOTOS = 5

// State of the create / edit form
data class AdEditUiState(
    val isNew: Boolean = true,
    val isLoading: Boolean = false,
    val loadError: String? = null,
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val isNegotiable: Boolean = false,
    val category: String = adCategories.first(),
    val city: String = adCities.first(),
    val condition: AdCondition = AdCondition.USED,
    val phone: String = "",
    val imageUrls: List<String> = emptyList(),
    val titleError: String? = null,
    val descriptionError: String? = null,
    val priceError: String? = null,
    val phoneError: String? = null,
    val imageError: String? = null,
    val saveError: String? = null,
    val isSaved: Boolean = false
) {
    val canAddPhoto: Boolean get() = imageUrls.size < MAX_PHOTOS

    val hasErrors: Boolean
        get() = titleError != null ||
            descriptionError != null ||
            priceError != null ||
            phoneError != null ||
            imageError != null
}
