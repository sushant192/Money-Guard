package com.example.moneyguard.features.auth.signup.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.example.moneyguard.core.arch.UiState

@Immutable
data class SignUpUiState(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    @StringRes val fullNameError: Int?,
    @StringRes val emailError: Int?,
    @StringRes val passwordError: Int?,
    @StringRes val confirmPasswordError: Int?,
    val isLoading: Boolean
) : UiState {
    companion object {
        val Initial = SignUpUiState(
            fullName = "",
            email = "",
            password = "",
            confirmPassword = "",
            fullNameError = null,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            isLoading = false
        )
    }
}
