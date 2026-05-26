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
        listenerNotificationKey: String,
        title: String,
        amountRupees: Int,
        note: String,
        category: ExpenseIconStyle,
        paymentSource: String,
        createdAtEpochMs: Long,
    ): Boolean {
        val rowId =
            dao.insertNotificationExpenseIfNew(
                expense =
                    ExpenseEntity(
                        title = title,
                        amountRupees = amountRupees,
                        note = note,
                        category = category.name,
                        paymentSource = paymentSource,
                        createdAtEpochMs = createdAtEpochMs,
                        sourceKey = sourceKey,
                        listenerNotificationKey = listenerNotificationKey,
                    ),
                nearTimeWindowMs = NOTIFICATION_NEAR_TIME_WINDOW_MS,
            )
        return rowId != -1L
    }

    override suspend fun updateExpense(
        id: Long,
        title: String,
        amountRupees: Int,
        note: String,
        category: ExpenseIconStyle,
    ) {
        val existing = dao.getById(id) ?: return
        dao.update(
            existing.copy(
                title = title,
                amountRupees = amountRupees,
                note = note,
                category = category.name,
            ),
        )
    }

    override suspend fun deleteExpense(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun getExpenseById(id: Long): ExpenseEntity? = dao.getById(id)

    companion object {
        /** Catches repeat bank/UPI shade posts for one payment with different notification keys. */
        private const val NOTIFICATION_NEAR_TIME_WINDOW_MS = 120_000L
    }
}
