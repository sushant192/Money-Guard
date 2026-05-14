package com.example.moneyguard.features.dashboard.setlimitandcategory.domain.repository

interface BudgetSetupRepository {
    suspend fun saveBudgetSetup(
        limit: Int,
        selectedCategories: Set<String>,
    )

    suspend fun hasCompletedBudgetSetup(): Boolean

    suspend fun clearBudgetSetup()
}
