package com.example.adboardapp.model

// Theme choice of the app
enum class ThemeMode(val label: String, val storageValue: String) {
    SYSTEM("Системна", "system"),
    LIGHT("Світла", "light"),
    DARK("Темна", "dark");

    companion object {
        fun fromStorage(value: String?): ThemeMode =
            entries.firstOrNull { it.storageValue == value } ?: SYSTEM
    }
}
