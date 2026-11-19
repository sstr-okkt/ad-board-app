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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adboardapp.model.Ad
import com.example.adboardapp.model.AdStatus
import com.example.adboardapp.model.Author
import com.example.adboardapp.ui.theme.AdBoardAppTheme

// Test data for now, no database or network yet
private val sampleAds = listOf(
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

// First screen: search field plus scrollable list of ads
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdListScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // Query lives here so the list filters while typing
    var query by rememberSaveable { mutableStateOf("") }

    val filtered = sampleAds.filter { ad ->
        ad.title.contains(query.trim(), ignoreCase = true)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Дошка оголошень") },
                actions = {
                    TextButton(onClick = { showToast(context, "Створити нове оголошення") }) {
                        Text("Додати")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showToast(context, "Створити нове оголошення") }) {
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
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Пошук за назвою") },
                    placeholder = { Text("Наприклад, ноутбук") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Text(
                    text = "Знайдено ${filtered.size} з ${sampleAds.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(8.dp))

                if (filtered.isEmpty()) {
                    Box(
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
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = filtered, key = { it.id }) { ad ->
                            AdCard(
                                ad = ad,
                                onClick = { showToast(context, "Відкрито: ${ad.title}") },
                                onPublish = { showToast(context, "Оголошення «${ad.title}» опубліковано") }
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
private fun AdListScreenPreview() {
    AdBoardAppTheme {
        AdListScreen()
    }
}
