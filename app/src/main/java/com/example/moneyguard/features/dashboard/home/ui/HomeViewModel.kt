package com.example.moneyguard.features.dashboard.home.ui

import com.example.moneyguard.core.arch.BaseComposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel
import java.util.Calendar

@KoinViewModel
class HomeViewModel : BaseComposeViewModel<HomeUiState>(),
    HomeUiEvents {

    private val _uiState = MutableStateFlow(
        HomeUiState.Initial.copy(greetingPrefix = greetingPrefixForHour(Calendar.getInstance()))
    )
    override val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    override fun onTabSelected(tab: DashboardTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    override fun onProfileClick() {
        // Profile screen lands in a later iteration.
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
}
