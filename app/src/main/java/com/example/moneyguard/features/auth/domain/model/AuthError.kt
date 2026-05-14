package com.example.moneyguard.features.auth.domain.model

/**
 * Domain-level auth failure modes. The data layer maps backend-specific
 * exceptions (FirebaseAuthException variants, Credential Manager errors, …)
 * into one of these so the UI never has to know about Firebase.
 */
sealed class AuthError {
    data object InvalidCredentials : AuthError()
    data object EmailAlreadyInUse : AuthError()
    data object WeakPassword : AuthError()
    data object UserNotFound : AuthError()
    data object Network : AuthError()
    /** User dismissed the Google sign-in sheet — treat as silent cancel. */
    data object GoogleCancelled : AuthError()
    data class Unknown(val message: String?) : AuthError()
}

/**
 * Throwable wrapper used inside [Result.failure]. Lets us flow [AuthError]
 * through Kotlin's [Result] without losing type info, while still being a
 * valid `Throwable`.
 */
class AuthException(val error: AuthError) : Exception(error.toString())
