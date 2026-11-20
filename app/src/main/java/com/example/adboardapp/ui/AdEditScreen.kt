package com.example.adboardapp.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.adboardapp.model.AdCondition
import com.example.adboardapp.model.adCategories
import com.example.adboardapp.model.adCities
import com.example.adboardapp.model.label
import com.example.adboardapp.ui.theme.AdBoardAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

// Picks photos and saves the ad
@Composable
fun AdEditScreen(
    viewModel: AdEditViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_PHOTOS)
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch {
                val urls = withContext(Dispatchers.IO) {
                    uris.mapNotNull { copyImage(context, it) }
                }
                urls.forEach(viewModel::onImageAdded)
            }
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    AdEditContent(
        state = state,
        onBack = onBack,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPriceChange = viewModel::onPriceChange,
        onNegotiableChange = viewModel::onNegotiableChange,
        onCategoryChange = viewModel::onCategoryChange,
        onCityChange = viewModel::onCityChange,
        onConditionChange = viewModel::onConditionChange,
        onPhoneChange = viewModel::onPhoneChange,
        onPickPhotos = {
            picker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onRemovePhoto = viewModel::onImageRemoved,
        onSaveDraft = { viewModel.save(publish = false) },
        onPublish = { viewModel.save(publish = true) }
    )
}

// Stateless form content
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdEditContent(
    state: AdEditUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onNegotiableChange: (Boolean) -> Unit,
    onCategoryChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onConditionChange: (AdCondition) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPickPhotos: () -> Unit,
    onRemovePhoto: (String) -> Unit,
    onSaveDraft: () -> Unit,
    onPublish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.isNew) "Нове оголошення" else "Редагування")
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.loadError != null -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.loadError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(24.dp)
                )
            }

            else -> Box(
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
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Фото (до $MAX_PHOTOS)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))

                    PhotosRow(
                        urls = state.imageUrls,
                        canAdd = state.canAddPhoto,
                        onPick = onPickPhotos,
                        onRemove = onRemovePhoto
                    )

                    if (state.imageError != null) {
                        Text(
                            text = state.imageError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    FormField(
                        value = state.title,
                        onValueChange = onTitleChange,
                        label = "Заголовок",
                        error = state.titleError
                    )

                    Spacer(Modifier.height(8.dp))

                    FormField(
                        value = state.description,
                        onValueChange = onDescriptionChange,
                        label = "Опис",
                        error = state.descriptionError,
                        minLines = 3
                    )

                    Spacer(Modifier.height(8.dp))

                    FormField(
                        value = state.price,
                        onValueChange = onPriceChange,
                        label = "Ціна, грн",
                        error = state.priceError,
                        keyboardType = KeyboardType.Number
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.isNegotiable,
                            onCheckedChange = onNegotiableChange
                        )
                        Text("Договірна")
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OptionDropdown(
                            label = "Категорія",
                            options = adCategories,
                            selected = state.category,
                            onSelect = onCategoryChange,
                            modifier = Modifier.weight(1f)
                        )
                        OptionDropdown(
                            label = "Місто",
                            options = adCities,
                            selected = state.city,
                            onSelect = onCityChange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Стан",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AdCondition.entries.forEach { condition ->
                            FilterChip(
                                selected = state.condition == condition,
                                onClick = { onConditionChange(condition) },
                                label = { Text(condition.label()) }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    FormField(
                        value = state.phone,
                        onValueChange = onPhoneChange,
                        label = "Контактний телефон",
                        error = state.phoneError,
                        keyboardType = KeyboardType.Phone
                    )

                    if (state.saveError != null) {
                        Text(
                            text = state.saveError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onSaveDraft,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Зберегти чернетку")
                        }
                        Button(
                            onClick = onPublish,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Опублікувати")
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

// Row of picked photos with the add tile
@Composable
private fun PhotosRow(
    urls: List<String>,
    canAdd: Boolean,
    onPick: () -> Unit,
    onRemove: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(urls) { url ->
            Box(modifier = Modifier.size(96.dp)) {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { onRemove(url) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "×",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        if (canAdd) {
            item {
                OutlinedButton(
                    onClick = onPick,
                    modifier = Modifier.size(96.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("+", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}

// Dropdown for category and city
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Text field with a validation message
@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = if (error != null) {
            { Text(error) }
        } else {
            null
        },
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth()
    )
}

// Copies the picked image into the app storage, so it survives restarts
private fun copyImage(context: Context, uri: Uri): String? = runCatching {
    val dir = File(context.filesDir, "images").apply { mkdirs() }
    val file = File(dir, "ad_${UUID.randomUUID()}.jpg")
    context.contentResolver.openInputStream(uri)?.use { input ->
        file.outputStream().use { output -> input.copyTo(output) }
    } ?: return@runCatching null
    Uri.fromFile(file).toString()
}.getOrNull()

@Preview(showBackground = true)
@Composable
private fun AdEditContentPreview() {
    AdBoardAppTheme {
        AdEditContent(
            state = AdEditUiState(),
            onBack = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onPriceChange = {},
            onNegotiableChange = {},
            onCategoryChange = {},
            onCityChange = {},
            onConditionChange = {},
            onPhoneChange = {},
            onPickPhotos = {},
            onRemovePhoto = {},
            onSaveDraft = {},
            onPublish = {}
        )
    }
}
