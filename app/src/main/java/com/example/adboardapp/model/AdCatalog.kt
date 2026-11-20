package com.example.adboardapp.model

// Canonical category keys stored in the database
val adCategories = listOf(
    "laptops", "smartphones", "tablets", "accessories", "clothing", "shoes",
    "watches", "furniture", "decor", "kitchen", "beauty", "fragrances",
    "groceries", "sports", "sunglasses", "bags", "dresses"
)

// Canonical city keys stored in the database
val adCities = listOf(
    "kyiv", "odesa", "lviv", "dnipro",
    "kharkiv", "vinnytsia", "zaporizhzhia", "ivano-frankivsk"
)

// Names of the categories in both languages
private val categoryNames = mapOf(
    "laptops" to ("Ноутбуки" to "Laptops"),
    "smartphones" to ("Смартфони" to "Smartphones"),
    "tablets" to ("Планшети" to "Tablets"),
    "accessories" to ("Аксесуари" to "Accessories"),
    "clothing" to ("Одяг" to "Clothing"),
    "shoes" to ("Взуття" to "Shoes"),
    "watches" to ("Годинники" to "Watches"),
    "furniture" to ("Меблі" to "Furniture"),
    "decor" to ("Декор" to "Decor"),
    "kitchen" to ("Кухня" to "Kitchen"),
    "beauty" to ("Краса" to "Beauty"),
    "fragrances" to ("Парфуми" to "Fragrances"),
    "groceries" to ("Продукти" to "Groceries"),
    "sports" to ("Спорт" to "Sports"),
    "sunglasses" to ("Окуляри" to "Sunglasses"),
    "bags" to ("Сумки" to "Bags"),
    "dresses" to ("Сукні" to "Dresses")
)

// Names of the cities in both languages
private val cityNames = mapOf(
    "kyiv" to ("Київ" to "Kyiv"),
    "odesa" to ("Одеса" to "Odesa"),
    "lviv" to ("Львів" to "Lviv"),
    "dnipro" to ("Дніпро" to "Dnipro"),
    "kharkiv" to ("Харків" to "Kharkiv"),
    "vinnytsia" to ("Вінниця" to "Vinnytsia"),
    "zaporizhzhia" to ("Запоріжжя" to "Zaporizhzhia"),
    "ivano-frankivsk" to ("Івано-Франківськ" to "Ivano-Frankivsk")
)

// Pairs of old Ukrainian values and the new keys, used by the Room migration
val legacyCategoryKeys = mapOf(
    "Ноутбуки" to "laptops",
    "Смартфони" to "smartphones",
    "Планшети" to "tablets",
    "Аксесуари" to "accessories",
    "Одяг" to "clothing",
    "Взуття" to "shoes",
    "Годинники" to "watches",
    "Меблі" to "furniture",
    "Декор" to "decor",
    "Кухня" to "kitchen",
    "Краса" to "beauty",
    "Парфуми" to "fragrances",
    "Продукти" to "groceries",
    "Спорт" to "sports",
    "Окуляри" to "sunglasses",
    "Сумки" to "bags",
    "Сукні" to "dresses"
)

val legacyCityKeys = mapOf(
    "Київ" to "kyiv",
    "Одеса" to "odesa",
    "Львів" to "lviv",
    "Дніпро" to "dnipro",
    "Харків" to "kharkiv",
    "Вінниця" to "vinnytsia",
    "Запоріжжя" to "zaporizhzhia",
    "Івано-Франківськ" to "ivano-frankivsk"
)

// Display name of a category key, falls back to the raw value
fun categoryName(key: String, language: String): String {
    val names = categoryNames[key] ?: return key
    return if (language == "en") names.second else names.first
}

// Display name of a city key, falls back to the raw value
fun cityName(key: String, language: String): String {
    val names = cityNames[key] ?: return key
    return if (language == "en") names.second else names.first
}

// Both language variants are used by the search
fun categorySearchNames(key: String): List<String> =
    categoryNames[key]?.let { listOf(it.first, it.second) } ?: listOf(key)

fun citySearchNames(key: String): List<String> =
    cityNames[key]?.let { listOf(it.first, it.second) } ?: listOf(key)

// Maps the demo API category to a canonical key
fun categoryKeyFromApi(raw: String): String = when (raw) {
    "beauty" -> "beauty"
    "fragrances" -> "fragrances"
    "furniture" -> "furniture"
    "groceries" -> "groceries"
    "home-decoration" -> "decor"
    "kitchen-accessories" -> "kitchen"
    "laptops" -> "laptops"
    "mens-shirts", "womens-dresses" -> if (raw == "womens-dresses") "dresses" else "clothing"
    "mens-shoes", "womens-shoes" -> "shoes"
    "mens-watches", "womens-watches" -> "watches"
    "mobile-accessories" -> "accessories"
    "smartphones" -> "smartphones"
    "sports-accessories" -> "sports"
    "sunglasses" -> "sunglasses"
    "tablets" -> "tablets"
    "womens-bags" -> "bags"
    else -> raw
}
