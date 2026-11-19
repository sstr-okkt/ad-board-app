package com.example.adboardapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.AdBoardApp
import com.example.adboardapp.domain.ObserveAdsUseCase
import com.example.adboardapp.domain.RefreshAdsUseCase
import com.example.adboardapp.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Holds the screen state and handles user actions
class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AdBoardApp).repository
    private val observeAds = ObserveAdsUseCase(repository)
    private val refreshAds = RefreshAdsUseCase(repository)

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init {
        // Cached data is shown immediately, even without network
        viewModelScope.launch {
            observeAds().collect { ads ->
                _uiState.update { it.copy(ads = ads, isLoading = false) }
            }
        }
        refresh()
    }

    // Called from the search field
    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
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
