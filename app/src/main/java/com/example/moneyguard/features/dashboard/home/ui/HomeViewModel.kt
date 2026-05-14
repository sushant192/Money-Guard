package com.example.moneyguard.features.dashboard.home.ui

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.moneyguard.core.arch.BaseComposeViewModel
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.core.notifications.NotificationAccessManager
import com.example.moneyguard.features.auth.domain.repository.AuthRepository
import com.example.moneyguard.features.auth.domain.usecase.LogoutUseCase
import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase.ClearBudgetSetupUseCase
import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase.GetDailyLimitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Named
import java.util.Calendar

@KoinViewModel
class HomeViewModel(
    @Named("AppNavigator") private val navigator: Navigator,
    private val notificationAccessManager: NotificationAccessManager,
    private val authRepository: AuthRepository,
    private val logoutUseCase: LogoutUseCase,
    private val clearBudgetSetup: ClearBudgetSetupUseCase,
    private val getDailyLimit: GetDailyLimitUseCase,
) : BaseComposeViewModel<HomeUiState>(),
    HomeUiEvents {

    private val _uiState = MutableStateFlow(
        HomeUiState.Initial.copy(
            greetingPrefix = greetingPrefixForHour(Calendar.getInstance()),
            userName = currentUserDisplayName(),
            userEmail = authRepository.currentUser?.email.orEmpty(),
            hasNotificationAccess = notificationAccessManager.hasNotificationAccess(),
        )
    )
    override val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshSavedDailyLimit()
    }

    override fun onActive() {
        _uiState.update {
            it.copy(hasNotificationAccess = notificationAccessManager.hasNotificationAccess())
        }
        refreshSavedDailyLimit()
    }

    override fun onTabSelected(tab: DashboardTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    override fun onSeeAllExpensesClick() {
        // "See all" on Home jumps to the History tab — same dashboard, just
        // another tab, so we reuse the existing tab-selection path.
        onTabSelected(DashboardTab.History)
    }

    override fun onFabClick() {
        // Manual expense entry — wire when feature exists.
    }

    override fun onAlertThresholdSelect(threshold: AlertThreshold) {
        _uiState.update { it.copy(alertThreshold = threshold) }
    }

    override fun onGrantNotificationAccessClick(activityContext: Context) {
        notificationAccessManager.openNotificationAccessSettings(activityContext)
    }

    override fun onLogoutClick() {
        // Sign out from Firebase first, then bounce back to the auth graph
        // and tear down the dashboard graph entirely so the user can't swipe
        // back into a "logged-out" Home. AuthGraph's start destination
        // (GetStarted) becomes the new top.
        viewModelScope.launch {
            clearBudgetSetup()
            logoutUseCase()
            navigator.navigate(Destination.AuthGraph) {
                popUpTo(Destination.DashboardGraph) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    companion object {
        internal fun greetingPrefixForHour(calendar: Calendar): String {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            return when (hour) {
                in 5..11 -> "Good morning,"
                in 12..16 -> "Good afternoon,"
                else -> "Good evening,"
            }
        }
    }

    private fun currentUserDisplayName(): String {
        val user = authRepository.currentUser
        return user?.displayName
            ?.takeIf { it.isNotBlank() }
            ?: user?.email
                ?.substringBefore("@")
                ?.takeIf { it.isNotBlank() }
            ?: HomeUiState.Initial.userName
    }

    private fun refreshSavedDailyLimit() {
        viewModelScope.launch {
            val savedLimit = getDailyLimit() ?: return@launch
            _uiState.update { state ->
                val remaining = (savedLimit - state.spentTodayRupees).coerceAtLeast(0)
                val usedPercent = if (savedLimit > 0) {
                    ((state.spentTodayRupees * 100f) / savedLimit).toInt()
                } else {
                    0
                }
                state.copy(
                    dailyLimitRupees = savedLimit,
                    remainingRupees = remaining,
                    budgetUsedPercent = usedPercent,
                )
            }
        }
    }
}
