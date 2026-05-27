package com.example.moneyguard.features.dashboard.notificationsettings.ui

import androidx.lifecycle.viewModelScope
import com.example.moneyguard.core.arch.BaseComposeViewModel
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.features.dashboard.notificationsettings.domain.model.NotificationSound
import com.example.moneyguard.features.dashboard.notificationsettings.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Named

@KoinViewModel
class NotificationsSettingsViewModel(
    @Named("AppNavigator") private val navigator: Navigator,
    private val repository: NotificationSettingsRepository,
) : BaseComposeViewModel<NotificationsSettingsUiState>(),
    NotificationsSettingsUiEvents {

    private val _uiState = MutableStateFlow(NotificationsSettingsUiState.Initial)
    override val uiState: StateFlow<NotificationsSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeSettings().collect { settings ->
                _uiState.update { current ->
                    val mapped = NotificationsSettingsUiState.from(settings)
                    current.copy(
                        limitWarning = mapped.limitWarning,
                        limitExceeded = mapped.limitExceeded,
                        dailySummary = mapped.dailySummary,
                        newExpenseDetected = mapped.newExpenseDetected,
                        largeTransaction = mapped.largeTransaction,
                        quietHoursEnabled = mapped.quietHoursEnabled,
                        sound = mapped.sound,
                        quietHoursStartMinutes = mapped.quietHoursStartMinutes,
                        quietHoursEndMinutes = mapped.quietHoursEndMinutes,
                    )
                }
            }
        }
    }

    override fun onBackClick() {
        viewModelScope.launch { navigator.navigateUp() }
    }

    override fun onLimitWarningToggle(enabled: Boolean) {
        updateAndPersist { it.copy(limitWarning = enabled) }
    }

    override fun onLimitExceededToggle(enabled: Boolean) {
        updateAndPersist { it.copy(limitExceeded = enabled) }
    }

    override fun onDailySummaryToggle(enabled: Boolean) {
        updateAndPersist { it.copy(dailySummary = enabled) }
    }

    override fun onNewExpenseDetectedToggle(enabled: Boolean) {
        updateAndPersist { it.copy(newExpenseDetected = enabled) }
    }

    override fun onLargeTransactionToggle(enabled: Boolean) {
        updateAndPersist { it.copy(largeTransaction = enabled) }
    }

    override fun onQuietHoursToggle(enabled: Boolean) {
        updateAndPersist { it.copy(quietHoursEnabled = enabled) }
    }

    override fun onSoundSelected(sound: NotificationSound) {
        updateAndPersist { it.copy(sound = sound) }
    }

    override fun onQuietHoursStartClick() {
        _uiState.update {
            it.copy(showQuietHoursStartPicker = true, showQuietHoursEndPicker = false)
        }
    }

    override fun onQuietHoursEndClick() {
        _uiState.update {
            it.copy(showQuietHoursEndPicker = true, showQuietHoursStartPicker = false)
        }
    }

    override fun onQuietHoursStartSelected(totalMinutes: Int) {
        updateAndPersist {
            it.copy(
                quietHoursStartMinutes = totalMinutes,
                showQuietHoursStartPicker = false,
            )
        }
    }

    override fun onQuietHoursEndSelected(totalMinutes: Int) {
        updateAndPersist {
            it.copy(
                quietHoursEndMinutes = totalMinutes,
                showQuietHoursEndPicker = false,
            )
        }
    }

    override fun onDismissTimePicker() {
        _uiState.update { it.copy(showQuietHoursStartPicker = false, showQuietHoursEndPicker = false) }
    }

    private fun updateAndPersist(transform: (NotificationsSettingsUiState) -> NotificationsSettingsUiState) {
        val updated = transform(_uiState.value)
        _uiState.value = updated
        viewModelScope.launch {
            repository.saveSettings(updated.toDomain())
        }
    }
}
