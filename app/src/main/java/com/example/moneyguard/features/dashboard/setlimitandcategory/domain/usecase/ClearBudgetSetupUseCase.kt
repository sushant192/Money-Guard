package com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase

import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.repository.BudgetSetupRepository
import org.koin.core.annotation.Factory

@Factory
class ClearBudgetSetupUseCase(
    private val repository: BudgetSetupRepository,
) {
    suspend operator fun invoke() = repository.clearBudgetSetup()
}
