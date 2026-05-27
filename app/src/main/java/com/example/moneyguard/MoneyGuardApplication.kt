package com.example.moneyguard

import android.app.Application
import com.example.moneyguard.core.notifications.alerts.DailySummaryScheduler
import com.example.moneyguard.di.AppModules
import com.example.moneyguard.di.appModule
import com.example.moneyguard.features.dashboard.notificationsettings.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.ksp.generated.module

class MoneyGuardApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)
            androidContext(this@MoneyGuardApplication)
            modules(
                appModule,
                AppModules().module
            )
        }

        val settingsRepository: NotificationSettingsRepository by inject()
        CoroutineScope(Dispatchers.IO).launch {
            val settings = settingsRepository.observeSettings().first()
            if (settings.dailySummary) {
                DailySummaryScheduler.schedule(applicationContext)
            }
        }
    }
}
