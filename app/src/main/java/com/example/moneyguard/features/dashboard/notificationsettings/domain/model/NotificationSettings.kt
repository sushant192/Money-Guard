package com.example.moneyguard.features.dashboard.notificationsettings.domain.model

data class NotificationSettings(
    val limitWarning: Boolean = true,
    val limitExceeded: Boolean = true,
    val dailySummary: Boolean = true,
    val newExpenseDetected: Boolean = true,
    val largeTransaction: Boolean = false,
    val quietHoursEnabled: Boolean = true,
    val sound: NotificationSound = NotificationSound.DEFAULT,
    val quietHoursStartMinutes: Int = 22 * 60,
    val quietHoursEndMinutes: Int = 7 * 60,
)
