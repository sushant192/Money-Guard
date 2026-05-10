package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.runtime.Immutable

/** A single transaction row inside the History tab. Trimmed-down vs. the
 *  Home expense card — only the time (no category prefix) is shown beneath
 *  the title, and there's no payment label.
 */
@Immutable
data class HistoryItemUi(
    val title: String,
    val time: String,
    /** Displayed with `−` prefix in the UI. */
    val amountRupees: Int,
    val iconStyle: ExpenseIconStyle,
)

/** A grouping of [HistoryItemUi] under a date bucket like "Today",
 *  "Yesterday", "12 May", etc. Bucket title is rendered in uppercase.
 */
@Immutable
data class HistoryGroup(
    val title: String,
    val items: List<HistoryItemUi>,
)
