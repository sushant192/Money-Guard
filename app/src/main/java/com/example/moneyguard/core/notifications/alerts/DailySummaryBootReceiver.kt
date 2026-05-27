package com.example.moneyguard.core.notifications.alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.moneyguard.features.dashboard.notificationsettings.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/** Re-schedules the daily summary after device reboot. */
class DailySummaryBootReceiver : BroadcastReceiver(), KoinComponent {

    private val settingsRepository: NotificationSettingsRepository by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = settingsRepository.observeSettings().first()
                if (settings.dailySummary) {
                    DailySummaryScheduler.schedule(context.applicationContext)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
