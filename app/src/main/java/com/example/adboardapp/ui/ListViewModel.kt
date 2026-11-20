package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.domain.ObserveAdsUseCase
import com.example.adboardapp.domain.RefreshAdsUseCase
import com.example.adboardapp.domain.ToggleFavoriteUseCase
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Holds the screen state and handles user actions
class ListViewModel(
    private val observeAds: ObserveAdsUseCase,
    private val refreshAds: RefreshAdsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init {
        // Cached data is shown immediately, even without network
        viewModelScope.launch {
            observeAds().collect { ads ->
                // Drafts are not shown on the public board
                _uiState.update {
                    it.copy(
                        ads = ads.filter { ad -> ad.status == AdStatus.PUBLISHED },
                        isLoading = false
                    )
                }
            }
        }
        refresh()
    }

    // Called from the search field
    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    // Called from the category chips
    fun onCategoryChange(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    // Called from the sort menu
    fun onSortChange(sort: AdSort) {
        _uiState.update { it.copy(sort = sort) }
    }

    // Called from the filters sheet
    fun onFiltersChange(filters: AdFilters) {
        _uiState.update { it.copy(filters = filters) }
    }

    // Called from the heart on the card
    fun onFavoriteClick(id: Int) {
        viewModelScope.launch { toggleFavorite(id) }
    }

    // Called on start and from the retry button
    fun refresh() {
        viewModelScope.launch {
            when (refreshAds()) {
                is Result.Success -> _uiState.update { it.copy(syncError = false) }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, syncError = true)
                }
            }
        }
    }
}
