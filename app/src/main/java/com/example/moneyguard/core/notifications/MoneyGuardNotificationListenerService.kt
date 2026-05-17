package com.example.moneyguard.core.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import org.koin.android.ext.android.inject

/**
 * System entry point for reading posted notifications after the user grants
 * Notification Access. Runs in the app process (even when UI is not visible) and
 * forwards payment debits to Room via [NotificationExpenseProcessor].
 */
class MoneyGuardNotificationListenerService : NotificationListenerService() {

    private val processor: NotificationExpenseProcessor by inject()

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val notification = sbn ?: return
        processor.onNotificationPosted(notification)
    }
}
