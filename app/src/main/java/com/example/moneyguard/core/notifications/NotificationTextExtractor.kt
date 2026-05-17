package com.example.moneyguard.core.notifications

import android.app.Notification
import android.os.Bundle

/**
 * Pulls human-readable text from a [Notification] (title, body, big text,
 * inbox lines). Does not read SMS — only what the posting app put on the shade.
 */
internal object NotificationTextExtractor {

    fun extract(notification: Notification): String {
        val extras: Bundle = notification.extras ?: return ""
        val parts = linkedSetOf<String>()

        extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.let(parts::add)
        extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.let(parts::add)
        extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()?.let(parts::add)
        extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()?.let(parts::add)
        extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString()?.let(parts::add)

        extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.forEach { line ->
            line?.toString()?.takeIf { it.isNotBlank() }?.let(parts::add)
        }

        return parts.joinToString(separator = " ").trim()
    }
}
