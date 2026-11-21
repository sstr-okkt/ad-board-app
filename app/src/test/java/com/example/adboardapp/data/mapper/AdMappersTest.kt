package com.example.adboardapp.data.mapper

import com.example.adboardapp.data.local.AdImageEntity
import com.example.adboardapp.data.local.AdWithImages
import com.example.adboardapp.data.remote.MetaDto
import com.example.adboardapp.data.remote.ProductDto
import com.example.adboardapp.data.remote.ReviewDto
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.AdSource
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdMappersTest {

    private fun dto(
        id: Int = 1,
        title: String = "Phone",
        price: Double = 100.0,
        category: String = "smartphones",
        thumbnail: String? = "https://example.com/thumb.jpg",
        images: List<String> = listOf(
            "https://example.com/1.jpg",
            "https://example.com/2.jpg"
        ),
        meta: MetaDto? = MetaDto("2026-01-01T00:00:00Z"),
        reviews: List<ReviewDto> = listOf(ReviewDto("Avery", 4.5))
    ) = ProductDto(
        id = id,
        title = title,
        price = price,
        category = category,
        stock = 5,
        brand = "Samsung",
        thumbnail = thumbnail,
        images = images,
        meta = meta,
        reviews = reviews
    )

    @Test
    fun productIsConvertedToEntity() {
        val entity = dto().toEntity()

        assertEquals(1, entity.id)
        assertEquals("Phone", entity.title)
        assertEquals(4100.0, entity.price, 0.01)
        assertEquals("smartphones", entity.category)
        assertEquals("odesa", entity.city)
        assertEquals(AdSource.REMOTE.name, entity.source)
        assertEquals(AdStatus.PUBLISHED.name, entity.status)
        assertNotNull(entity.publishedAt)
        assertTrue(entity.description.contains("phone"))
        assertTrue(entity.authorContact.orEmpty().startsWith("+380"))
        assertTrue(entity.views > 0)
        assertEquals("2026-01-01", entity.publishedAt)
    }

    @Test
    fun missingCreationDateFallsBackToARecentDate() {
        val entity = dto(id = 1, meta = null).toEntity()

        val published = LocalDate.parse(entity.publishedAt)
        assertTrue(published in LocalDate.now().minusDays(30L)..LocalDate.now())
    }

    @Test
    fun ratingAveragesAllReviews() {
        val reviews = listOf(
            ReviewDto("Avery", 4.0),
            ReviewDto("Blake", 5.0),
            ReviewDto("Casey", 0.0)
        )

        val entity = dto(reviews = reviews).toEntity()

        assertEquals(4.5, entity.authorRating, 0.01)
    }

    @Test
    fun descriptionUsesTheLocalizedCityName() {
        val entity = dto(id = 1).toEntity()

        assertEquals("odesa", entity.city)
        assertTrue(entity.description.contains("Одеса"))
        assertFalse(entity.description.contains("odesa"))
    }

    @Test
    fun newConditionIsUsedForEveryThirdAd() {
        assertEquals(AdCondition.NEW.name, dto(id = 3).toEntity().condition)
        assertEquals(AdCondition.USED.name, dto(id = 1).toEntity().condition)
    }

    @Test
    fun photosAreMappedWithPositions() {
        val images = dto().toImageEntities()

        assertEquals(2, images.size)
        assertEquals(listOf(0, 1), images.map { it.position })
        assertTrue(images.all { it.adId == 1 })
    }

    @Test
    fun thumbnailIsUsedWhenGalleryIsEmpty() {
        val images = dto(images = emptyList()).toImageEntities()

        assertEquals(1, images.size)
        assertEquals("https://example.com/thumb.jpg", images.first().url)
    }

    @Test
    fun roomRowIsConvertedToDomain() {
        val entity = dto(id = 2).toEntity()
        val row = AdWithImages(
            ad = entity,
            images = listOf(
                AdImageEntity(id = 2, adId = 2, url = "second", position = 1),
                AdImageEntity(id = 1, adId = 2, url = "first", position = 0)
            )
        )

        val ad = row.toDomain()

        assertEquals(2, ad.id)
        assertEquals(listOf("first", "second"), ad.imageUrls)
        assertEquals(AdSource.REMOTE, ad.source)
        assertEquals(AdStatus.PUBLISHED, ad.status)
        assertEquals("Ігор Мельник", ad.author.name)
        assertEquals(4.5, ad.author.rating, 0.001)
    }

    @Test
    fun domainAdIsConvertedBackToEntity() {
        val ad = Ad(
            id = -1,
            title = "My ad",
            description = "Description",
            price = 1500.0,
            isNegotiable = true,
            category = "Меблі",
            city = "Київ",
            condition = AdCondition.NEW,
            imageUrls = emptyList(),
            views = 3,
            status = AdStatus.DRAFT,
            author = Author(-1, "Ви", 0.0, "+380671112233"),
            publishedAt = null,
            source = AdSource.LOCAL,
            isFavorite = true
        )

        val entity = ad.toEntity()

        assertEquals(ad.id, entity.id)
        assertEquals(ad.title, entity.title)
        assertEquals(ad.description, entity.description)
        assertEquals(ad.price, entity.price, 0.001)
        assertEquals(ad.category, entity.category)
        assertEquals(ad.city, entity.city)
        assertEquals(ad.condition.name, entity.condition)
        assertEquals(ad.status.name, entity.status)
        assertEquals(ad.author.contact, entity.authorContact)
        assertEquals(ad.source.name, entity.source)
        assertEquals(ad.isFavorite, entity.isFavorite)
    }
}
