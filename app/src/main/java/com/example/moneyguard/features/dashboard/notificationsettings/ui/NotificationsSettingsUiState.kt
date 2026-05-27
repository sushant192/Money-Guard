package com.example.moneyguard.features.dashboard.notificationsettings.ui

import com.example.moneyguard.core.arch.UiState
import com.example.moneyguard.features.dashboard.notificationsettings.domain.model.NotificationSettings
import com.example.moneyguard.features.dashboard.notificationsettings.domain.model.NotificationSound

data class NotificationsSettingsUiState(
    val limitWarning: Boolean = true,
    val limitExceeded: Boolean = true,
    val dailySummary: Boolean = true,
    val newExpenseDetected: Boolean = true,
    val largeTransaction: Boolean = false,
    val quietHoursEnabled: Boolean = true,
    val sound: NotificationSound = NotificationSound.DEFAULT,
    val quietHoursStartMinutes: Int = 22 * 60,
    val quietHoursEndMinutes: Int = 7 * 60,
    val showQuietHoursStartPicker: Boolean = false,
    val showQuietHoursEndPicker: Boolean = false,
) : UiState {

    companion object {
        val Initial = NotificationsSettingsUiState()

        fun from(settings: NotificationSettings) =
            NotificationsSettingsUiState(
                limitWarning = settings.limitWarning,
                limitExceeded = settings.limitExceeded,
                dailySummary = settings.dailySummary,
                newExpenseDetected = settings.newExpenseDetected,
                largeTransaction = settings.largeTransaction,
                quietHoursEnabled = settings.quietHoursEnabled,
                sound = settings.sound,
                quietHoursStartMinutes = settings.quietHoursStartMinutes,
                quietHoursEndMinutes = settings.quietHoursEndMinutes,
            )
    }

    fun toDomain(): NotificationSettings =
        NotificationSettings(
            limitWarning = limitWarning,
            limitExceeded = limitExceeded,
            dailySummary = dailySummary,
            newExpenseDetected = newExpenseDetected,
            largeTransaction = largeTransaction,
            quietHoursEnabled = quietHoursEnabled,
            sound = sound,
            quietHoursStartMinutes = quietHoursStartMinutes,
            quietHoursEndMinutes = quietHoursEndMinutes,
        )
}
