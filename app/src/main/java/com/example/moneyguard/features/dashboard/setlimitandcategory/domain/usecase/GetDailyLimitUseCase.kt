package com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase

import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.repository.BudgetSetupRepository
import org.koin.core.annotation.Factory

@Factory
class GetDailyLimitUseCase(
    private val repository: BudgetSetupRepository,
) {
    suspend operator fun invoke(): Int? = repository.getDailyLimit()
}
