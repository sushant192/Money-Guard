package com.example.moneyguard.features.dashboard.setlimitandcategory.data

import com.example.moneyguard.data.datastore.MoneyGuardPreferenceDataStore
import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.repository.BudgetSetupRepository

class BudgetSetupRepositoryImpl(
    private val preferenceDataStore: MoneyGuardPreferenceDataStore,
) : BudgetSetupRepository {

    override suspend fun saveBudgetSetup(
        limit: Int,
        selectedCategories: Set<String>,
    ) {
        preferenceDataStore.saveBudgetSetup(
            limit = limit,
            selectedCategories = selectedCategories,
        )
    }

    override suspend fun hasCompletedBudgetSetup(): Boolean =
        preferenceDataStore.hasCompletedBudgetSetup()

    override suspend fun clearBudgetSetup() {
        preferenceDataStore.clearBudgetSetup()
    }
}
