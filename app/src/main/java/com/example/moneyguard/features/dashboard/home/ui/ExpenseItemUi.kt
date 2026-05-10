package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.runtime.Immutable

/** Visual treatment for the leading icon on a transaction row. */
enum class ExpenseIconStyle {
    Entertainment,
    Transfer,
    Food,
}

@Immutable
data class ExpenseItemUi(
    val title: String,
    /** e.g. "Entertainment · 8:30 AM" */
    val metaLine: String,
    /** Negative displayed with − prefix in UI. */
    val amountRupees: Int,
    /** e.g. "Monthly", "UPI", "Card" */
    val paymentLabel: String,
    val iconStyle: ExpenseIconStyle,
)
