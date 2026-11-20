package com.example.adboardapp.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adboardapp.R
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.ui.theme.AdBoardAppTheme
import org.koin.androidx.compose.koinViewModel

// Brand font of the wordmark
private val BrandFont = FontFamily(Font(R.font.archivo_black))

// Connects the ViewModel with the stateless screen content
@Composable
fun AdListScreen(
    onAdClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    viewModel: ListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AdListContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onCategoryChange = viewModel::onCategoryChange,
        onSortChange = viewModel::onSortChange,
        onFiltersChange = viewModel::onFiltersChange,
        onAdClick = { ad -> onAdClick(ad.id) },
        onFavoriteClick = viewModel::onFavoriteClick,
        onAddClick = onAddClick,
        onRetry = viewModel::refresh
    )
}

// Renders the screen from a ready UiState, no ViewModel here
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdListContent(
    uiState: ListUiState,
    onQueryChange: (String) -> Unit,
    onCategoryChange: (String?) -> Unit,
    onSortChange: (AdSort) -> Unit,
    onFiltersChange: (AdFilters) -> Unit,
    onAdClick: (Ad) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontFamily = BrandFont,
                        fontSize = 40.sp,
                        lineHeight = 40.sp,
                        modifier = Modifier.height(40.dp)
                    )
                },
                actions = {
                    AddAdButton(onClick = onAddClick)
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxHeight()
            ) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    label = { Text(stringResource(R.string.search)) },
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (uiState.syncError) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.offline_banner),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = onRetry) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }

                CategoryChips(
                    categories = uiState.categories,
                    selected = uiState.selectedCategory,
                    onSelect = onCategoryChange
                )

                if (!uiState.isLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(
                                R.string.found_count,
                                uiState.visibleAds.size,
                                uiState.ads.size
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { showFilters = true }) {
                            Text(
                                text = if (uiState.hasActiveFilters) {
                                    stringResource(
                                        R.string.filters_with_count,
                                        uiState.activeFilterCount
                                    )
                                } else {
                                    stringResource(R.string.filters)
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        SortMenu(sort = uiState.sort, onSortChange = onSortChange)
                    }
                }

                when {
                    uiState.isLoading -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    uiState.visibleAds.isEmpty() -> EmptyState(
                        hasQuery = uiState.query.isNotBlank(),
                        hasFilters = uiState.hasActiveFilters,
                        onAddClick = onAddClick,
                        onResetFilters = {
                            onFiltersChange(AdFilters())
                            onCategoryChange(null)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    else -> LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = uiState.visibleAds, key = { it.id }) { ad ->
                            AdCard(
                                ad = ad,
                                onClick = { onAdClick(ad) },
                                onFavoriteClick = { onFavoriteClick(ad.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFilters) {
        FiltersSheet(
            initial = uiState.filters,
            cities = uiState.cities,
            resultCount = { draft -> uiState.copy(filters = draft).visibleAds.size },
            onApply = onFiltersChange,
            onDismiss = { showFilters = false }
        )
    }
}

// Bottom sheet with the extra filters
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersSheet(
    initial: AdFilters,
    cities: List<String>,
    resultCount: (AdFilters) -> Int,
    onApply: (AdFilters) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var draft by remember { mutableStateOf(initial) }
    val result = normalized(draft)

    val cityOptions = listOf(
        LabeledOption(value = "", label = stringResource(R.string.all_cities))
    ) + cities.map { key ->
        LabeledOption(value = key, label = cityLabel(key))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = stringResource(R.string.filters_title),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(12.dp))

            OptionDropdown(
                label = stringResource(R.string.city_label),
                options = cityOptions,
                selectedValue = draft.city ?: "",
                onSelect = { value ->
                    draft = draft.copy(city = value.takeUnless { it.isEmpty() })
                }
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.price_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriceField(
                    label = stringResource(R.string.price_from),
                    value = draft.minPrice,
                    modifier = Modifier.weight(1f),
                    onValueChange = { draft = draft.copy(minPrice = it) }
                )
                PriceField(
                    label = stringResource(R.string.price_to),
                    value = draft.maxPrice,
                    modifier = Modifier.weight(1f),
                    onValueChange = { draft = draft.copy(maxPrice = it) }
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.condition_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = draft.condition == null,
                    onClick = { draft = draft.copy(condition = null) },
                    label = { Text(stringResource(R.string.all)) }
                )
                AdCondition.entries.forEach { condition ->
                    FilterChip(
                        selected = draft.condition == condition,
                        onClick = { draft = draft.copy(condition = condition) },
                        label = { Text(stringResource(conditionRes(condition))) }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = draft.onlyWithPhoto,
                    onCheckedChange = { draft = draft.copy(onlyWithPhoto = it) }
                )
                Text(stringResource(R.string.only_with_photo))
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val empty = AdFilters()
                        draft = empty
                        onApply(empty)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.reset))
                }
                Button(
                    onClick = {
                        onApply(result)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.show_count, resultCount(result)))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// Price input for the filters
@Composable
private fun PriceField(
    label: String,
    value: Int?,
    modifier: Modifier = Modifier,
    onValueChange: (Int?) -> Unit
) {
    OutlinedTextField(
        value = value?.toString() ?: "",
        onValueChange = { text ->
            if (text.all { it.isDigit() } && text.length <= 9) {
                onValueChange(text.toIntOrNull())
            }
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

// Swaps min and max when the user typed them in the wrong order
private fun normalized(filters: AdFilters): AdFilters {
    val min = filters.minPrice
    val max = filters.maxPrice
    return if (min != null && max != null && min > max) {
        filters.copy(minPrice = max, maxPrice = min)
    } else {
        filters
    }
}

// Horizontal list of category filters
@Composable
private fun CategoryChips(
    categories: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    val options = categories
        .map { key -> key to categoryLabel(key) }
        .sortedBy { it.second }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text(stringResource(R.string.all)) },
                modifier = Modifier.testTag("category_all")
            )
        }
        items(options) { (key, label) ->
            FilterChip(
                selected = selected == key,
                onClick = { onSelect(key) },
                label = { Text(label) },
                modifier = Modifier.testTag("category_$key")
            )
        }
    }
}

// Dropdown with the list sort order
@Composable
private fun SortMenu(
    sort: AdSort,
    onSortChange: (AdSort) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(
                text = "${stringResource(sortRes(sort))} ▾",
                style = MaterialTheme.typography.bodySmall
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AdSort.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(sortRes(option))) },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Shown when the list has no ads to display
@Composable
private fun EmptyState(
    hasQuery: Boolean,
    hasFilters: Boolean,
    onAddClick: () -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFiltered = hasQuery || hasFilters
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(
                    if (isFiltered) R.string.nothing_found else R.string.no_ads
                ),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            if (isFiltered) {
                Text(
                    text = stringResource(R.string.filtered_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (hasFilters) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = onResetFilters) {
                        Text(stringResource(R.string.reset_filters))
                    }
                }
            } else {
                Button(onClick = onAddClick) {
                    Text(stringResource(R.string.post_ad))
                }
            }
        }
    }
}

@StringRes
internal fun sortRes(sort: AdSort): Int = when (sort) {
    AdSort.NEWEST -> R.string.sort_newest
    AdSort.CHEAPEST -> R.string.sort_cheapest
    AdSort.EXPENSIVE -> R.string.sort_expensive
}

@StringRes
internal fun conditionRes(condition: AdCondition): Int = when (condition) {
    AdCondition.NEW -> R.string.condition_new
    AdCondition.USED -> R.string.condition_used
}

@Preview(showBackground = true)
@Composable
private fun AdListContentPreview() {
    AdBoardAppTheme {
        AdListContent(
            uiState = ListUiState(ads = previewAds, isLoading = false),
            onQueryChange = {},
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
