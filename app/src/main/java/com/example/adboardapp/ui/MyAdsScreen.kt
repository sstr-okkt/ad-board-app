package com.example.adboardapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adboardapp.R
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.ui.theme.AdBoardAppTheme
import org.koin.androidx.compose.koinViewModel

// My ads with the active, draft and archived tabs
@Composable
fun MyAdsScreen(
    onAdClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: MyAdsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<Ad?>(null) }

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.delete_ad_title)) },
            text = { Text(stringResource(R.string.delete_ad_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDelete?.let { viewModel.delete(it.id) }
                        pendingDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    MyAdsContent(
        uiState = uiState,
        onTabChange = viewModel::onTabChange,
        onAdClick = { ad -> onAdClick(ad.id) },
        onFavoriteClick = viewModel::onFavoriteClick,
        onEditClick = onEditClick,
        onPublishClick = viewModel::publish,
        onArchiveClick = viewModel::archive,
        onDeleteClick = { ad -> pendingDelete = ad },
        onAddClick = onAddClick
    )
}

// Stateless content of the my ads screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAdsContent(
    uiState: MyAdsUiState,
    onTabChange: (MyAdsTab) -> Unit,
    onAdClick: (Ad) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onPublishClick: (Int) -> Unit,
    onArchiveClick: (Int) -> Unit,
    onDeleteClick: (Ad) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_ads_title)) },
                actions = {
                    TextButton(onClick = onAddClick) {
                        Text(stringResource(R.string.add))
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize()
            ) {
                PrimaryTabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
                    MyAdsTab.entries.forEach { tab ->
                        Tab(
                            selected = uiState.selectedTab == tab,
                            onClick = { onTabChange(tab) },
                            text = { Text("${stringResource(tabRes(tab))} (${uiState.count(tab)})") }
                        )
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

                    uiState.visibleAds.isEmpty() -> EmptyMyAds(
                        tab = uiState.selectedTab,
                        onAddClick = onAddClick,
                        modifier = Modifier.weight(1f)
                    )

                    else -> LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = uiState.visibleAds, key = { it.id }) { ad ->
                            Column {
                                AdCard(
                                    ad = ad,
                                    onClick = { onAdClick(ad) },
                                    onFavoriteClick = { onFavoriteClick(ad.id) },
                                    showStatus = true
                                )
                                AdActions(
                                    ad = ad,
                                    onEditClick = onEditClick,
                                    onPublishClick = onPublishClick,
                                    onArchiveClick = onArchiveClick,
                                    onDeleteClick = { onDeleteClick(ad) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Actions available for the ad in its current status
@Composable
private fun AdActions(
    ad: Ad,
    onEditClick: (Int) -> Unit,
    onPublishClick: (Int) -> Unit,
    onArchiveClick: (Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        when (ad.status) {
            AdStatus.DRAFT -> {
                TextButton(onClick = { onPublishClick(ad.id) }) {
                    Text(stringResource(R.string.action_publish))
                }
                TextButton(onClick = { onEditClick(ad.id) }) {
                    Text(stringResource(R.string.action_edit))
                }
            }

            AdStatus.PUBLISHED -> {
                TextButton(onClick = { onEditClick(ad.id) }) {
                    Text(stringResource(R.string.action_edit))
                }
                TextButton(onClick = { onArchiveClick(ad.id) }) {
                    Text(stringResource(R.string.action_archive))
                }
            }

            AdStatus.ARCHIVED -> {
                TextButton(onClick = { onPublishClick(ad.id) }) {
                    Text(stringResource(R.string.action_publish))
                }
            }
        }

        Spacer(Modifier.weight(1f))

        TextButton(onClick = onDeleteClick) {
            Text(
                text = stringResource(R.string.action_delete),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// Placeholder for an empty tab
@Composable
private fun EmptyMyAds(
    tab: MyAdsTab,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (tab) {
                    MyAdsTab.ACTIVE -> stringResource(R.string.empty_active)
                    MyAdsTab.DRAFTS -> stringResource(R.string.empty_drafts)
                    MyAdsTab.ARCHIVED -> stringResource(R.string.empty_archive)
                },
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.empty_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = onAddClick) {
                Text(stringResource(R.string.post_ad))
            }
        }
    }
}

@StringRes
internal fun tabRes(tab: MyAdsTab): Int = when (tab) {
    MyAdsTab.ACTIVE -> R.string.tab_active
    MyAdsTab.DRAFTS -> R.string.tab_drafts
    MyAdsTab.ARCHIVED -> R.string.tab_archived
}

@Preview(showBackground = true)
@Composable
private fun MyAdsContentPreview() {
    AdBoardAppTheme {
        MyAdsContent(
            uiState = MyAdsUiState(ads = previewAds, isLoading = false),
            onTabChange = {},
            onAdClick = {},
            onFavoriteClick = {},
            onEditClick = {},
            onPublishClick = {},
            onArchiveClick = {},
            onDeleteClick = {},
            onAddClick = {}
        )
    }
}
