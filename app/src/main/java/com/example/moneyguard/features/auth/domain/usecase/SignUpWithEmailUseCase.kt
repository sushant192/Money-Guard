package com.example.moneyguard.features.auth.domain.usecase

import com.example.moneyguard.features.auth.domain.model.AuthUser
import com.example.moneyguard.features.auth.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class SignUpWithEmailUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String,
    ): Result<AuthUser> = repository.signUpWithEmail(email.trim(), password, displayName.trim())
}
