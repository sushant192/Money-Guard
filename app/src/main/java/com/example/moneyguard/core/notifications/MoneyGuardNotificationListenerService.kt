package com.example.moneyguard.core.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * System entry point for reading posted notifications after the user grants
 * Notification Access. The actual parsing / categorisation pipeline will plug
 * into this service next.
 */
class MoneyGuardNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val notification = sbn ?: return

        // Placeholder hook for the spend-detection pipeline. Keeping a small
        // log here makes it easy to confirm that Notification Access wiring is
        // alive before we add parsing + persistence.
        Log.d(
            TAG,
            "Notification received from ${notification.packageName}"
        )
    }

    companion object {
        private const val TAG = "MoneyGuardNotifSvc"
    }
}
