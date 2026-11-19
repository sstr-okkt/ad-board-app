package com.example.adboardapp.model

// Ad state on the board
enum class AdStatus {
    DRAFT,      // not published yet
    PUBLISHED,  // visible to everyone
    ARCHIVED    // removed from the board
}

// User who posted the ad
data class Author(
    val id: Int,
    val name: String,
    val rating: Double,
    val contact: String? = null // null if the author left no contacts
)

// Main entity shown on the board
data class Ad(
    val id: Int,
    val title: String,
    val price: Double,
    val category: String,
    val city: String,
    val views: Int,
    val status: AdStatus,
    val author: Author,
    val publishedAt: String? = null // null for drafts
) {
    // Adds thousand separators: 18500.0 -> "18 500"
    fun formattedPrice(): String {
        val grouped = price.toLong().toString()
            .reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()
        return "$grouped грн"
    }
}

// Result of an operation: data or an error message
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}
