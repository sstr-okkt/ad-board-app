package com.example.adboardapp.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.data.AdRepositoryImpl
import com.example.adboardapp.domain.GetAdByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
        viewModelScope.launch {
            val ad = getAdById(adId)
            _uiState.update { DetailUiState(ad = ad, isLoading = false) }
        }
    }
}
