package com.example.adboardapp.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.data.AdRepositoryImpl
import com.example.adboardapp.domain.GetAdByIdUseCase
import com.example.adboardapp.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Loads one ad by the id passed in the navigation route
class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getAdById: GetAdByIdUseCase
) : ViewModel() {

    // Used by the default ViewModel factory
    constructor(savedStateHandle: SavedStateHandle) : this(
        savedStateHandle,
        GetAdByIdUseCase(AdRepositoryImpl())
    )

    private val adId: Int = checkNotNull(savedStateHandle["itemId"])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadAd()
    }

    // Called from the retry button after a network error
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
