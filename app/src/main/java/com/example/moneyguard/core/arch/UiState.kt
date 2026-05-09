package com.example.moneyguard.core.arch

import androidx.compose.runtime.Immutable

/**
 * Marker interface for any state that drives a Compose screen.
 * Implementations should be [Immutable] so Compose can skip recomposition
 * when the same instance is reused.
 */
@Immutable
interface UiState
