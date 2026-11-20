package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import com.example.adboardapp.data.settings.ThemeManager
import com.example.adboardapp.model.ThemeMode
import kotlinx.coroutines.flow.StateFlow

// Exposes the selected theme and changes it
class ThemeViewModel(private val themeManager: ThemeManager) : ViewModel() {

    val mode: StateFlow<ThemeMode> = themeManager.mode

    fun setMode(mode: ThemeMode) {
        themeManager.setMode(mode)
    }
}
