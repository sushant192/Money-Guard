package com.example.moneyguard.features.auth.domain.model

/**
 * Domain representation of an authenticated user. Intentionally minimal —
 * anything richer (preferences, profile pic) belongs in a separate `User`
 * profile model fed by Room/DataStore.
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
)
