package com.example.moneyguard.features.auth.splash.ui

import androidx.compose.runtime.Immutable
import com.example.moneyguard.core.arch.UiState

@Immutable
data class SplashUiState(
    val isLoading: Boolean,
) : UiState {
    companion object {
        val Initial = SplashUiState(isLoading = true)
    }
}
