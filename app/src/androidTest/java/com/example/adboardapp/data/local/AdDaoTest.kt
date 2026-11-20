package com.example.adboardapp.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.AdSource
import com.example.adboardapp.model.AdStatus
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: AdDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.adDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun replaceRemoteKeepsFavoriteAndViews() = runBlocking {
        dao.insert(entity(id = 1, views = 42, isFavorite = true))

        dao.replaceRemote(
            ads = listOf(entity(id = 1, views = 0, isFavorite = false)),
            images = emptyList()
        )

        val saved = dao.getById(1)!!.ad
        assertEquals(42, saved.views)
        assertTrue(saved.isFavorite)
    }

    @Test
    fun replaceRemoteKeepsLocalAds() = runBlocking {
        dao.insert(entity(id = -1, source = AdSource.LOCAL, status = AdStatus.DRAFT))
        dao.insert(entity(id = 1))

        dao.replaceRemote(listOf(entity(id = 2)), emptyList())

        assertNotNull(dao.getById(-1))
        assertNull(dao.getById(1))
        assertNotNull(dao.getById(2))
    }

    @Test
    fun deleteByIdRemovesPhotos() = runBlocking {
        dao.insert(entity(id = -1, source = AdSource.LOCAL))
        dao.insertImages(
            listOf(
                AdImageEntity(adId = -1, url = "first", position = 0),
                AdImageEntity(adId = -1, url = "second", position = 1)
            )
        )

        dao.deleteById(-1)

        assertNull(dao.getById(-1))
        assertEquals(0, countImages(-1))
    }

    @Test
    fun nextLocalIdReturnsNegativeIds() = runBlocking {
        assertEquals(-1, dao.nextLocalId())
        dao.insert(entity(id = -1, source = AdSource.LOCAL))
        assertEquals(-2, dao.nextLocalId())
    }

    private fun countImages(adId: Int): Int {
        val sqlite = database.openHelper.readableDatabase
        sqlite.query("SELECT COUNT(*) FROM ad_images WHERE adId = ?", arrayOf(adId)).use { cursor ->
            cursor.moveToFirst()
            return cursor.getInt(0)
        }
    }

    private fun entity(
        id: Int,
        source: AdSource = AdSource.REMOTE,
        status: AdStatus = AdStatus.PUBLISHED,
        views: Int = 0,
        isFavorite: Boolean = false
    ) = AdEntity(
        id = id,
        title = "Ad $id",
        description = "Description",
        price = 100.0,
        isNegotiable = false,
        category = "smartphones",
        city = "kyiv",
        condition = AdCondition.USED.name,
        views = views,
        status = status.name,
        authorName = "Seller",
        authorRating = 4.0,
        authorContact = "+380671112233",
        publishedAt = "2026-11-01",
        source = source.name,
        isFavorite = isFavorite
    )
}
