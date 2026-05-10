package com.example.moneyguard.features.dashboard.home.ui

import androidx.annotation.StringRes
import com.example.moneyguard.R

/** Pre-set "warn me at this much of my daily limit" choices on the Limits tab. */
enum class AlertThreshold(@StringRes val labelRes: Int) {
    Seventy(R.string.limits_threshold_70),
    Eighty(R.string.limits_threshold_80),
    Ninety(R.string.limits_threshold_90),
    Custom(R.string.limits_threshold_custom),
}
