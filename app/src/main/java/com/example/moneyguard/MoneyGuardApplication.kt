package com.example.moneyguard

import android.app.Application
import com.example.moneyguard.di.AppModules
import com.example.moneyguard.di.appModule
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
    }
}
