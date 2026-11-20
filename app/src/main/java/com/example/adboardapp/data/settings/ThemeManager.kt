package com.example.adboardapp.data.settings

import android.content.Context
import com.example.adboardapp.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Stores the theme choice between launches
class ThemeManager(context: Context) {

    private val preferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _mode = MutableStateFlow(
        ThemeMode.fromStorage(preferences.getString(KEY_THEME, null))
    )
    val mode: StateFlow<ThemeMode> = _mode.asStateFlow()

    fun setMode(mode: ThemeMode) {
        preferences.edit().putString(KEY_THEME, mode.storageValue).apply()
        _mode.value = mode
    }

    private companion object {
        const val KEY_THEME = "theme_mode"
    }
}
