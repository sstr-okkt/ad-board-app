package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.data.AdRepositoryImpl
import com.example.adboardapp.domain.GetAdsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Holds the screen state and handles user actions
class ListViewModel(
    private val getAds: GetAdsUseCase = GetAdsUseCase(AdRepositoryImpl())
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListUiState(isLoading = true))
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init {
        loadAds()
    }

    // Called from the search field
    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    private fun loadAds() {
        viewModelScope.launch {
            val ads = getAds()
            _uiState.update { it.copy(ads = ads, isLoading = false) }
        }
    }
}
