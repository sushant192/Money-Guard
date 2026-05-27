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

    /**
     * Persists a notification-derived debit. Returns false if [sourceKey] was
     * already stored (duplicate notification).
     */
    suspend fun insertNotificationExpense(
        sourceKey: String,
        listenerNotificationKey: String,
        title: String,
        amountRupees: Int,
        note: String,
        category: ExpenseIconStyle,
        paymentSource: String,
        createdAtEpochMs: Long,
    ): Boolean

    suspend fun updateExpense(
        id: Long,
        title: String,
        amountRupees: Int,
        note: String,
        category: ExpenseIconStyle,
    )

    suspend fun deleteExpense(id: Long)

    suspend fun getExpenseById(id: Long): ExpenseEntity?

    suspend fun getSpentTodayRupees(nowEpochMs: Long = System.currentTimeMillis()): Int

    suspend fun getTransactionCountToday(nowEpochMs: Long = System.currentTimeMillis()): Int
}
