package com.example.adboardapp.ui

import androidx.annotation.StringRes
import com.example.adboardapp.R
import com.example.adboardapp.model.ErrorReason

// Text of a failed data layer operation, resolved in the current language
@StringRes
fun errorReasonRes(reason: ErrorReason): Int = when (reason) {
    ErrorReason.SYNC -> R.string.error_sync
    ErrorReason.NOT_FOUND -> R.string.ad_not_found
    ErrorReason.SAVE -> R.string.error_save
    ErrorReason.DELETE -> R.string.error_delete
    ErrorReason.STATUS -> R.string.error_status
}
