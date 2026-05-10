package com.example.moneyguard.di

import com.example.moneyguard.core.navigation.AppNavigator
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.Navigator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Manual bindings that need [org.koin.core.qualifier.Qualifier]s (e.g. dispatchers,
 * named navigators). Annotation-based bindings (`@KoinViewModel`, `@Single`)
 * live in [AppModules] and are picked up by the KSP scan.
 */
val appModule = module {

    // Coroutine dispatchers
    single<CoroutineDispatcher>(named("IODispatcher")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("MainDispatcher")) { Dispatchers.Main.immediate }
    single<CoroutineDispatcher>(named("DefaultDispatcher")) { Dispatchers.Default }

    // Navigation — start the app on the auth graph, which itself starts at
    // GetStarted. Once we have a session-restore use case, this can return
    // either AuthGraph or DashboardGraph based on whether the user is signed in.
    single<Navigator>(named("AppNavigator")) {
        AppNavigator(startDestination = Destination.AuthGraph)
    }
}
