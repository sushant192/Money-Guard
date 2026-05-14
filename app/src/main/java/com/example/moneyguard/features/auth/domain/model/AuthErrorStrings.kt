package com.example.moneyguard.features.auth.domain.model

import androidx.annotation.StringRes
import com.example.moneyguard.R

/**
 * UI-ready copy for an [AuthError]: a bold title line and a softer body line.
 * Toast composables consume this; ViewModels emit it on their error channel.
 */
data class AuthErrorMessage(
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int,
)

fun AuthError.toMessage(): AuthErrorMessage = when (this) {
    AuthError.InvalidCredentials -> AuthErrorMessage(
        titleRes = R.string.auth_error_invalid_credentials_title,
        messageRes = R.string.auth_error_invalid_credentials_message,
    )
    AuthError.EmailAlreadyInUse -> AuthErrorMessage(
        titleRes = R.string.auth_error_email_in_use_title,
        messageRes = R.string.auth_error_email_in_use_message,
    )
    AuthError.WeakPassword -> AuthErrorMessage(
        titleRes = R.string.auth_error_weak_password_title,
        messageRes = R.string.auth_error_weak_password_message,
    )
    AuthError.UserNotFound -> AuthErrorMessage(
        titleRes = R.string.auth_error_user_not_found_title,
        messageRes = R.string.auth_error_user_not_found_message,
    )
    AuthError.Network -> AuthErrorMessage(
        titleRes = R.string.auth_error_network_title,
        messageRes = R.string.auth_error_network_message,
    )
    AuthError.GoogleCancelled,
    is AuthError.Unknown -> AuthErrorMessage(
        titleRes = R.string.auth_error_unknown_title,
        messageRes = R.string.auth_error_unknown_message,
    )
}

/** Default fallback for unknown failures. */
fun unknownAuthErrorMessage() = AuthErrorMessage(
    titleRes = R.string.auth_error_unknown_title,
    messageRes = R.string.auth_error_unknown_message,
)
