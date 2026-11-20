package com.example.adboardapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.example.adboardapp.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Shows the date the way a board does: today, yesterday or a short date
@Composable
internal fun publishedAtLabel(raw: String?): String {
    if (raw.isNullOrBlank()) return stringResource(R.string.date_draft)

    val date = runCatching { LocalDate.parse(raw) }.getOrNull() ?: return raw
    val today = LocalDate.now()

    return when {
        date == today -> stringResource(R.string.date_today)
        date == today.minusDays(1) -> stringResource(R.string.date_yesterday)
        else -> {
            val locale = LocalConfiguration.current.locales[0]
            val pattern = if (date.year == today.year) "d MMMM" else "d MMMM yyyy"
            date.format(DateTimeFormatter.ofPattern(pattern, locale))
        }
    }
}

// Localized views counter with the correct plural form
@Composable
internal fun viewsLabel(views: Int): String =
    pluralStringResource(R.plurals.views, views, views)
