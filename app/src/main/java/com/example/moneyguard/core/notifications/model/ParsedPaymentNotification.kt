package com.example.moneyguard.core.notifications.model

import com.example.moneyguard.features.dashboard.home.ui.ExpenseIconStyle

/**
 * A debit payment parsed from a single notification's text.
 */
data class ParsedPaymentNotification(
    val amountRupees: Int,
    val title: String,
    val note: String,
    val category: ExpenseIconStyle,
    val paymentSource: String,
    /** UPI / bank reference when present — used to dedupe repeat notifications. */
    val transactionRef: String? = null,
)
