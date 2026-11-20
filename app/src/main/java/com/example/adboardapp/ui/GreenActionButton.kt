package com.example.adboardapp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.example.adboardapp.ui.theme.ButtonGreen
import com.example.adboardapp.ui.theme.ButtonGreenDark

// Green pill button used for the main actions
@Composable
internal fun GreenActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val green = if (isDark) ButtonGreenDark else ButtonGreen

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = green,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        Text(text)
    }
}
