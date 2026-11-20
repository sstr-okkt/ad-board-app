package com.example.adboardapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.adboardapp.R

// Green button that opens the ad form
@Composable
internal fun AddAdButton(onClick: () -> Unit) {
    GreenActionButton(
        text = stringResource(R.string.add_ad),
        onClick = onClick,
        modifier = Modifier.padding(end = 8.dp)
    )
}
