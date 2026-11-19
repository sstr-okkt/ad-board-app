package com.example.adboardapp.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.ui.theme.AdBoardAppTheme

// Connects the ViewModel with the stateless screen content
@Composable
fun AdListScreen(viewModel: ListViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AdListContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onAdClick = { ad -> showToast(context, "Відкрито: ${ad.title}") },
        onAddClick = { showToast(context, "Створити нове оголошення") },
        onPublishClick = { ad -> showToast(context, "Оголошення «${ad.title}» опубліковано") }
    )
}

// Renders the screen from a ready UiState, no ViewModel here
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdListContent(
    uiState: ListUiState,
    onQueryChange: (String) -> Unit,
    onAdClick: (Ad) -> Unit,
    onAddClick: () -> Unit,
    onPublishClick: (Ad) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    label = { Text("Пошук за назвою") },
                    placeholder = { Text("Наприклад, ноутбук") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (!uiState.isLoading) {
                    Text(
                        text = "Знайдено ${uiState.visibleAds.size} з ${uiState.ads.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(8.dp))
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

                    uiState.visibleAds.isEmpty() -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нічого не знайдено",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = uiState.visibleAds, key = { it.id }) { ad ->
                            AdCard(
                                ad = ad,
                                onClick = { onAdClick(ad) },
                                onPublish = { onPublishClick(ad) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Single ad card
@Composable
private fun AdCard(
    ad: Ad,
    onClick: () -> Unit,
    onPublish: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = ad.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(ad.status)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = ad.formattedPrice(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${ad.category} · ${ad.city}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = buildString {
                    append("${ad.views} переглядів")
                    append(" · автор: ${ad.author.name}")
                    ad.publishedAt?.let { append(" · $it") }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onClick) {
                    Text("Детальніше")
                }
                if (ad.status == AdStatus.DRAFT) {
                    OutlinedButton(onClick = onPublish) {
                        Text("Опублікувати")
                    }
                }
            }
        }
    }
}

// Small colored badge with the ad status
@Composable
private fun StatusBadge(status: AdStatus) {
    val color = statusColor(status)
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = statusLabel(status),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun statusColor(status: AdStatus): Color = when (status) {
    AdStatus.PUBLISHED -> Color(0xFF2E7D32)
    AdStatus.DRAFT -> Color(0xFF8D6E00)
    AdStatus.ARCHIVED -> Color(0xFF616161)
}

private fun statusLabel(status: AdStatus): String = when (status) {
    AdStatus.PUBLISHED -> "Опубліковано"
    AdStatus.DRAFT -> "Чернетка"
    AdStatus.ARCHIVED -> "Архів"
}

private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
private fun AdListContentPreview() {
    AdBoardAppTheme {
        AdListContent(
            uiState = ListUiState(ads = sampleAds),
            onQueryChange = {},
            onAdClick = {},
            onAddClick = {},
            onPublishClick = {}
        )
    }
}
