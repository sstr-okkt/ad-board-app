package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.domain.DeleteAdUseCase
import com.example.adboardapp.domain.ObserveLocalAdsUseCase
import com.example.adboardapp.domain.ToggleFavoriteUseCase
import com.example.adboardapp.domain.UpdateAdStatusUseCase
import com.example.adboardapp.model.AdStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Handles the list of ads created on this device
class MyAdsViewModel(
    observeLocalAds: ObserveLocalAdsUseCase,
    private val updateStatus: UpdateAdStatusUseCase,
    private val deleteAd: DeleteAdUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyAdsUiState())
    val uiState: StateFlow<MyAdsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeLocalAds().collect { ads ->
                _uiState.update { it.copy(ads = ads, isLoading = false) }
            }
        }
    }

    fun onTabChange(tab: MyAdsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onFavoriteClick(id: Int) {
        viewModelScope.launch { toggleFavorite(id) }
    }

    fun publish(id: Int) {
        viewModelScope.launch { updateStatus(id, AdStatus.PUBLISHED) }
    }

    fun archive(id: Int) {
        viewModelScope.launch { updateStatus(id, AdStatus.ARCHIVED) }
    }

    fun delete(id: Int) {
        viewModelScope.launch { deleteAd(id) }
    }
}
