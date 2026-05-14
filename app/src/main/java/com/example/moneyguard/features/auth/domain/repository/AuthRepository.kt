package com.example.moneyguard.features.auth.domain.repository

import com.example.moneyguard.features.auth.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * Sole gateway between the UI/domain layers and whichever backend powers auth.
 * Implementations map backend errors into [com.example.moneyguard.features.auth.domain.model.AuthError]
 * (carried via [com.example.moneyguard.features.auth.domain.model.AuthException] inside [Result.failure]).
 */
interface AuthRepository {

    /** Synchronous current snapshot — handy for routing decisions on cold start. */
    val currentUser: AuthUser?

    /** Hot stream of the current user (or null). Emits on every sign-in/out. */
    fun observeAuthState(): Flow<AuthUser?>

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String,
    ): Result<AuthUser>

    suspend fun loginWithEmail(
        email: String,
        password: String,
    ): Result<AuthUser>

    /** Caller obtains the ID token via Credential Manager and passes it here. */
    suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthUser>

    suspend fun logout()
}
