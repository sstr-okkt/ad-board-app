package com.example.adboardapp.ui

import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author

// Small fake list used only by @Preview
internal val previewAds = listOf(
    Ad(
        id = 1,
        title = "iPhone 13 128GB",
        price = 15_000.0,
        category = "smartphones",
        city = "odesa",
        views = 540,
        status = AdStatus.PUBLISHED,
        author = Author(1, "Ігор Мельник", 4.9, "+380671234567"),
        publishedAt = "2026-11-12"
    ),
    Ad(
        id = 2,
        title = "Крісло офісне",
        price = 2_300.0,
        category = "furniture",
        city = "odesa",
        views = 96,
        status = AdStatus.DRAFT,
        author = Author(2, "Алла Дмитрук", 4.3, null),
        publishedAt = null
    )
)
