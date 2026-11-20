package com.example.adboardapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.label
import com.example.adboardapp.ui.theme.AdBoardAppTheme
import org.koin.androidx.compose.koinViewModel

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
        topBar = {
            TopAppBar(
                title = { Text("Дошка оголошень") },
                actions = {
                    TextButton(onClick = onAddClick) {
                        Text("Додати")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
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
                    label = { Text("Пошук") },
                    placeholder = { Text("Назва, опис або місто") },
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
                                text = "Немає з'єднання. Показано збережені дані",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = onRetry) {
                                Text("Повторити")
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
                            text = "Знайдено ${uiState.visibleAds.size} із ${uiState.ads.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { showFilters = true }) {
                            Text(
                                text = if (uiState.hasActiveFilters) {
                                    "Фільтри (${uiState.activeFilterCount})"
                                } else {
                                    "Фільтри"
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
                text = "Фільтри",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(12.dp))

            OptionDropdown(
                label = "Місто",
                options = listOf(ALL_CITIES) + cities,
                selected = draft.city ?: ALL_CITIES,
                onSelect = { value ->
                    draft = draft.copy(city = value.takeUnless { it == ALL_CITIES })
                }
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Ціна, грн",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriceField(
                    label = "Від",
                    value = draft.minPrice,
                    modifier = Modifier.weight(1f),
                    onValueChange = { draft = draft.copy(minPrice = it) }
                )
                PriceField(
                    label = "До",
                    value = draft.maxPrice,
                    modifier = Modifier.weight(1f),
                    onValueChange = { draft = draft.copy(maxPrice = it) }
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Стан",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = draft.condition == null,
                    onClick = { draft = draft.copy(condition = null) },
                    label = { Text("Усі") }
                )
                AdCondition.entries.forEach { condition ->
                    FilterChip(
                        selected = draft.condition == condition,
                        onClick = { draft = draft.copy(condition = condition) },
                        label = { Text(condition.label()) }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = draft.onlyWithPhoto,
                    onCheckedChange = { draft = draft.copy(onlyWithPhoto = it) }
                )
                Text("Тільки з фото")
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { draft = AdFilters() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Скинути")
                }
                Button(
                    onClick = {
                        onApply(result)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Показати ${resultCount(result)}")
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

private const val ALL_CITIES = "Усі міста"

// Horizontal list of category filters
@Composable
private fun CategoryChips(
    categories: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text("Усі") }
            )
        }
        items(categories) { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(category) }
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
                text = "${sort.label} ▾",
                style = MaterialTheme.typography.bodySmall
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AdSort.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
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
                text = if (isFiltered) "Нічого не знайдено" else "На дошці ще немає оголошень",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            if (isFiltered) {
                Text(
                    text = "Спробуйте змінити запит або фільтри",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Button(onClick = onAddClick) {
                    Text("Подати оголошення")
                }
            }
        }
    }
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
