package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import com.example.adboardapp.data.settings.LanguageManager
import com.example.adboardapp.model.AppLanguage
import kotlinx.coroutines.flow.StateFlow

// Exposes the selected language and changes it
class LanguageViewModel(private val languageManager: LanguageManager) : ViewModel() {

    val language: StateFlow<AppLanguage> = languageManager.language

    fun setLanguage(language: AppLanguage) {
        languageManager.setLanguage(language)
    }
}
