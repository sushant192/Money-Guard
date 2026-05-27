package com.example.moneyguard.core.notifications.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.moneyguard.MainActivity
import com.example.moneyguard.R
import com.example.moneyguard.core.notifications.PostNotificationPermissionManager
import com.example.moneyguard.features.dashboard.notificationsettings.domain.model.NotificationSound
import org.koin.core.annotation.Single

/**
 * Posts MoneyGuard's own spending alerts (separate from reading bank shade notifications).
 */
@Single
class MoneyGuardAlertNotifier(
    private val context: Context,
    private val permissionManager: PostNotificationPermissionManager,
) {

    private val notificationManager = NotificationManagerCompat.from(context)

    fun ensureChannels(sound: NotificationSound) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) return
        val systemManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        CHANNEL_CONFIGS.forEach { config ->
            val channel =
                NotificationChannel(
                    config.id,
                    context.getString(R.string.alert_channel_name),
                    config.importance,
                ).apply {
                    description = context.getString(R.string.alert_channel_desc)
                    setShowBadge(true)
                    when (sound) {
                        NotificationSound.NONE -> {
                            setSound(null, null)
                            enableVibration(false)
                        }
                        NotificationSound.SUBTLE -> {
                            setSound(
                                Settings.System.DEFAULT_NOTIFICATION_URI,
                                AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .build(),
                            )
                            enableVibration(false)
                        }
                        NotificationSound.DEFAULT -> {
                            enableVibration(true)
                        }
                    }
                }
            systemManager.createNotificationChannel(channel)
        }
    }

    fun showNewExpense(amountRupees: Int, merchant: String, sound: NotificationSound) {
        post(
            id = AlertNotificationIds.NEW_EXPENSE,
            sound = sound,
            title = context.getString(R.string.alert_new_expense_title),
            body =
                context.getString(
                    R.string.alert_new_expense_body,
                    amountRupees,
                    merchant,
                ),
        )
    }

    fun showLargeTransaction(amountRupees: Int, merchant: String, threshold: Int, sound: NotificationSound) {
        post(
            id = AlertNotificationIds.LARGE_TRANSACTION,
            sound = sound,
            title = context.getString(R.string.alert_large_txn_title),
            body =
                context.getString(
                    R.string.alert_large_txn_body,
                    amountRupees,
                    merchant,
                    threshold,
                ),
        )
    }

    fun showLimitWarning(percentUsed: Int, limitRupees: Int, remainingRupees: Int, sound: NotificationSound) {
        post(
            id = AlertNotificationIds.LIMIT_WARNING,
            sound = sound,
            title = context.getString(R.string.alert_limit_warning_title),
            body =
                context.getString(
                    R.string.alert_limit_warning_body,
                    percentUsed,
                    limitRupees,
                    remainingRupees,
                ),
        )
    }

    fun showLimitExceeded(overByRupees: Int, limitRupees: Int, sound: NotificationSound) {
        post(
            id = AlertNotificationIds.LIMIT_EXCEEDED,
            sound = sound,
            title = context.getString(R.string.alert_limit_exceeded_title),
            body =
                context.getString(
                    R.string.alert_limit_exceeded_body,
                    overByRupees,
                    limitRupees,
                ),
        )
    }

    fun showDailySummary(spentRupees: Int, transactionCount: Int, sound: NotificationSound) {
        val body =
            if (transactionCount == 0) {
                context.getString(R.string.alert_daily_summary_empty_body)
            } else {
                context.getString(
                    R.string.alert_daily_summary_body,
                    spentRupees,
                    transactionCount,
                )
            }
        post(
            id = AlertNotificationIds.DAILY_SUMMARY,
            sound = sound,
            title = context.getString(R.string.alert_daily_summary_title),
            body = body,
        )
    }

    private fun post(id: Int, sound: NotificationSound, title: String, body: String) {
        if (!permissionManager.canPostAlerts()) return
        ensureChannels(sound)

        val launchIntent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                id,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val channelId = channelIdFor(sound)
        val notification =
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setGroup(AlertNotificationIds.GROUP_KEY)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

        notificationManager.notify(id, notification)
    }

    private fun channelIdFor(sound: NotificationSound): String =
        when (sound) {
            NotificationSound.DEFAULT -> CHANNEL_DEFAULT
            NotificationSound.SUBTLE -> CHANNEL_SUBTLE
            NotificationSound.NONE -> CHANNEL_SILENT
        }

    private data class ChannelConfig(val id: String, val importance: Int)

    private companion object {
        const val CHANNEL_DEFAULT = "moneyguard_alerts_default"
        const val CHANNEL_SUBTLE = "moneyguard_alerts_subtle"
        const val CHANNEL_SILENT = "moneyguard_alerts_silent"

        val CHANNEL_CONFIGS =
            listOf(
                ChannelConfig(CHANNEL_DEFAULT, NotificationManager.IMPORTANCE_HIGH),
                ChannelConfig(CHANNEL_SUBTLE, NotificationManager.IMPORTANCE_DEFAULT),
                ChannelConfig(CHANNEL_SILENT, NotificationManager.IMPORTANCE_LOW),
            )
    }
}
