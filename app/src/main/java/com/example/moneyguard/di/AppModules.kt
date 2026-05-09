package com.example.moneyguard.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Aggregates every annotation-based Koin definition under
 * `com.example.moneyguard` (e.g. `@KoinViewModel`, `@Single`, `@Factory`).
 *
 * KSP generates an extension property `AppModules.module` that we load from
 * [com.example.moneyguard.MoneyGuardApplication] alongside [appModule].
 */
@Module
@ComponentScan("com.example.moneyguard")
class AppModules
