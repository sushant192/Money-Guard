package com.example.moneyguard.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations.
 *
 * The app is split into two top-level **graphs**:
 *
 *   - [AuthGraph] — onboarding & auth screens ([GetStarted], [Login], [SignUp])
 *   - [DashboardGraph] — the post-auth experience ([Home], …)
 *
 * Successful login / sign-up navigates to [DashboardGraph] while popping the
 * entire [AuthGraph] off the back stack so the user can't swipe back into auth.
 *
 * Each [Serializable] type can be used with the typed Compose Navigation APIs:
 * `composable<Destination.Login>`, `toRoute<Destination.Login>()`, etc.
 */
@Serializable
sealed interface Destination {

    // -------- Splash --------

    /**
     * Cold-start router. Decides whether the user goes into [AuthGraph] or
     * [DashboardGraph] based on the cached Firebase user. Pops itself off the
     * back stack once the decision is made.
     */
    @Serializable
    data object Splash : Destination

    // -------- Graph routes --------

    @Serializable
    data object AuthGraph : Destination

    @Serializable
    data object DashboardGraph : Destination

    // -------- Auth graph screens --------

    @Serializable
    data object GetStarted : Destination

    @Serializable
    data object Login : Destination

    @Serializable
    data object SignUp : Destination

    // -------- Dashboard graph screens --------

    /**
     * One-time onboarding step shown right after sign-up so the user can
     * configure a daily spend limit and pick their relevant categories.
     * Login skips this destination and goes straight to [Home].
     */
    @Serializable
    data object SetLimitAndCategory : Destination

    @Serializable
    data object Home : Destination
}
