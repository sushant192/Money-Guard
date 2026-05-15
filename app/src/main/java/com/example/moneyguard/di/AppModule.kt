package com.example.moneyguard.di

import androidx.room.Room
import com.example.moneyguard.R
import com.example.moneyguard.core.navigation.AppNavigator
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.data.local.MoneyGuardDatabase
import com.example.moneyguard.data.local.dao.ExpenseDao
import com.example.moneyguard.features.auth.data.AuthRepositoryImpl
import com.example.moneyguard.features.auth.data.GoogleSignInHelper
import com.example.moneyguard.features.auth.domain.repository.AuthRepository
import com.example.moneyguard.features.dashboard.home.data.ExpenseRepositoryImpl
import com.example.moneyguard.features.dashboard.home.domain.repository.ExpenseRepository
import com.example.moneyguard.features.dashboard.setlimitandcategory.data.BudgetSetupRepositoryImpl
import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.repository.BudgetSetupRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Manual bindings that need [org.koin.core.qualifier.Qualifier]s (e.g. dispatchers,
 * named navigators) or third-party constructors (Firebase, Credential Manager).
 * Annotation-based bindings (`@KoinViewModel`, `@Factory`) live in [AppModules]
 * and are picked up by the KSP scan.
 */
val appModule = module {

    // Coroutine dispatchers
    single<CoroutineDispatcher>(named("IODispatcher")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("MainDispatcher")) { Dispatchers.Main.immediate }
    single<CoroutineDispatcher>(named("DefaultDispatcher")) { Dispatchers.Default }

    // Navigation — start at Splash; the SplashViewModel decides whether to
    // route into AuthGraph or DashboardGraph based on FirebaseAuth.currentUser.
    single<Navigator>(named("AppNavigator")) {
        AppNavigator(startDestination = Destination.Splash)
    }

    // Auth (Firebase + Credential Manager)
    single { FirebaseAuth.getInstance() }
    single {
        GoogleSignInHelper(
            webClientId = androidContext().getString(R.string.default_web_client_id),
        )
    }
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    single<BudgetSetupRepository> { BudgetSetupRepositoryImpl(get()) }

    single {
        Room.databaseBuilder(
            androidContext(),
            MoneyGuardDatabase::class.java,
            "moneyguard.db",
        ).fallbackToDestructiveMigration()
            .build()
    }
    single<ExpenseDao> { get<MoneyGuardDatabase>().expenseDao() }
    single<ExpenseRepository> { ExpenseRepositoryImpl(get()) }
}
