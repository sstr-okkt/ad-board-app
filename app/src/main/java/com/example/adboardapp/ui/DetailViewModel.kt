package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.domain.GetAdByIdUseCase
import com.example.adboardapp.domain.IncrementViewsUseCase
import com.example.adboardapp.domain.ToggleFavoriteUseCase
import com.example.adboardapp.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Loads one ad by the id passed in the navigation route
class DetailViewModel(
    private val adId: Int,
    private val getAdById: GetAdByIdUseCase,
    private val incrementViews: IncrementViewsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadAd()
    }

    // Called from the heart in the top bar
    fun onFavoriteClick() {
        val ad = _uiState.value.ad ?: return
        viewModelScope.launch {
            toggleFavorite(ad.id)
            _uiState.update { state ->
                state.copy(ad = state.ad?.copy(isFavorite = !state.ad.isFavorite))
            }
        }
    }

    // Called from the retry button
    fun reload() {
        _uiState.value = DetailUiState()
        loadAd()
    }

    private fun loadAd() {
        viewModelScope.launch {
            incrementViews(adId)
            _uiState.value = when (val result = getAdById(adId)) {
                is Result.Success -> DetailUiState(ad = result.data, isLoading = false)
                is Result.Error -> DetailUiState(
                    isLoading = false,
                    errorMessage = result.message
                )
            }
        }
    }
}
