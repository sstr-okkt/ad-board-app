package com.example.adboardapp.ui

// Formats a Ukrainian phone number as +380 XX XXX XX XX
internal fun formatPhoneNumber(raw: String): String {
    if (raw.isBlank()) return ""

    val digits = raw.filter { it.isDigit() }
    if (digits.length < 4) return raw

    val local = when {
        digits.startsWith("380") -> digits.drop(3)
        digits.startsWith("0") -> digits.drop(1)
        else -> digits
    }.take(9)

    return buildString {
        append("+380")
        if (local.isNotEmpty()) append(' ').append(local.take(2))
        if (local.length > 2) append(' ').append(local.substring(2, 5))
        if (local.length > 5) append(' ').append(local.substring(5, 7))
        if (local.length > 7) append(' ').append(local.substring(7, 9))
    }
}
