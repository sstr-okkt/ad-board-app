package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.domain.GetAdByIdUseCase
import com.example.adboardapp.domain.SaveAdUseCase
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.AdSource
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author
import com.example.adboardapp.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Handles the create and edit form
class AdEditViewModel(
    private val adId: Int,
    private val getAdById: GetAdByIdUseCase,
    private val saveAd: SaveAdUseCase
) : ViewModel() {

    // Original ad is kept to restore views, favorites and the publish date
    private var original: Ad? = null

    private val _uiState = MutableStateFlow(
        AdEditUiState(isNew = adId == 0, isLoading = adId != 0)
    )
    val uiState: StateFlow<AdEditUiState> = _uiState.asStateFlow()

    init {
        if (adId != 0) loadAd()
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, titleError = null) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value, descriptionError = null) }
    }

    fun onPriceChange(value: String) {
        // Only digits and one separator are allowed
        if (value.count { it.isDigit() } > 9) return
        if (value.any { !it.isDigit() && it != '.' && it != ',' }) return
        _uiState.update { it.copy(price = value, priceError = null) }
    }

    fun onNegotiableChange(value: Boolean) {
        _uiState.update { it.copy(isNegotiable = value) }
    }

    fun onCategoryChange(value: String) {
        _uiState.update { it.copy(category = value) }
    }

    fun onCityChange(value: String) {
        _uiState.update { it.copy(city = value) }
    }

    fun onConditionChange(value: AdCondition) {
        _uiState.update { it.copy(condition = value) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value, phoneError = null) }
    }

    fun onImageAdded(url: String) {
        _uiState.update { state ->
            if (state.imageUrls.size >= MAX_PHOTOS) {
                state
            } else {
                state.copy(imageUrls = state.imageUrls + url, imageError = null)
            }
        }
    }

    fun onImageRemoved(url: String) {
        _uiState.update { it.copy(imageUrls = it.imageUrls - url) }
    }

    // Saves the ad as a draft or publishes it
    fun save(publish: Boolean) {
        val validated = validate(_uiState.value, publish)
        _uiState.value = validated
        if (validated.hasErrors) return

        viewModelScope.launch {
            when (val result = saveAd(buildAd(validated), publish)) {
                is Result.Success -> _uiState.update { it.copy(isSaved = true) }
                is Result.Error -> _uiState.update { it.copy(saveError = result.message) }
            }
        }
    }

    private fun loadAd() {
        viewModelScope.launch {
            when (val result = getAdById(adId)) {
                is Result.Success -> {
                    val ad = result.data
                    original = ad
                    _uiState.value = AdEditUiState(
                        isNew = false,
                        isLoading = false,
                        title = ad.title,
                        description = ad.description,
                        price = priceText(ad.price),
                        isNegotiable = ad.isNegotiable,
                        category = ad.category,
                        city = ad.city,
                        condition = ad.condition,
                        phone = ad.author.contact.orEmpty(),
                        imageUrls = ad.imageUrls
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, loadError = result.message)
                }
            }
        }
    }

    private fun validate(state: AdEditUiState, publish: Boolean): AdEditUiState {
        val title = state.title.trim()
        val description = state.description.trim()
        val price = state.price.replace(',', '.').toDoubleOrNull()
        val digits = state.phone.count { it.isDigit() }

        return state.copy(
            titleError = when {
                title.length < 5 -> FieldError.TITLE_SHORT
                title.length > 60 -> FieldError.TITLE_LONG
                else -> null
            },
            descriptionError = when {
                description.length < 20 -> FieldError.DESCRIPTION_SHORT
                description.length > 2000 -> FieldError.DESCRIPTION_LONG
                else -> null
            },
            priceError = when {
                price == null -> FieldError.PRICE_REQUIRED
                price <= 0.0 -> FieldError.PRICE_POSITIVE
                price > 10_000_000 -> FieldError.PRICE_TOO_HIGH
                else -> null
            },
            phoneError = if (digits !in 10..13) {
                FieldError.PHONE_INVALID
            } else {
                null
            },
            imageError = if (publish && state.imageUrls.isEmpty()) {
                FieldError.PHOTO_REQUIRED
            } else {
                null
            },
            saveError = null
        )
    }

    private fun buildAd(state: AdEditUiState): Ad {
        val price = state.price.replace(',', '.').toDouble()
        return Ad(
            id = adId,
            title = state.title.trim(),
            description = state.description.trim(),
            price = price,
            isNegotiable = state.isNegotiable,
            category = state.category,
            city = state.city,
            condition = state.condition,
            imageUrls = state.imageUrls,
            views = original?.views ?: 0,
            status = original?.status ?: AdStatus.DRAFT,
            author = Author(
                id = adId,
                name = "Ви",
                rating = original?.author?.rating ?: 0.0,
                contact = state.phone.trim()
            ),
            publishedAt = original?.publishedAt,
            source = AdSource.LOCAL,
            isFavorite = original?.isFavorite ?: false
        )
    }

    // 15000.0 -> "15000", 15000.5 -> "15000.5"
    private fun priceText(price: Double): String =
        if (price % 1.0 == 0.0) price.toLong().toString() else price.toString()
}
