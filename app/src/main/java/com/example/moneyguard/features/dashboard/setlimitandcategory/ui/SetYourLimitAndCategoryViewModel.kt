package com.example.moneyguard.features.dashboard.setlimitandcategory.ui

import androidx.lifecycle.viewModelScope
import com.example.moneyguard.core.arch.BaseComposeViewModel
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Named

@KoinViewModel
class SetYourLimitAndCategoryViewModel(
    @Named("AppNavigator") private val navigator: Navigator
) : BaseComposeViewModel<SetYourLimitAndCategoryUiState>(),
    SetYourLimitAndCategoryUiEvents {

    private val _uiState = MutableStateFlow(SetYourLimitAndCategoryUiState.Initial)
    override val uiState: StateFlow<SetYourLimitAndCategoryUiState> = _uiState.asStateFlow()

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
        when (_uiState.value.step) {
            SetLimitStep.LIMIT -> {
                _uiState.update { it.copy(step = SetLimitStep.CATEGORIES) }
            }
            SetLimitStep.CATEGORIES -> finishOnboarding()
        }
    }

    override fun onBackClick() {
        // Only meaningful from the categories step. Flipping back to LIMIT
        // keeps any user-made selections in state so they aren't lost.
        if (_uiState.value.step == SetLimitStep.CATEGORIES) {
            _uiState.update { it.copy(step = SetLimitStep.LIMIT) }
        }
    }

    private fun finishOnboarding() {
        // Persistence (Room + DataStore) lands in the next iteration. For now,
        // hop to Home and pop this onboarding step off the back stack so the
        // user can't swipe back into it.
        viewModelScope.launch {
            navigator.navigate(Destination.Home) {
                popUpTo(Destination.SetLimitAndCategory) { inclusive = true }
                launchSingleTop = true
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
