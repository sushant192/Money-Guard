package com.example.moneyguard.features.dashboard.home.data

import com.example.moneyguard.data.local.dao.ExpenseDao
import com.example.moneyguard.data.local.entity.ExpenseEntity
import com.example.moneyguard.features.dashboard.home.domain.repository.ExpenseRepository
import com.example.moneyguard.features.dashboard.home.ui.ExpenseIconStyle
import kotlinx.coroutines.flow.Flow

class ExpenseRepositoryImpl(
    private val dao: ExpenseDao,
) : ExpenseRepository {

    override fun observeAllExpenses(): Flow<List<ExpenseEntity>> =
        dao.observeAllOrderedDesc()

    override suspend fun insertManualExpense(
        title: String,
        amountRupees: Int,
        note: String,
        category: ExpenseIconStyle,
    ) {
        dao.insert(
            ExpenseEntity(
                title = title,
                amountRupees = amountRupees,
                note = note,
                category = category.name,
                paymentSource = "Manual",
                createdAtEpochMs = System.currentTimeMillis(),
                sourceKey = null,
            ),
        )
    }

    override suspend fun insertNotificationExpense(
        sourceKey: String,
        title: String,
        amountRupees: Int,
        note: String,
        category: ExpenseIconStyle,
        paymentSource: String,
        createdAtEpochMs: Long,
    ): Boolean {
        val rowId =
            dao.insert(
                ExpenseEntity(
                    title = title,
                    amountRupees = amountRupees,
                    note = note,
                    category = category.name,
                    paymentSource = paymentSource,
                    createdAtEpochMs = createdAtEpochMs,
                    sourceKey = sourceKey,
                ),
            )
        return rowId != -1L
    }
}
