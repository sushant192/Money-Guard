package com.example.moneyguard.features.auth.domain.usecase

import com.example.moneyguard.features.auth.domain.model.AuthUser
import com.example.moneyguard.features.auth.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class LoginWithEmailUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<AuthUser> = repository.loginWithEmail(email.trim(), password)
}
