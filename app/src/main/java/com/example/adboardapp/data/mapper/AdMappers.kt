package com.example.adboardapp.data.mapper

import com.example.adboardapp.data.local.AdEntity
import com.example.adboardapp.data.local.AdImageEntity
import com.example.adboardapp.data.local.AdWithImages
import com.example.adboardapp.data.remote.ProductDto
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.AdSource
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author
import com.example.adboardapp.model.adCities
import java.time.LocalDate
import kotlin.math.roundToInt

// Ukrainian sellers used for the demo feed
private val sellerNames = listOf(
    "Олександр Коваль", "Марія Шевченко", "Ігор Мельник", "Анна Бондар",
    "Дмитро Ткачук", "Олена Гриценко", "Сергій Мороз", "Юлія Савченко"
)

// Converts an API product into a Room entity
fun ProductDto.toEntity(): AdEntity {
    val reviewer = reviews.firstOrNull()
    val isNew = id % 3 == 0
    val city = adCities[id % adCities.size]

    return AdEntity(
        id = id,
        title = title,
        description = descriptionFor(this, isNew, city),
        price = (price * 41).roundToInt().toDouble(), // rough USD to UAH for the demo
        isNegotiable = id % 4 == 0,
        category = categoryLabel(category),
        city = city,
        condition = if (isNew) AdCondition.NEW.name else AdCondition.USED.name,
        views = stock * 7 + id * 13,
        status = AdStatus.PUBLISHED.name,
        authorName = sellerNames[id % sellerNames.size],
        authorRating = reviewer?.rating?.takeIf { it > 0.0 } ?: (4.0 + (id % 10) / 10.0),
        authorContact = phoneFor(id),
        publishedAt = LocalDate.now().minusDays((id % 30).toLong()).toString(),
        source = AdSource.REMOTE.name,
        isFavorite = false
    )
}

// Photos of the product, the thumbnail is used when the gallery is empty
fun ProductDto.toImageEntities(): List<AdImageEntity> {
    val urls = images.ifEmpty { listOfNotNull(thumbnail) }
    return urls.mapIndexed { index, url ->
        AdImageEntity(adId = id, url = url, position = index)
    }
}

// Converts a Room row with photos into the domain model
fun AdWithImages.toDomain(): Ad = Ad(
    id = ad.id,
    title = ad.title,
    description = ad.description,
    price = ad.price,
    isNegotiable = ad.isNegotiable,
    category = ad.category,
    city = ad.city,
    condition = runCatching { AdCondition.valueOf(ad.condition) }
        .getOrDefault(AdCondition.USED),
    imageUrls = images.sortedBy { it.position }.map { it.url },
    views = ad.views,
    status = runCatching { AdStatus.valueOf(ad.status) }
        .getOrDefault(AdStatus.PUBLISHED),
    author = Author(
        id = ad.id,
        name = ad.authorName,
        rating = ad.authorRating,
        contact = ad.authorContact
    ),
    publishedAt = ad.publishedAt,
    source = runCatching { AdSource.valueOf(ad.source) }
        .getOrDefault(AdSource.REMOTE),
    isFavorite = ad.isFavorite
)

// Converts the domain model back into a Room entity
fun Ad.toEntity(): AdEntity = AdEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    isNegotiable = isNegotiable,
    category = category,
    city = city,
    condition = condition.name,
    views = views,
    status = status.name,
    authorName = author.name,
    authorRating = author.rating,
    authorContact = author.contact,
    publishedAt = publishedAt,
    source = source.name,
    isFavorite = isFavorite
)

// Short ad text built from the product data
private fun descriptionFor(dto: ProductDto, isNew: Boolean, city: String): String =
    buildString {
        append("Продаю ${dto.title.lowercase()}.")
        dto.brand?.let { append(" Бренд: $it.") }
        append(if (isNew) " Стан новий." else " Стан вживаний, все працює.")
        append(" Можливий огляд у місті $city.")
        append(" Пишіть або телефонуйте, відповідаю швидко.")
    }

// Stable phone number per ad, Ukrainian format
private fun phoneFor(id: Int): String {
    val operator = listOf("67", "50", "63", "93", "96", "68")[id % 6]
    val tail = (id * 123457 % 10_000_000).toString().padStart(7, '0')
    return "+380$operator$tail"
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
