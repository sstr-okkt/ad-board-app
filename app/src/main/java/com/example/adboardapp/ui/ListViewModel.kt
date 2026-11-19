package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.data.AdRepositoryImpl
import com.example.adboardapp.domain.GetAdsUseCase
import com.example.adboardapp.model.Result
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

    // Called from the retry button after a network error
    fun reload() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadAds()
    }

    private fun loadAds() {
        viewModelScope.launch {
            when (val result = getAds()) {
                is Result.Success -> _uiState.update {
                    it.copy(ads = result.data, isLoading = false, errorMessage = null)
                }

                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }
}
