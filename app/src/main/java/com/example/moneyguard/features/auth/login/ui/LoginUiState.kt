package com.example.moneyguard.features.auth.login.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.example.moneyguard.core.arch.UiState

@Immutable
data class LoginUiState(
    val email: String,
    val password: String,
    @StringRes val emailError: Int?,
    @StringRes val passwordError: Int?,
    val isLoading: Boolean
) : UiState {
    companion object {
        val Initial = LoginUiState(
            email = "",
            password = "",
            emailError = null,
            passwordError = null,
            isLoading = false
        )
    }
}
