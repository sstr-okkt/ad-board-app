package com.example.adboardapp.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adboardapp.R
import com.example.adboardapp.data.rate.CurrencyRate
import com.example.adboardapp.model.AppLanguage
import com.example.adboardapp.model.ThemeMode
import com.example.adboardapp.ui.theme.AdBoardAppTheme
import java.text.NumberFormat
import org.koin.androidx.compose.koinViewModel

// App settings: language, theme and the exchange rate
@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel = koinViewModel(),
    languageViewModel: LanguageViewModel = koinViewModel(),
    rateViewModel: ExchangeRateViewModel = koinViewModel()
) {
    val themeMode by themeViewModel.mode.collectAsStateWithLifecycle()
    val language by languageViewModel.language.collectAsStateWithLifecycle()
    val rate by rateViewModel.rate.collectAsStateWithLifecycle()

    SettingsContent(
        themeMode = themeMode,
        onThemeChange = themeViewModel::setMode,
        language = language,
        onLanguageChange = languageViewModel::setLanguage,
        rate = rate
    )
}

// Stateless content of the settings screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    rate: CurrencyRate?,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.settings_title)) })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                SectionTitle(stringResource(R.string.language_section))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        AppLanguage.entries.forEachIndexed { index, option ->
                            RadioRow(
                                label = stringResource(languageRes(option)),
                                description = null,
                                selected = language == option,
                                onClick = { onLanguageChange(option) }
                            )
                            if (index != AppLanguage.entries.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 56.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                SectionTitle(stringResource(R.string.appearance_section))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ThemeMode.entries.forEachIndexed { index, option ->
                            RadioRow(
                                label = stringResource(themeRes(option)),
                                description = stringResource(themeDescriptionRes(option)),
                                selected = themeMode == option,
                                onClick = { onThemeChange(option) }
                            )
                            if (index != ThemeMode.entries.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 56.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                SectionTitle(stringResource(R.string.rate_section))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.rate_nbu),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(4.dp))
                        if (rate == null) {
                            Text(
                                text = stringResource(R.string.rate_empty),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = stringResource(
                                    R.string.rate_value,
                                    formatRate(rate.usdToUah)
                                ),
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (rate.date != null) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.rate_updated, rate.date),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 41.523 -> "41,52" in the current locale
@Composable
private fun formatRate(value: Double): String {
    val locale = LocalConfiguration.current.locales[0]
    return remember(value, locale) {
        NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }.format(value)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(8.dp))
}

// One row of a radio group
@Composable
private fun RadioRow(
    label: String,
    description: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@StringRes
private fun languageRes(language: AppLanguage): Int = when (language) {
    AppLanguage.UKRAINIAN -> R.string.language_ukrainian
    AppLanguage.ENGLISH -> R.string.language_english
}

@StringRes
internal fun themeRes(theme: ThemeMode): Int = when (theme) {
    ThemeMode.SYSTEM -> R.string.theme_system
    ThemeMode.LIGHT -> R.string.theme_light
    ThemeMode.DARK -> R.string.theme_dark
}

@StringRes
private fun themeDescriptionRes(theme: ThemeMode): Int = when (theme) {
    ThemeMode.SYSTEM -> R.string.theme_system_desc
    ThemeMode.LIGHT -> R.string.theme_light_desc
    ThemeMode.DARK -> R.string.theme_dark_desc
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    AdBoardAppTheme {
        SettingsContent(
            themeMode = ThemeMode.SYSTEM,
            onThemeChange = {},
            language = AppLanguage.UKRAINIAN,
            onLanguageChange = {},
            rate = null
        )
    }
}
