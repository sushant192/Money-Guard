package com.example.moneyguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.moneyguard.core.arch.ObserveAsEvents
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.NavigationAction
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.features.auth.getstarted.ui.GetStartedScreen
import com.example.moneyguard.features.auth.getstarted.ui.GetStartedViewModel
import com.example.moneyguard.features.auth.login.ui.LoginScreen
import com.example.moneyguard.features.auth.login.ui.LoginViewModel
import com.example.moneyguard.features.auth.signup.ui.SignUpScreen
import com.example.moneyguard.features.auth.signup.ui.SignUpViewModel
import com.example.moneyguard.features.dashboard.home.ui.HomeScreen
import com.example.moneyguard.features.dashboard.home.ui.HomeViewModel
import com.example.moneyguard.features.dashboard.setlimitandcategory.ui.SetYourLimitAndCategoryScreen
import com.example.moneyguard.features.dashboard.setlimitandcategory.ui.SetYourLimitAndCategoryViewModel
import com.example.moneyguard.ui.theme.MoneyGuardTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneyGuardTheme {
                MoneyGuardApp()
            }
        }
    }
}

@Composable
private fun MoneyGuardApp() {
    val navController = rememberNavController()
    val navigator: Navigator = koinInject(named("AppNavigator"))

    ObserveAsEvents(flow = navigator.navigationActions) { action ->
        when (action) {
            is NavigationAction.Navigate -> navController.navigate(
                route = action.destination,
                builder = action.navOptions
            )

            NavigationAction.NavigateUp -> navController.navigateUp()
        }
    }

    NavHost(
        navController = navController,
        startDestination = navigator.startDestination
    ) {
        authGraph()
        dashboardGraph()
    }
}

/**
 * Onboarding & authentication flow. Entry point of the app for unauthenticated
 * users; successful login / sign-up navigates out to [Destination.DashboardGraph].
 */
private fun NavGraphBuilder.authGraph() {
    navigation<Destination.AuthGraph>(
        startDestination = Destination.GetStarted
    ) {
        composable<Destination.GetStarted> {
            GetStartedScreen(viewModel = koinViewModel<GetStartedViewModel>())
        }

        composable<Destination.SignUp> {
            SignUpScreen(viewModel = koinViewModel<SignUpViewModel>())
        }

        composable<Destination.Login> {
            LoginScreen(viewModel = koinViewModel<LoginViewModel>())
        }
    }
}

/**
 * The post-auth experience. New screens (transactions, settings, …) get added
 * here as `composable<Destination.Foo> { … }` entries.
 */
private fun NavGraphBuilder.dashboardGraph() {
    // Sign-up enters the dashboard graph at SetLimitAndCategory (the
    // onboarding step). Login bypasses this and navigates straight to Home.
    navigation<Destination.DashboardGraph>(
        startDestination = Destination.SetLimitAndCategory
    ) {
        composable<Destination.SetLimitAndCategory> {
            SetYourLimitAndCategoryScreen(
                viewModel = koinViewModel<SetYourLimitAndCategoryViewModel>()
            )
        }

        composable<Destination.Home> {
            HomeScreen(viewModel = koinViewModel<HomeViewModel>())
        }
    }
}
