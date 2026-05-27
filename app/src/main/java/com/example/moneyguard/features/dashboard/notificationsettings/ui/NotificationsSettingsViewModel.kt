package com.example.moneyguard.features.dashboard.notificationsettings.ui

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.moneyguard.core.arch.BaseComposeViewModel
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.core.notifications.PostNotificationPermissionManager
import com.example.moneyguard.core.notifications.alerts.DailySummaryScheduler
import com.example.moneyguard.core.notifications.alerts.MoneyGuardAlertNotifier
import com.example.moneyguard.data.datastore.MoneyGuardPreferenceDataStore
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
    private val alertNotifier: MoneyGuardAlertNotifier,
    private val postNotificationPermissionManager: PostNotificationPermissionManager,
    private val preferenceDataStore: MoneyGuardPreferenceDataStore,
    private val context: Context,
) : BaseComposeViewModel<NotificationsSettingsUiState>(),
    NotificationsSettingsUiEvents {

    private val _uiState = MutableStateFlow(NotificationsSettingsUiState.Initial)
    override val uiState: StateFlow<NotificationsSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferenceDataStore.observeAlertThresholdPercent().collect { percent ->
                _uiState.update { it.copy(limitWarningThresholdPercent = percent) }
            }
        }
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

    override fun onActive() {
        viewModelScope.launch {
            val percent = preferenceDataStore.getAlertThresholdPercent()
            _uiState.update { it.copy(limitWarningThresholdPercent = percent) }
            refreshPostNotificationUi()
            maybeRequestPostNotificationPermission()
        }
    }

    override fun onBackClick() {
        viewModelScope.launch { navigator.navigateUp() }
    }

    override fun onPostNotificationPromptHandled(granted: Boolean) {
        viewModelScope.launch {
            if (!granted) {
                preferenceDataStore.setDeclinedPostNotificationOnSettings()
            } else {
                preferenceDataStore.clearDeclinedPostNotificationOnSettings()
            }
            refreshPostNotificationUi()
        }
    }

    override fun onRequestPostNotificationPermissionClick() {
        _uiState.update { it.copy(requestPostNotificationPermission = true) }
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

    private suspend fun refreshPostNotificationUi() {
        val canPost = postNotificationPermissionManager.canPostAlerts()
        val showBanner = !canPost && preferenceDataStore.hasDeclinedPostNotificationOnSettings()
        _uiState.update {
            it.copy(
                requestPostNotificationPermission = false,
                showPermissionDeniedBanner = showBanner,
            )
        }
    }

    private suspend fun maybeRequestPostNotificationPermission() {
        if (postNotificationPermissionManager.canPostAlerts()) return
        if (preferenceDataStore.hasDeclinedPostNotificationOnSettings()) return
        _uiState.update { it.copy(requestPostNotificationPermission = true) }
    }

    private fun updateAndPersist(transform: (NotificationsSettingsUiState) -> NotificationsSettingsUiState) {
        val updated = transform(_uiState.value)
        _uiState.value = updated
        viewModelScope.launch {
            repository.saveSettings(updated.toDomain())
            alertNotifier.ensureChannels(updated.sound)
            syncDailySummarySchedule(updated.dailySummary)
        }
    }

    private fun syncDailySummarySchedule(enabled: Boolean) {
        if (enabled) {
            DailySummaryScheduler.schedule(context.applicationContext)
        } else {
            DailySummaryScheduler.cancel(context.applicationContext)
        }
    }
}
