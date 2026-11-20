package com.example.adboardapp.model

// Languages supported by the app
enum class AppLanguage(val tag: String) {
    UKRAINIAN("uk"),
    ENGLISH("en");

    companion object {
        fun fromTag(value: String?): AppLanguage =
            entries.firstOrNull { it.tag == value } ?: UKRAINIAN
    }
}
