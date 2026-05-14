package com.example.moneyguard.features.auth.data

import com.example.moneyguard.features.auth.domain.model.AuthException
import com.example.moneyguard.features.auth.domain.model.AuthUser
import com.example.moneyguard.features.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * [AuthRepository] backed by [FirebaseAuth]. Firebase already persists the
 * signed-in user across app launches, so we simply expose [currentUser] for
 * cold-start routing and a [callbackFlow] of auth-state changes for any
 * screen that wants to react live.
 */
class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override val currentUser: AuthUser?
        get() = auth.currentUser?.toAuthUser()

    override fun observeAuthState(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fb ->
            trySend(fb.currentUser?.toAuthUser())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String,
    ): Result<AuthUser> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: error("Firebase returned a null user after sign-up")
        // Persist the display name on the Firebase user so it survives cold
        // starts and is exposed via FirebaseUser.displayName everywhere.
        user.updateProfile(
            userProfileChangeRequest { this.displayName = displayName }
        ).await()
        user.toAuthUser(overrideDisplayName = displayName)
    }.recoverFailure()

    override suspend fun loginWithEmail(
        email: String,
        password: String,
    ): Result<AuthUser> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: error("Firebase returned a null user after sign-in")
        user.toAuthUser()
    }.recoverFailure()

    override suspend fun signInWithGoogleIdToken(
        idToken: String,
    ): Result<AuthUser> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: error("Firebase returned a null user after Google sign-in")
        user.toAuthUser()
    }.recoverFailure()

    override suspend fun logout() {
        auth.signOut()
    }
}

/** Map a [FirebaseUser] onto our domain [AuthUser]. */
private fun FirebaseUser.toAuthUser(overrideDisplayName: String? = null) = AuthUser(
    uid = uid,
    email = email,
    displayName = overrideDisplayName ?: displayName,
)

/**
 * Convert raw Throwables on the failure branch of [Result] into our typed
 * [AuthException] so the UI never sees Firebase types directly.
 */
private fun Result<AuthUser>.recoverFailure(): Result<AuthUser> = fold(
    onSuccess = { Result.success(it) },
    onFailure = { Result.failure(AuthException(it.toAuthError())) },
)
