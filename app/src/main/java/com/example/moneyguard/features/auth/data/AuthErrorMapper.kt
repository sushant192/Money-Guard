package com.example.moneyguard.features.auth.data

import com.example.moneyguard.features.auth.domain.model.AuthError
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

/**
 * Maps Firebase / network exceptions to domain [AuthError]s. Centralised so
 * each repo method can do `try { … } catch (e: Throwable) { Result.failure(AuthException(toAuthError(e))) }`.
 */
internal fun Throwable.toAuthError(): AuthError = when (this) {
    is FirebaseAuthInvalidCredentialsException,
    is FirebaseAuthInvalidUserException -> AuthError.InvalidCredentials
    is FirebaseAuthUserCollisionException -> AuthError.EmailAlreadyInUse
    is FirebaseAuthWeakPasswordException -> AuthError.WeakPassword
    is FirebaseNetworkException -> AuthError.Network
    else -> AuthError.Unknown(message)
}
