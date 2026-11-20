package com.example.adboardapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.adboardapp.R
import com.example.adboardapp.ui.theme.ButtonGreen
import com.example.adboardapp.ui.theme.ButtonGreenDark

// Green button that opens the ad form
@Composable
internal fun AddAdButton(onClick: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val green = if (isDark) ButtonGreenDark else ButtonGreen

    ActionButton(
        text = stringResource(R.string.add_ad),
        onClick = onClick,
        containerColor = green,
        modifier = Modifier.padding(end = 8.dp)
    )
}
