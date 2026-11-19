package com.example.adboardapp.data

import com.example.adboardapp.domain.AdRepository
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author
import kotlinx.coroutines.delay

// In-memory data source, later it can be replaced by a network or database one
class AdRepositoryImpl : AdRepository {

    override suspend fun getAds(): List<Ad> {
        delay(500) // pretend we load from a server
        return ads
    }

    override suspend fun getAdById(id: Int): Ad? {
        delay(300)
        return ads.firstOrNull { it.id == id }
    }

    private companion object {
        // Test data from practical 1
        val ads = listOf(
            Ad(
                id = 1,
                title = "iPhone 13 128GB",
                price = 15_000.0,
                category = "Техніка",
                city = "Одеса",
                views = 540,
                status = AdStatus.PUBLISHED,
                author = Author(11, "Ігор Мельник", 4.9, "+380671234567"),
                publishedAt = "2026-11-12"
            ),
            Ad(
                id = 2,
                title = "Крісло офісне",
                price = 2_300.0,
                category = "Меблі",
                city = "Одеса",
                views = 96,
                status = AdStatus.PUBLISHED,
                author = Author(12, "Алла Дмитрук", 4.3, null),
                publishedAt = "2026-11-10"
            ),
            Ad(
                id = 3,
                title = "Пальто жіноче",
                price = 1_450.0,
                category = "Одяг",
                city = "Львів",
                views = 64,
                status = AdStatus.PUBLISHED,
                author = Author(13, "Софія Гнатюк", 4.8, "+380931112244"),
                publishedAt = "2026-11-09"
            ),
            Ad(
                id = 4,
                title = "Монітор Dell 24\"",
                price = 4_900.0,
                category = "Техніка",
                city = "Київ",
                views = 312,
                status = AdStatus.PUBLISHED,
                author = Author(14, "Роман Олійник", 4.5, "+380502223355"),
                publishedAt = "2026-11-11"
            ),
            Ad(
                id = 5,
                title = "Шафа для одягу",
                price = 3_800.0,
                category = "Меблі",
                city = "Дніпро",
                views = 41,
                status = AdStatus.ARCHIVED,
                author = Author(15, "Ірина Савчук", 4.1, "+380673334466"),
                publishedAt = "2026-10-30"
            ),
            Ad(
                id = 6,
                title = "Настільна лампа",
                price = 650.0,
                category = "Побутова техніка",
                city = "Одеса",
                views = 128,
                status = AdStatus.PUBLISHED,
                author = Author(16, "Дмитро Литвин", 4.6, "+380934445577"),
                publishedAt = "2026-11-08"
            ),
            Ad(
                id = 7,
                title = "Кросівки Nike 42",
                price = 2_100.0,
                category = "Спорт",
                city = "Харків",
                views = 187,
                status = AdStatus.PUBLISHED,
                author = Author(17, "Юлія Романюк", 4.7, "+380675556688"),
                publishedAt = "2026-11-13"
            ),
            Ad(
                id = 8,
                title = "Енциклопедія, 3 томи",
                price = 1_200.0,
                category = "Книги",
                city = "Київ",
                views = 22,
                status = AdStatus.DRAFT,
                author = Author(18, "Олег Мазур", 4.2, "+380936667799"),
                publishedAt = null
            )
        )
    }
}
