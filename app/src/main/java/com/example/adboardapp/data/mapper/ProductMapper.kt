package com.example.adboardapp.data.mapper

import com.example.adboardapp.data.remote.ProductDto
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author

// Converts an API product into the domain ad
fun ProductDto.toAd(): Ad {
    val reviewer = reviews.firstOrNull()
    return Ad(
        id = id,
        title = title,
        price = price * 41, // rough USD to UAH for the demo
        category = categoryLabel(category),
        city = "Онлайн",
        views = stock,
        status = AdStatus.PUBLISHED,
        author = Author(
            id = id,
            name = reviewer?.reviewerName ?: brand ?: "Продавець",
            rating = reviewer?.rating ?: 0.0,
            contact = null
        ),
        publishedAt = meta?.createdAt?.take(10)
    )
}

// Turns API category names into readable labels
private fun categoryLabel(raw: String): String = when (raw) {
    "beauty" -> "Краса"
    "fragrances" -> "Парфуми"
    "furniture" -> "Меблі"
    "groceries" -> "Продукти"
    "home-decoration" -> "Декор"
    "kitchen-accessories" -> "Кухня"
    "laptops" -> "Ноутбуки"
    "mens-shirts" -> "Одяг"
    "mens-shoes" -> "Взуття"
    "mens-watches" -> "Годинники"
    "mobile-accessories" -> "Аксесуари"
    "smartphones" -> "Смартфони"
    "sports-accessories" -> "Спорт"
    "sunglasses" -> "Окуляри"
    "tablets" -> "Планшети"
    "womens-bags" -> "Сумки"
    "womens-dresses" -> "Сукні"
    "womens-shoes" -> "Взуття"
    "womens-watches" -> "Годинники"
    else -> raw.replaceFirstChar { it.uppercase() }
}
