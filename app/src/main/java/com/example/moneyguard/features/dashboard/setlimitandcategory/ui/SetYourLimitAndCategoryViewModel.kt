package com.example.moneyguard.features.dashboard.setlimitandcategory.ui

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.moneyguard.core.arch.BaseComposeViewModel
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.core.notifications.NotificationAccessManager
import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase.SaveBudgetSetupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Named

@KoinViewModel
class SetYourLimitAndCategoryViewModel(
    @Named("AppNavigator") private val navigator: Navigator,
    private val notificationAccessManager: NotificationAccessManager,
    private val saveBudgetSetup: SaveBudgetSetupUseCase,
) : BaseComposeViewModel<SetYourLimitAndCategoryUiState>(),
    SetYourLimitAndCategoryUiEvents {

    private val _uiState = MutableStateFlow(
        SetYourLimitAndCategoryUiState.Initial.copy(
            hasNotificationAccess = notificationAccessManager.hasNotificationAccess(),
        )
    )
    override val uiState: StateFlow<SetYourLimitAndCategoryUiState> = _uiState.asStateFlow()

    override fun onActive() {
        _uiState.update {
            it.copy(hasNotificationAccess = notificationAccessManager.hasNotificationAccess())
        }
    }

    override fun onLimitChange(value: Int) {
        _uiState.update {
            it.copy(limit = snapToHundred(value))
        }
    }

    override fun onCategoryToggle(category: Category) {
        _uiState.update {
            val updated = if (category in it.selectedCategories) {
                it.selectedCategories - category
            } else {
                it.selectedCategories + category
            }
            it.copy(selectedCategories = updated)
        }
    }

    override fun onContinueClick() {
        if (_uiState.value.isLoading) return
        when (_uiState.value.step) {
            SetLimitStep.LIMIT -> {
                _uiState.update { it.copy(step = SetLimitStep.CATEGORIES) }
            }
            SetLimitStep.CATEGORIES -> {
                _uiState.update { it.copy(step = SetLimitStep.NOTIFICATION_ACCESS) }
            }
            SetLimitStep.NOTIFICATION_ACCESS -> Unit
        }
    }

    override fun onBackClick() {
        if (_uiState.value.isLoading) return
        when (_uiState.value.step) {
            SetLimitStep.CATEGORIES -> {
                // Flipping back to LIMIT keeps any user-made selections in
                // state so they aren't lost.
                _uiState.update { it.copy(step = SetLimitStep.LIMIT) }
            }
            else -> Unit
        }
    }

    override fun onOpenNotificationSettingsClick(activityContext: Context) {
        _uiState.update { it.copy(hasOpenedNotificationSettings = true) }
        notificationAccessManager.openNotificationAccessSettings(activityContext)
    }

    override fun onFinishClick() {
        if (_uiState.value.isLoading) return
        finishOnboarding()
    }

    private fun finishOnboarding() {
        viewModelScope.launch {
            val current = _uiState.value
            _uiState.update { it.copy(isLoading = true) }

            runCatching {
                saveBudgetSetup(
                    limit = current.limit,
                    selectedCategories = current.selectedCategories.map(Category::name).toSet(),
                )
            }.onSuccess {
                navigator.navigate(Destination.Home) {
                    popUpTo(Destination.SetLimitAndCategory) { inclusive = true }
                    launchSingleTop = true
                }
            }.onFailure {
                _uiState.update { state -> state.copy(isLoading = false) }
            }
        }
    }

    /**
     * Rounds [value] to the nearest [SetYourLimitAndCategoryUiState.LIMIT_STEP]
     * (100) and clamps to [MIN_LIMIT, MAX_LIMIT].
     */
    private fun snapToHundred(value: Int): Int {
        val step = SetYourLimitAndCategoryUiState.LIMIT_STEP
        val rounded = ((value + step / 2) / step) * step
        return rounded.coerceIn(
            SetYourLimitAndCategoryUiState.MIN_LIMIT,
            SetYourLimitAndCategoryUiState.MAX_LIMIT
        )
    }
}
