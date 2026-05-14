package com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase

import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.repository.BudgetSetupRepository
import org.koin.core.annotation.Factory

@Factory
class HasCompletedBudgetSetupUseCase(
    private val repository: BudgetSetupRepository,
) {
    suspend operator fun invoke(): Boolean = repository.hasCompletedBudgetSetup()
}
