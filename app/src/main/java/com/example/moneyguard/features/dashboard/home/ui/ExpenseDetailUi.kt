package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ExpenseDetailUi(
    val id: Long,
    val title: String,
    /** e.g. "Food · 1:45 PM" */
    val metaLine: String,
    val amountRupees: Int,
    val paymentLabel: String,
    val iconStyle: ExpenseIconStyle,
    val categoryLabel: String,
    val categoryAccent: Color,
    val dateLabel: String,
    val timeLabel: String,
    val note: String,
    val useUpiPaymentIcon: Boolean,
)
