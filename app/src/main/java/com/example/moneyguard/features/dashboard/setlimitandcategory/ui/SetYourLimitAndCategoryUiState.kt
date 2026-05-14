package com.example.moneyguard.features.dashboard.setlimitandcategory.ui

import androidx.compose.runtime.Immutable
import com.example.moneyguard.core.arch.UiState

/**
 * Three-step onboarding flow held inside a single screen:
 *
 * - [LIMIT] — pick the daily spend limit (slider).
 * - [CATEGORIES] — pick spend categories.
 * - [NOTIFICATION_ACCESS] — grant notification access so the app can read
 *   payment notifications and auto-log spends.
 *
 * The permission step is slightly stateful:
 * - first arrival -> only show "Open settings"
 * - return without granting -> show "Open settings" + "I'll do it later"
 * - permission granted -> show success copy + "All done - let's go"
 */
enum class SetLimitStep { LIMIT, CATEGORIES, NOTIFICATION_ACCESS }

@Immutable
data class SetYourLimitAndCategoryUiState(
    val step: SetLimitStep,
    val limit: Int,
    val selectedCategories: Set<Category>,
    val hasNotificationAccess: Boolean,
    val hasOpenedNotificationSettings: Boolean,
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
            hasNotificationAccess = false,
            hasOpenedNotificationSettings = false,
            isLoading = false
        )
    }
}
