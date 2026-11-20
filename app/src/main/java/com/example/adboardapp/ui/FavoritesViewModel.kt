package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.domain.ObserveFavoritesUseCase
import com.example.adboardapp.domain.ToggleFavoriteUseCase
import com.example.adboardapp.model.Ad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// State of the favorites screen
data class FavoritesUiState(
    val ads: List<Ad> = emptyList(),
    val isLoading: Boolean = true
)

// Shows the ads marked with a heart
class FavoritesViewModel(
    observeFavorites: ObserveFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeFavorites().collect { ads ->
                _uiState.value = FavoritesUiState(ads = ads, isLoading = false)
            }
        }
    }

    fun onFavoriteClick(id: Int) {
        viewModelScope.launch { toggleFavorite(id) }
    }
}
