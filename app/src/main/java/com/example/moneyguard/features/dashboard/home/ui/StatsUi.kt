package com.example.moneyguard.features.dashboard.home.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * One row of the "Spending by category" list on the Stats tab.
 *
 * The progress bar fraction is computed at render time as
 * `amountRupees / maxAmountRupees` so we don't have to keep a precomputed
 * fraction in state.
 */
@Immutable
data class CategorySpendUi(
    @StringRes val nameRes: Int,
    val amountRupees: Int,
    val color: Color,
)

/** Single bar in the "Daily this week" mini-bar-chart on the Stats tab. */
@Immutable
data class DayBarUi(
    val label: String,
    /** 0f..1f — height of the bar relative to the chart area. */
    val heightFraction: Float,
    val kind: BarKind,
)

enum class BarKind { Past, Today, Future }
