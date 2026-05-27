package com.example.moneyguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.moneyguard.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expense: ExpenseEntity): Long

    @Query("SELECT * FROM expenses ORDER BY createdAtEpochMs DESC")
    fun observeAllOrderedDesc(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ExpenseEntity?

    /** Same amount from a notification within [windowMs] of [createdAtEpochMs]. */
    @Query(
        """
        SELECT COUNT(*) > 0 FROM expenses
        WHERE sourceKey IS NOT NULL
        AND amountRupees = :amountRupees
        AND ABS(createdAtEpochMs - :createdAtEpochMs) <= :windowMs
        LIMIT 1
        """,
    )
    suspend fun existsNotificationExpenseNearTime(
        amountRupees: Int,
        createdAtEpochMs: Long,
        windowMs: Long,
    ): Boolean

    @Query(
        """
        SELECT COUNT(*) > 0 FROM expenses
        WHERE listenerNotificationKey = :listenerNotificationKey
        LIMIT 1
        """,
    )
    suspend fun existsByListenerNotificationKey(listenerNotificationKey: String): Boolean

    @Transaction
    suspend fun insertNotificationExpenseIfNew(
        expense: ExpenseEntity,
        nearTimeWindowMs: Long,
    ): Long {
        val listenerKey = expense.listenerNotificationKey
        if (listenerKey != null && existsByListenerNotificationKey(listenerKey)) {
            return -1L
        }
        if (
            existsNotificationExpenseNearTime(
                amountRupees = expense.amountRupees,
                createdAtEpochMs = expense.createdAtEpochMs,
                windowMs = nearTimeWindowMs,
            )
        ) {
            return -1L
        }
        return insert(expense)
    }

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        SELECT COALESCE(SUM(amountRupees), 0) FROM expenses
        WHERE createdAtEpochMs >= :dayStartMs AND createdAtEpochMs < :dayEndMs
        """,
    )
    suspend fun sumAmountBetween(dayStartMs: Long, dayEndMs: Long): Int

    @Query(
        """
        SELECT COUNT(*) FROM expenses
        WHERE createdAtEpochMs >= :dayStartMs AND createdAtEpochMs < :dayEndMs
        """,
    )
    suspend fun countBetween(dayStartMs: Long, dayEndMs: Long): Int
}
