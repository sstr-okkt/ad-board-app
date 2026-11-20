package com.example.adboardapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.example.adboardapp.model.categoryName
import com.example.adboardapp.model.cityName

// Current app language tag, used for the catalog labels
@Composable
internal fun currentLanguage(): String {
    val language = LocalConfiguration.current.locales[0].language
    return if (language == "en") "en" else "uk"
}

@Composable
internal fun categoryLabel(key: String): String = categoryName(key, currentLanguage())

@Composable
internal fun cityLabel(key: String): String = cityName(key, currentLanguage())
