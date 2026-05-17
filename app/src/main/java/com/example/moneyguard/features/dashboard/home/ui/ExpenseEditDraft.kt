package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.runtime.Immutable

@Immutable
data class ExpenseEditDraft(
    val expenseId: Long,
    val amountRupees: Int,
    val title: String,
    val note: String,
    val category: ExpenseIconStyle,
)
