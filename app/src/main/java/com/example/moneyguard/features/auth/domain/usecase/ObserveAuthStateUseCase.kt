package com.example.moneyguard.features.auth.domain.usecase

import com.example.moneyguard.features.auth.domain.model.AuthUser
import com.example.moneyguard.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveAuthStateUseCase(
    private val repository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthUser?> = repository.observeAuthState()
}
