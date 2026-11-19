package com.example.adboardapp.ui

import com.example.adboardapp.model.Ad

// State for the details screen
data class DetailUiState(
    val ad: Ad? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
