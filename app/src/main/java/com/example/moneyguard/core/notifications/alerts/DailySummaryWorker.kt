package com.example.moneyguard.core.notifications.alerts

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneyguard.features.dashboard.notificationsettings.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DailySummaryWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params),
    KoinComponent {

    private val spendAlertCoordinator: SpendAlertCoordinator by inject()
    private val settingsRepository: NotificationSettingsRepository by inject()

    override suspend fun doWork(): Result {
        val settings = settingsRepository.observeSettings().first()
        if (settings.dailySummary) {
            spendAlertCoordinator.sendDailySummaryIfEnabled()
        }
        if (settings.dailySummary) {
            DailySummaryScheduler.schedule(applicationContext)
        }
        return Result.success()
    }
}
