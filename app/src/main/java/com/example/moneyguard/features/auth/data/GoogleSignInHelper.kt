package com.example.moneyguard.features.auth.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.moneyguard.features.auth.domain.model.AuthError
import com.example.moneyguard.features.auth.domain.model.AuthException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

/**
 * Thin wrapper around AndroidX [CredentialManager] that returns a Google ID
 * token suitable for [com.google.firebase.auth.GoogleAuthProvider.getCredential].
 *
 * We call this from [com.example.moneyguard.features.auth.login.ui.LoginViewModel]
 * with the **activity** context (passed in from the screen) — Credential
 * Manager needs an Activity to host its bottom sheet.
 */
class GoogleSignInHelper(
    private val webClientId: String,
) {

    /**
     * Triggers the Google account picker / one-tap sheet and returns the ID
     * token on success. The first attempt is restricted to accounts already
     * authorised for this app (faster, no chrome on returning users); if that
     * comes back empty we retry with the broader filter so a brand-new user
     * can pick from any Google account on the device.
     */
    suspend fun fetchIdToken(activityContext: Context): Result<String> = runCatching {
        if (webClientId.isBlank()) {
            throw AuthException(
                AuthError.Unknown("Missing WEB_CLIENT_ID — add it to local.properties"),
            )
        }
        val manager = CredentialManager.create(activityContext)

        val idToken = tryGetIdToken(manager, activityContext, filterAuthorized = true)
            ?: tryGetIdToken(manager, activityContext, filterAuthorized = false)
            ?: throw AuthException(AuthError.GoogleCancelled)

        idToken
    }.recoverCatching { e ->
        when (e) {
            is AuthException -> throw e
            is GetCredentialCancellationException -> throw AuthException(AuthError.GoogleCancelled)
            is NoCredentialException -> throw AuthException(AuthError.GoogleCancelled)
            is GetCredentialException -> throw AuthException(AuthError.Unknown(e.message))
            is GoogleIdTokenParsingException -> throw AuthException(AuthError.Unknown(e.message))
            else -> throw AuthException(AuthError.Unknown(e.message))
        }
    }

    private suspend fun tryGetIdToken(
        manager: CredentialManager,
        activityContext: Context,
        filterAuthorized: Boolean,
    ): String? {
        val option = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(filterAuthorized)
            .setAutoSelectEnabled(filterAuthorized)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val response = try {
            manager.getCredential(activityContext, request)
        } catch (e: NoCredentialException) {
            // No matching credentials at this filter level — let the caller
            // fall back to the broader request.
            if (filterAuthorized) return null else throw e
        }

        val credential = response.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return GoogleIdTokenCredential.createFrom(credential.data).idToken
        }
        return null
    }
}
