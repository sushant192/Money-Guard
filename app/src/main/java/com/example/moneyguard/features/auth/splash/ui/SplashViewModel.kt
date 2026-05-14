package com.example.moneyguard.features.auth.splash.ui

import androidx.lifecycle.viewModelScope
import com.example.moneyguard.core.arch.BaseComposeViewModel
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.features.auth.domain.repository.AuthRepository
import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase.HasCompletedBudgetSetupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Named

/**
 * Decides whether a freshly launched app shows the auth flow or the dashboard.
 *
 * We read [AuthRepository.currentUser] synchronously — Firebase has already
 * restored the cached user by the time `FirebaseAuth.getInstance()` is touched
 * during DI graph setup, so this resolves immediately and the user only sees
 * the splash for one frame.
 */
@KoinViewModel
class SplashViewModel(
    @Named("AppNavigator") private val navigator: Navigator,
    private val authRepository: AuthRepository,
    private val hasCompletedBudgetSetup: HasCompletedBudgetSetupUseCase,
) : BaseComposeViewModel<SplashUiState>() {

    private val _uiState = MutableStateFlow(SplashUiState.Initial)
    override val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val target = when {
                authRepository.currentUser == null -> Destination.AuthGraph
                hasCompletedBudgetSetup() -> Destination.Home
                else -> Destination.DashboardGraph
            }
            navigator.navigate(target) {
                popUpTo(Destination.Splash) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}
