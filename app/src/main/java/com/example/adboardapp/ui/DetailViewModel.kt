package com.example.adboardapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.AdBoardApp
import com.example.adboardapp.domain.GetAdByIdUseCase
import com.example.adboardapp.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Loads one ad by the id passed in the navigation route
class DetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val getAdById = GetAdByIdUseCase((application as AdBoardApp).repository)

    private val adId: Int = checkNotNull(savedStateHandle["itemId"])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadAd()
    }

    // Called from the retry button
    fun reload() {
        _uiState.value = DetailUiState()
        loadAd()
    }

    private fun loadAd() {
        viewModelScope.launch {
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
