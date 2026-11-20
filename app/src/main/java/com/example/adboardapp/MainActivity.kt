package com.example.adboardapp

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adboardapp.data.settings.LanguageManager
import com.example.adboardapp.model.ThemeMode
import com.example.adboardapp.ui.AdBoardNavHost
import com.example.adboardapp.ui.LanguageViewModel
import com.example.adboardapp.ui.ThemeViewModel
import com.example.adboardapp.ui.theme.AdBoardAppTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {

    // Applies the saved language on Android 8-12
    override fun attachBaseContext(newBase: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val language = LanguageManager.readLanguage(newBase)
            val locale = Locale.forLanguageTag(language.tag)
            Locale.setDefault(locale)
            val configuration = Configuration(newBase.resources.configuration).apply {
                setLocale(locale)
            }
            super.attachBaseContext(newBase.createConfigurationContext(configuration))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = koinViewModel()
            val languageViewModel: LanguageViewModel = koinViewModel()
            val themeMode by themeViewModel.mode.collectAsStateWithLifecycle()
            val language by languageViewModel.language.collectAsStateWithLifecycle()

            // Android 13+ recreates the activity itself when the locale changes
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                val appliedLanguage = remember {
                    LanguageManager.readLanguage(this@MainActivity)
                }
                LaunchedEffect(language) {
                    if (language != appliedLanguage) recreate()
                }
            }

            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            AdBoardAppTheme(darkTheme = darkTheme) {
                AdBoardNavHost()
            }
        }
    }
}
