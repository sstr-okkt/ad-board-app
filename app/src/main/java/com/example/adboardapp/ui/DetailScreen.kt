package com.example.adboardapp.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.adboardapp.R
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.ui.theme.AdBoardAppTheme

// Connects the ViewModel with the stateless details content
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) onDeleted()
    }

    DetailContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::reload,
        onFavoriteClick = viewModel::onFavoriteClick,
        onEdit = onEdit,
        onDeleteConfirmed = viewModel::delete
    )
}

// Renders the details screen from a ready UiState
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onFavoriteClick: () -> Unit,
    onEdit: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ad = uiState.ad
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_ad_title)) },
            text = { Text(stringResource(R.string.delete_ad_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteConfirmed()
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ad_screen_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(stringResource(R.string.back))
                    }
                },
                actions = {
                    if (ad != null) {
                        TextButton(onClick = onFavoriteClick) {
                            Text(
                                text = if (ad.isFavorite) "♥" else "♡",
                                fontSize = 20.sp,
                                color = if (ad.isFavorite) {
                                    Color(0xFFE91E63)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            uiState.errorMessage != null -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.try_again))
                    }
                }
            }

            ad == null -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.ad_not_found),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                DetailBody(
                    ad = ad,
                    onEdit = onEdit,
                    onDelete = { showDeleteDialog = true }
                )
            }
        }
    }
}

// Main content of the details screen
@Composable
private fun DetailBody(
    ad: Ad,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var phoneVisible by rememberSaveable(ad.id) { mutableStateOf(false) }
    val currency = stringResource(R.string.currency_uah)
    val shareChooserTitle = stringResource(R.string.share_chooser)
    val city = cityLabel(ad.city)

    Column(
        modifier = Modifier
            .widthIn(max = 840.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        PhotoGallery(
            urls = ad.imageUrls,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        )

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = ad.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            if (ad.isLocal) {
                Spacer(Modifier.width(8.dp))
                StatusBadge(ad.status)
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = ad.formattedPrice(currency),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (ad.isNegotiable) {
                Spacer(Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = stringResource(R.string.negotiable),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        InfoRow(label = stringResource(R.string.category_label), value = categoryLabel(ad.category))
        InfoRow(label = stringResource(R.string.condition_label), value = stringResource(conditionRes(ad.condition)))
        InfoRow(label = stringResource(R.string.city_label), value = cityLabel(ad.city))
        InfoRow(label = stringResource(R.string.published_label), value = publishedAtLabel(ad.publishedAt))
        InfoRow(label = stringResource(R.string.views_label), value = viewsLabel(ad.views))

        if (ad.description.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.description_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = ad.description,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(20.dp))

        SellerCard(ad = ad)

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (ad.author.contact != null) {
                if (phoneVisible) {
                    Button(
                        onClick = { dialPhone(context, ad.author.contact) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(ad.author.contact)
                    }
                } else {
                    Button(
                        onClick = { phoneVisible = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.show_phone))
                    }
                }
            }
            OutlinedButton(
                onClick = {
                    shareAd(
                        context = context,
                        ad = ad,
                        currency = currency,
                        city = city,
                        chooserTitle = shareChooserTitle
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.share))
            }
        }

        if (ad.isLocal) {
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.edit))
                }
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.delete))
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// Swipeable gallery of the ad photos
@Composable
private fun PhotoGallery(
    urls: List<String>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (urls.isEmpty()) {
            Text(
                text = stringResource(R.string.photo_missing),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val pagerState = rememberPagerState(pageCount = { urls.size })

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = urls[page],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Surface(
                color = Color.Black.copy(alpha = 0.55f),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${urls.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// Seller name, rating and the phone action
@Composable
private fun SellerCard(ad: Ad) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.seller),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (ad.isLocal) stringResource(R.string.you) else ad.author.name,
                style = MaterialTheme.typography.titleMedium
            )
            if (ad.author.rating > 0.0) {
                Text(
                    text = stringResource(R.string.rating_format, ad.author.rating.toString()),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// Label above value, used for the ad attributes
@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// Opens the phone dialer with the seller number
private fun dialPhone(context: Context, phone: String) {
    val number = phone.filter { it.isDigit() || it == '+' }
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
    runCatching { context.startActivity(intent) }
}

// Opens the system share sheet with the ad text
private fun shareAd(
    context: Context,
    ad: Ad,
    currency: String,
    city: String,
    chooserTitle: String
) {
    val text = buildString {
        appendLine(ad.title)
        appendLine(ad.formattedPrice(currency))
        appendLine(city)
        append(ad.description)
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, chooserTitle))
}

@Preview(showBackground = true)
@Composable
private fun DetailContentPreview() {
    AdBoardAppTheme {
        DetailContent(
            uiState = DetailUiState(ad = previewAds.first(), isLoading = false),
            onBack = {},
            onRetry = {},
            onFavoriteClick = {},
            onEdit = {},
            onDeleteConfirmed = {}
        )
    }
}
