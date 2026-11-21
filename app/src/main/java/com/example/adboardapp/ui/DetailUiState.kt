package com.example.adboardapp.ui

import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.ErrorReason

// State for the details screen
data class DetailUiState(
    val ad: Ad? = null,
    val isLoading: Boolean = true,
    val errorReason: ErrorReason? = null,
    val isDeleted: Boolean = false
)
