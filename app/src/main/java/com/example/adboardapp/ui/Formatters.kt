package com.example.adboardapp.ui

import java.time.LocalDate

private val months = listOf(
    "січня", "лютого", "березня", "квітня", "травня", "червня",
    "липня", "серпня", "вересня", "жовтня", "листопада", "грудня"
)

// Shows the date the way a board does: today, yesterday or a short date
internal fun formatPublishedAt(raw: String?): String {
    if (raw.isNullOrBlank()) return "чернетка"

    val date = runCatching { LocalDate.parse(raw) }.getOrNull() ?: return raw
    val today = LocalDate.now()

    return when {
        date == today -> "Сьогодні"
        date == today.minusDays(1) -> "Вчора"
        date.year == today.year -> "${date.dayOfMonth} ${months[date.monthValue - 1]}"
        else -> "${date.dayOfMonth} ${months[date.monthValue - 1]} ${date.year}"
    }
}

// Correct Ukrainian plural for the views counter
internal fun viewsLabel(views: Int): String {
    val lastTwo = views % 100
    val last = views % 10
    val word = when {
        lastTwo in 11..14 -> "переглядів"
        last == 1 -> "перегляд"
        last in 2..4 -> "перегляди"
        else -> "переглядів"
    }
    return "$views $word"
}
