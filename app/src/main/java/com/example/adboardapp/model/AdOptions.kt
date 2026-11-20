package com.example.adboardapp.model

// Categories used by the demo feed and by the form
val adCategories = listOf(
    "Ноутбуки", "Смартфони", "Планшети", "Аксесуари", "Одяг", "Взуття",
    "Годинники", "Меблі", "Декор", "Кухня", "Краса", "Парфуми",
    "Продукти", "Спорт", "Окуляри", "Сумки", "Сукні"
)

// Cities available for new ads
val adCities = listOf(
    "Київ", "Одеса", "Львів", "Дніпро",
    "Харків", "Вінниця", "Запоріжжя", "Івано-Франківськ"
)

// Human readable item condition
fun AdCondition.label(): String = when (this) {
    AdCondition.NEW -> "Нове"
    AdCondition.USED -> "Вживане"
}
