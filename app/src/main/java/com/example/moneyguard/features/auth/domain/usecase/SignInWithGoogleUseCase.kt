package com.example.moneyguard.features.auth.domain.usecase

import com.example.moneyguard.features.auth.domain.model.AuthUser
import com.example.moneyguard.features.auth.domain.repository.AuthRepository
import org.koin.core.annotation.Factory

@Factory
class SignInWithGoogleUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(idToken: String): Result<AuthUser> =
        repository.signInWithGoogleIdToken(idToken)
}
