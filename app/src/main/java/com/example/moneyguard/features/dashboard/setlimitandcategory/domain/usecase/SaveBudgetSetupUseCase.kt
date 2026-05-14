package com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase

import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.repository.BudgetSetupRepository
import org.koin.core.annotation.Factory

@Factory
class SaveBudgetSetupUseCase(
    private val repository: BudgetSetupRepository,
) {
    suspend operator fun invoke(
        limit: Int,
        selectedCategories: Set<String>,
    ) {
        repository.saveBudgetSetup(
            limit = limit,
            selectedCategories = selectedCategories,
        )
    }
}
