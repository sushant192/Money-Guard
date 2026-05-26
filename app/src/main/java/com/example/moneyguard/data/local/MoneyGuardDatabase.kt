package com.example.moneyguard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moneyguard.data.local.dao.ExpenseDao
import com.example.moneyguard.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class MoneyGuardDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
}
