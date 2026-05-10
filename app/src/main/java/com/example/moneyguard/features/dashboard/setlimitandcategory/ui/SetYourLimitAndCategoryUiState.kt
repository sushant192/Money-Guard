package com.example.moneyguard.features.dashboard.setlimitandcategory.ui

import androidx.compose.runtime.Immutable
import com.example.moneyguard.core.arch.UiState

/**
 * Two-step onboarding flow held inside a single screen:
 *
 * - [LIMIT] — pick the daily spend limit (slider).
 * - [CATEGORIES] — pick spend categories.
 *
 * Tapping the primary button advances [LIMIT] → [CATEGORIES], and on
 * [CATEGORIES] it completes the flow.
 */
enum class SetLimitStep { LIMIT, CATEGORIES }

@Immutable
data class SetYourLimitAndCategoryUiState(
    val step: SetLimitStep,
    val limit: Int,
    val selectedCategories: Set<Category>,
    val isLoading: Boolean
) : UiState {
    companion object {
        const val MIN_LIMIT = 500
        const val MAX_LIMIT = 10_000
        const val LIMIT_STEP = 100

        val Initial = SetYourLimitAndCategoryUiState(
            step = SetLimitStep.LIMIT,
            limit = MIN_LIMIT,
            selectedCategories = emptySet(),
            isLoading = false
        )
    }
}
