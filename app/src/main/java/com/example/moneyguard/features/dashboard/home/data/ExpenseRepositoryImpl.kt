package com.example.moneyguard.features.dashboard.home.data

import com.example.moneyguard.data.local.dao.ExpenseDao
import com.example.moneyguard.data.local.entity.ExpenseEntity
import com.example.moneyguard.features.dashboard.home.domain.repository.ExpenseRepository
import com.example.moneyguard.features.dashboard.home.ui.ExpenseIconStyle
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

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

    override suspend fun getSpentTodayRupees(nowEpochMs: Long): Int {
        val (start, end) = dayBounds(nowEpochMs)
        return dao.sumAmountBetween(start, end)
    }

    override suspend fun getTransactionCountToday(nowEpochMs: Long): Int {
        val (start, end) = dayBounds(nowEpochMs)
        return dao.countBetween(start, end)
    }

    private fun dayBounds(nowEpochMs: Long): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply { timeInMillis = nowEpochMs }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        return start to start + DAY_MS
    }

    companion object {
        private const val DAY_MS = 24L * 60L * 60L * 1000L
        /** Catches repeat bank/UPI shade posts for one payment with different notification keys. */
        private const val NOTIFICATION_NEAR_TIME_WINDOW_MS = 120_000L
    }
}
