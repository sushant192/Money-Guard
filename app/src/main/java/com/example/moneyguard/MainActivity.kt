package com.example.moneyguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneyguard.core.arch.ObserveAsEvents
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.NavigationAction
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.features.auth.getstarted.ui.GetStartedScreen
import com.example.moneyguard.features.auth.getstarted.ui.GetStartedViewModel
import com.example.moneyguard.ui.theme.MoneyGuardTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
        composable<Destination.GetStarted> {
            GetStartedScreen(viewModel = koinViewModel<GetStartedViewModel>())
        }

        // TODO: Login & SignUp screens to be added next.
    }
}
