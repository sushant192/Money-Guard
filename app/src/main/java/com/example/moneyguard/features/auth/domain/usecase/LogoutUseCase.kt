package com.example.moneyguard.features.auth.domain.usecase

import com.example.moneyguard.features.auth.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class LogoutUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke() = repository.logout()
}
