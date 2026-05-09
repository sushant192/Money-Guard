package com.example.moneyguard.core.navigation

import androidx.navigation.NavOptionsBuilder

/**
 * One-shot navigation commands emitted by ViewModels via [Navigator] and
 * consumed at the activity level (where the [androidx.navigation.NavController] lives).
 */
sealed interface NavigationAction {

    data class Navigate(
        val destination: Destination,
        val navOptions: NavOptionsBuilder.() -> Unit = {}
    ) : NavigationAction

    data object NavigateUp : NavigationAction
}
