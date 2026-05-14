package com.example.moneyguard.core.notifications

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import org.koin.core.annotation.Single

/**
 * Small wrapper around the system's notification-listener access APIs.
 *
 * Notification reading is not a runtime permission dialog; users must enable
 * our app inside the system's Notification Access settings screen.
 */
@Single
class NotificationAccessManager(
    private val context: Context,
) {
    fun hasNotificationAccess(): Boolean =
        NotificationManagerCompat.getEnabledListenerPackages(context)
            .contains(context.packageName)

    fun openNotificationAccessSettings(activityContext: Context) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            activityContext.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            activityContext.startActivity(
                Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}
