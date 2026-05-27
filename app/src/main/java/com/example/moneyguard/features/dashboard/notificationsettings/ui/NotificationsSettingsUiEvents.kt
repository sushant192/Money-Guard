package com.example.moneyguard.features.dashboard.notificationsettings.ui

import com.example.moneyguard.features.dashboard.notificationsettings.domain.model.NotificationSound

interface NotificationsSettingsUiEvents {
    fun onBackClick()

    fun onLimitWarningToggle(enabled: Boolean)

    fun onLimitExceededToggle(enabled: Boolean)

    fun onDailySummaryToggle(enabled: Boolean)

    fun onNewExpenseDetectedToggle(enabled: Boolean)

    fun onLargeTransactionToggle(enabled: Boolean)

    fun onQuietHoursToggle(enabled: Boolean)

    fun onSoundSelected(sound: NotificationSound)

    fun onQuietHoursStartClick()

    fun onQuietHoursEndClick()

    fun onQuietHoursStartSelected(totalMinutes: Int)

    fun onQuietHoursEndSelected(totalMinutes: Int)

    fun onDismissTimePicker()

    fun onPostNotificationPromptHandled(granted: Boolean)

    fun onRequestPostNotificationPermissionClick()
}
