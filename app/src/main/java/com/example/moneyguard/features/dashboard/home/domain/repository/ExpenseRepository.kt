package com.example.moneyguard.features.dashboard.home.domain.repository

import com.example.moneyguard.data.local.entity.ExpenseEntity
import com.example.moneyguard.features.dashboard.home.ui.ExpenseIconStyle
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeAllExpenses(): Flow<List<ExpenseEntity>>

    suspend fun insertManualExpense(
        title: String,
        amountRupees: Int,
        note: String,
        category: ExpenseIconStyle,
    )
}
