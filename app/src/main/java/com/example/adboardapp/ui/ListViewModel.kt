package com.example.adboardapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Test data from practical 1, will be replaced by a repository later
internal val sampleAds = listOf(
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

// Holds the screen state and handles user actions
class ListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ListUiState(isLoading = true))
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init {
        // Pretend we load ads from a data source
        viewModelScope.launch {
            delay(500)
            _uiState.update { it.copy(ads = sampleAds, isLoading = false) }
        }
    }

    // Called from the search field
    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }
}
