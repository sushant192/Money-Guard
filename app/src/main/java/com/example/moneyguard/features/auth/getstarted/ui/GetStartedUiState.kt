package com.example.moneyguard.features.auth.getstarted.ui

import androidx.compose.runtime.Immutable
import com.example.moneyguard.core.arch.UiState

@Immutable
data class GetStartedUiState(
    val isLoading: Boolean
) : UiState {
    companion object {
        val Initial = GetStartedUiState(
            isLoading = false
        )
    }
}
