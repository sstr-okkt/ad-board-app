package com.example.adboardapp.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.adboardapp.R
import com.example.adboardapp.model.AdStatus

// Small colored badge with the ad status
@Composable
internal fun StatusBadge(status: AdStatus) {
    val color = statusColor(status)
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = stringResource(statusRes(status)),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

internal fun statusColor(status: AdStatus): Color = when (status) {
    AdStatus.PUBLISHED -> Color(0xFF2E7D32)
    AdStatus.DRAFT -> Color(0xFF8D6E00)
    AdStatus.ARCHIVED -> Color(0xFF616161)
}

@StringRes
internal fun statusRes(status: AdStatus): Int = when (status) {
    AdStatus.PUBLISHED -> R.string.status_published
    AdStatus.DRAFT -> R.string.status_draft
    AdStatus.ARCHIVED -> R.string.status_archived
}
