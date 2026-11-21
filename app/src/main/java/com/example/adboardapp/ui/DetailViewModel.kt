package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.domain.DeleteAdUseCase
import com.example.adboardapp.domain.IncrementViewsUseCase
import com.example.adboardapp.domain.ObserveAdUseCase
import com.example.adboardapp.domain.ToggleFavoriteUseCase
import com.example.adboardapp.model.ErrorReason
import com.example.adboardapp.model.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Loads one ad by the id passed in the navigation route
class DetailViewModel(
    private val adId: Int,
    private val observeAd: ObserveAdUseCase,
    private val incrementViews: IncrementViewsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val deleteAd: DeleteAdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        load()
    }

    // Called from the heart in the top bar
    fun onFavoriteClick() {
        val ad = _uiState.value.ad ?: return
        viewModelScope.launch { toggleFavorite(ad.id) }
    }

    // Called from the delete confirmation
    fun delete() {
        val ad = _uiState.value.ad ?: return
        if (!ad.isLocal) return

        viewModelScope.launch {
            when (val result = deleteAd(ad.id)) {
                is Result.Success -> _uiState.update { it.copy(isDeleted = true) }
                is Result.Error -> _uiState.update { it.copy(errorReason = result.reason) }
            }
        }
    }

    // Called from the retry button
    fun reload() {
        observeJob?.cancel()
        _uiState.value = DetailUiState()
        load()
    }

    private fun load() {
        viewModelScope.launch { incrementViews(adId) }

        observeJob = viewModelScope.launch {
            observeAd(adId).collect { ad ->
                // Deletion is handled by the delete callback, not by the empty flow
                if (_uiState.value.isDeleted) return@collect

                _uiState.value = if (ad == null) {
                    DetailUiState(
                        isLoading = false,
                        errorReason = ErrorReason.NOT_FOUND
                    )
                } else {
                    DetailUiState(ad = ad, isLoading = false)
                }
            }
        }
    }
}
