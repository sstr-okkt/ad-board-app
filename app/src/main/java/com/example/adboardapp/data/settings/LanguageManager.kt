package com.example.adboardapp.data.settings

import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.app.LocaleManager
import androidx.annotation.RequiresApi
import com.example.adboardapp.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

// Stores the app language and applies it to the Android resources
class LanguageManager(private val context: Context) {

    private val preferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(readLanguage(context))
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        preferences.edit().putString(KEY_LANGUAGE, language.tag).apply()
        _language.value = language

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            applyLocales(language)
        } else {
            Locale.setDefault(Locale.forLanguageTag(language.tag))
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun applyLocales(language: AppLanguage) {
        val manager = context.getSystemService(LocaleManager::class.java)
        manager.applicationLocales = LocaleList.forLanguageTags(language.tag)
    }

    companion object {
        private const val KEY_LANGUAGE = "app_language"

        // The saved language, or the system one on first launch
        fun readLanguage(context: Context): AppLanguage {
            val saved = context
                .getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getString(KEY_LANGUAGE, null)

            if (saved != null) return AppLanguage.fromTag(saved)

            val systemLanguage = Locale.getDefault().language
            return if (systemLanguage == "en") AppLanguage.ENGLISH else AppLanguage.UKRAINIAN
        }
    }
}
