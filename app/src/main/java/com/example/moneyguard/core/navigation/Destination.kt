package com.example.moneyguard.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations. Each destination is a [Serializable] type
 * so it can be used with `composable<Destination.Foo>` / `toRoute<Destination.Foo>()`.
 */
@Serializable
sealed interface Destination {

    @Serializable
    data object GetStarted : Destination

    @Serializable
    data object Login : Destination

    @Serializable
    data object SignUp : Destination
}
