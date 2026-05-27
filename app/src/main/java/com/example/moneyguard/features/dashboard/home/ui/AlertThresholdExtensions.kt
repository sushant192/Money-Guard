package com.example.moneyguard.features.dashboard.home.ui

import com.example.moneyguard.features.dashboard.home.domain.MAX_ALERT_THRESHOLD_PERCENT
import com.example.moneyguard.features.dashboard.home.domain.MIN_ALERT_THRESHOLD_PERCENT

/** Maps persisted percent to the Limits tab chip selection. */
fun Int.toAlertThresholdSelection(): AlertThreshold =
    when (this) {
        70 -> AlertThreshold.Seventy
        80 -> AlertThreshold.Eighty
        90 -> AlertThreshold.Ninety
        else -> AlertThreshold.Custom
    }

/** Resolves the effective warning percent for presets vs custom. */
fun AlertThreshold.resolvePercent(storedPercent: Int): Int =
    when (this) {
        AlertThreshold.Seventy -> 70
        AlertThreshold.Eighty -> 80
        AlertThreshold.Ninety -> 90
        AlertThreshold.Custom ->
            storedPercent.coerceIn(MIN_ALERT_THRESHOLD_PERCENT, MAX_ALERT_THRESHOLD_PERCENT)
    }
