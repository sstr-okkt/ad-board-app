package com.example.adboardapp.ui

import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.adCategories
import com.example.adboardapp.model.adCities

// How many photos one ad can have
const val MAX_PHOTOS = 5

// Validation errors of the form, resolved to strings on the screen
enum class FieldError {
    TITLE_SHORT,
    TITLE_LONG,
    DESCRIPTION_SHORT,
    DESCRIPTION_LONG,
    PRICE_REQUIRED,
    PRICE_POSITIVE,
    PRICE_TOO_HIGH,
    PHONE_INVALID,
    PHOTO_REQUIRED
}

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
    val titleError: FieldError? = null,
    val descriptionError: FieldError? = null,
    val priceError: FieldError? = null,
    val phoneError: FieldError? = null,
    val imageError: FieldError? = null,
    val saveError: String? = null,
    val isSaving: Boolean = false,
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
