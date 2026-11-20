package com.example.adboardapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adboardapp.model.ThemeMode
import com.example.adboardapp.ui.theme.AdBoardAppTheme
import org.koin.androidx.compose.koinViewModel

// App settings, currently the theme choice
@Composable
fun SettingsScreen(
    viewModel: ThemeViewModel = koinViewModel()
) {
    val mode by viewModel.mode.collectAsStateWithLifecycle()

    SettingsContent(
        mode = mode,
        onModeChange = viewModel::setMode
    )
}

// Stateless content of the settings screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    mode: ThemeMode,
    onModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Налаштування") })
        }
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "Оформлення",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ThemeMode.entries.forEachIndexed { index, option ->
                            ThemeOption(
                                label = option.label,
                                description = description(option),
                                selected = mode == option,
                                onClick = { onModeChange(option) }
                            )
                            if (index != ThemeMode.entries.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 56.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// One row of the theme radio group
@Composable
private fun ThemeOption(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun description(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> "Як у системі"
    ThemeMode.LIGHT -> "Світло-сірі кольори"
    ThemeMode.DARK -> "Темні кольори"
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    AdBoardAppTheme {
        SettingsContent(
            mode = ThemeMode.SYSTEM,
            onModeChange = {}
        )
    }
}
