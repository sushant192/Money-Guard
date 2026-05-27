package com.example.moneyguard.core.notifications.alerts

import com.example.moneyguard.data.datastore.MoneyGuardPreferenceDataStore
import com.example.moneyguard.features.dashboard.home.domain.repository.ExpenseRepository
import com.example.moneyguard.features.dashboard.notificationsettings.domain.repository.NotificationSettingsRepository
import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase.GetDailyLimitUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Evaluates user alert preferences after a spend is recorded and posts system notifications.
 */
@Single
class SpendAlertCoordinator(
    private val settingsRepository: NotificationSettingsRepository,
    private val preferenceDataStore: MoneyGuardPreferenceDataStore,
    private val expenseRepository: ExpenseRepository,
    private val getDailyLimit: GetDailyLimitUseCase,
    private val alertNotifier: MoneyGuardAlertNotifier,
) {

    suspend fun onExpenseRecorded(
        amountRupees: Int,
        merchantTitle: String,
        recordedAtEpochMs: Long = System.currentTimeMillis(),
    ) {
        val settings = settingsRepository.observeSettings().first()
        if (QuietHoursHelper.isMuted(settings)) return

        alertNotifier.ensureChannels(settings.sound)

        if (settings.newExpenseDetected) {
            alertNotifier.showNewExpense(
                amountRupees = amountRupees,
                merchant = merchantTitle,
                sound = settings.sound,
            )
        }

        if (settings.largeTransaction && amountRupees >= LARGE_TRANSACTION_THRESHOLD_RUPEES) {
            alertNotifier.showLargeTransaction(
                amountRupees = amountRupees,
                merchant = merchantTitle,
                threshold = LARGE_TRANSACTION_THRESHOLD_RUPEES,
                sound = settings.sound,
            )
        }

        val dailyLimit = getDailyLimit() ?: return
        if (dailyLimit <= 0) return

        val spentToday = expenseRepository.getSpentTodayRupees(recordedAtEpochMs)
        val spentBefore = (spentToday - amountRupees).coerceAtLeast(0)
        val percentAfter = ((spentToday * 100f) / dailyLimit).toInt().coerceAtLeast(0)
        val dayKey = dayKeyFor(recordedAtEpochMs)

        if (settings.limitWarning) {
            val warningThresholdPercent = preferenceDataStore.getAlertThresholdPercent()
            val percentBefore = if (dailyLimit > 0) ((spentBefore * 100f) / dailyLimit).toInt() else 0
            val crossedWarning =
                percentBefore < warningThresholdPercent && percentAfter >= warningThresholdPercent
            val alreadySent = preferenceDataStore.getLimitWarningSentDay() == dayKey
            if (crossedWarning && !alreadySent) {
                val remaining = (dailyLimit - spentToday).coerceAtLeast(0)
                alertNotifier.showLimitWarning(
                    percentUsed = percentAfter,
                    limitRupees = dailyLimit,
                    remainingRupees = remaining,
                    sound = settings.sound,
                )
                preferenceDataStore.setLimitWarningSentDay(dayKey)
            }
        }

        if (settings.limitExceeded) {
            val crossedLimit = spentBefore < dailyLimit && spentToday >= dailyLimit
            val alreadySent = preferenceDataStore.getLimitExceededSentDay() == dayKey
            if (crossedLimit && !alreadySent) {
                alertNotifier.showLimitExceeded(
                    overByRupees = spentToday - dailyLimit,
                    limitRupees = dailyLimit,
                    sound = settings.sound,
                )
                preferenceDataStore.setLimitExceededSentDay(dayKey)
            }
        }
    }

    suspend fun sendDailySummaryIfEnabled() {
        val settings = settingsRepository.observeSettings().first()
        if (!settings.dailySummary) return
        if (QuietHoursHelper.isMuted(settings)) return

        val now = System.currentTimeMillis()
        val spent = expenseRepository.getSpentTodayRupees(now)
        val count = expenseRepository.getTransactionCountToday(now)

        alertNotifier.ensureChannels(settings.sound)
        alertNotifier.showDailySummary(
            spentRupees = spent,
            transactionCount = count,
            sound = settings.sound,
        )
    }

    private fun dayKeyFor(epochMs: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = epochMs }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    companion object {
        const val LARGE_TRANSACTION_THRESHOLD_RUPEES = 500
    }
}
