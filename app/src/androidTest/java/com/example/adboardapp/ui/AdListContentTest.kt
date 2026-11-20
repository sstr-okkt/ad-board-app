package com.example.adboardapp.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author
import com.example.adboardapp.ui.theme.AdBoardAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AdListContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val ads = listOf(
        ad(id = 1, title = "iPhone 13", price = 15000.0, category = "smartphones"),
        ad(id = 2, title = "Крісло офісне", price = 2300.0, category = "furniture")
    )

    @Test
    fun listShowsAdsAndFiltersByQuery() {
        val state = mutableStateOf(ListUiState(ads = ads, isLoading = false))

        composeRule.setContent {
            AdBoardAppTheme {
                AdListContent(
                    uiState = state.value,
                    onQueryChange = { state.value = state.value.copy(query = it) },
                    onCategoryChange = {},
                    onSortChange = {},
                    onFiltersChange = {},
                    onAdClick = {},
                    onFavoriteClick = {},
                    onAddClick = {},
                    onRetry = {}
                )
            }
        }

        composeRule.onNodeWithText("iPhone 13").assertIsDisplayed()
        composeRule.onNodeWithText("Крісло офісне").assertIsDisplayed()

        composeRule.onNode(hasSetTextAction()).performTextInput("iphone")
        composeRule.waitForIdle()

        composeRule.onNodeWithText("iPhone 13").assertIsDisplayed()
        composeRule.onNodeWithText("Крісло офісне").assertDoesNotExist()
    }

    @Test
    fun categoryChipReportsSelection() {
        var selected: String? = null

        composeRule.setContent {
            AdBoardAppTheme {
                AdListContent(
                    uiState = ListUiState(ads = ads, isLoading = false),
                    onQueryChange = {},
                    onCategoryChange = { selected = it },
                    onSortChange = {},
                    onFiltersChange = {},
                    onAdClick = {},
                    onFavoriteClick = {},
                    onAddClick = {},
                    onRetry = {}
                )
            }
        }

        composeRule.onNodeWithTag("category_furniture").performClick()

        assertEquals("furniture", selected)
    }

    private fun ad(
        id: Int,
        title: String,
        price: Double,
        category: String
    ) = Ad(
        id = id,
        title = title,
        description = "Description",
        price = price,
        category = category,
        city = "kyiv",
        condition = AdCondition.USED,
        views = 10,
        status = AdStatus.PUBLISHED,
        author = Author(id, "Seller", 4.0),
        publishedAt = "2026-11-01"
    )
}
