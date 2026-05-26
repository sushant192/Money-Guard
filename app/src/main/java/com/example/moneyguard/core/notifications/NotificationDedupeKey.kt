package com.example.moneyguard.core.notifications

import com.example.moneyguard.core.notifications.model.ParsedPaymentNotification

/**
 * Stable id for notification-derived expenses. [android.service.notification.StatusBarNotification.getKey]
 * changes when the same payment posts multiple shade entries (bank + wallet, processing + success, etc.).
 */
internal object NotificationDedupeKey {

    private const val TIME_BUCKET_MS = 3 * 60 * 1000L

    fun build(
        packageName: String,
        parsed: ParsedPaymentNotification,
        postTimeEpochMs: Long,
    ): String {
        val ref = parsed.transactionRef?.trim()?.uppercase()
        if (!ref.isNullOrBlank()) {
            return "notif|$packageName|ref|$ref"
        }
        val title = parsed.title.trim().lowercase()
        val bucket = postTimeEpochMs / TIME_BUCKET_MS
        return "notif|$packageName|${parsed.amountRupees}|$title|$bucket"
    }
}
