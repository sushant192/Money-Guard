package com.example.moneyguard.core.notifications

import android.app.Notification
import android.service.notification.StatusBarNotification

/**
 * Filters [NotificationListenerService.onNotificationPosted] callbacks that should not
 * create expenses. One visible bank/email alert often triggers multiple system events.
 */
internal object NotificationPostedFilter {

    fun shouldProcess(sbn: StatusBarNotification): Boolean {
        val notification = sbn.notification
        return shouldProcess(
            notificationFlags = notification.flags,
            text = NotificationTextExtractor.extract(notification),
        )
    }

    internal fun shouldProcess(notificationFlags: Int, text: String): Boolean {
        if (notificationFlags and Notification.FLAG_GROUP_SUMMARY != 0) {
            return false
        }
        if (isInProgressOnly(notificationFlags, text)) {
            return false
        }
        return true
    }

    /**
     * Skips ongoing "payment processing" alerts; the final debit notification follows.
     */
    internal fun isInProgressOnly(notificationFlags: Int, text: String): Boolean {
        if (notificationFlags and Notification.FLAG_ONGOING_EVENT == 0) return false
        val lower = text.lowercase()
        val inProgress =
            lower.contains("processing") ||
                lower.contains("in progress") ||
                lower.contains("initiated") ||
                lower.contains("please wait")
        if (!inProgress) return false
        return !lower.contains("debited") &&
            !lower.contains("debit alert") &&
            !lower.contains("successful") &&
            !lower.contains("completed")
    }
}
