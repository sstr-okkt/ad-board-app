package com.example.adboardapp.ui

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adboardapp.model.Ad
import com.example.adboardapp.ui.theme.AdBoardAppTheme

// Connects the ViewModel with the stateless details content
@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailContent(uiState = uiState, onBack = onBack)
}

// Renders the details screen from a ready UiState
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    uiState: DetailUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Оголошення") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        val ad = uiState.ad
        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            ad == null -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Оголошення не знайдено",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                DetailBody(ad = ad)
            }
        }
    }
}

// Main content of the details screen
@Composable
private fun DetailBody(ad: Ad) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .widthIn(max = 840.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = ad.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            StatusBadge(ad.status)
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = ad.formattedPrice(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(16.dp))

        InfoRow(label = "Категорія", value = ad.category)
        InfoRow(label = "Місто", value = ad.city)
        InfoRow(label = "Переглядів", value = ad.views.toString())
        InfoRow(
            label = "Опубліковано",
            value = ad.publishedAt ?: "ще не опубліковано"
        )

        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        Text(
            text = "Автор",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = ad.author.name,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Рейтинг: ${ad.author.rating} із 5.0",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Контакт: ${ad.author.contact ?: "не вказано"}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showToast(context, "Написати автору: ${ad.author.name}") }) {
                Text("Написати автору")
            }
            if (ad.author.contact != null) {
                OutlinedButton(onClick = { showToast(context, "Телефон: ${ad.author.contact}") }) {
                    Text("Подзвонити")
                }
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

@Preview(showBackground = true)
@Composable
private fun DetailContentPreview() {
    AdBoardAppTheme {
        DetailContent(
            uiState = DetailUiState(ad = previewAds.first(), isLoading = false),
            onBack = {}
        )
    }
}
