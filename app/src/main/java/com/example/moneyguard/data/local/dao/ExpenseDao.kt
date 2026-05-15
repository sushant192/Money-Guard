package com.example.moneyguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moneyguard.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: ExpenseEntity): Long

    @Query("SELECT * FROM expenses ORDER BY createdAtEpochMs DESC")
    fun observeAllOrderedDesc(): Flow<List<ExpenseEntity>>
}
