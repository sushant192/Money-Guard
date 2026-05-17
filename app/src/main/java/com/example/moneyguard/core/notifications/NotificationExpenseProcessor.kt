package com.example.moneyguard.core.notifications

import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.moneyguard.core.notifications.parser.PaymentNotificationParser
import com.example.moneyguard.features.dashboard.home.domain.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

/**
 * Runs on a background coroutine when the system posts a notification. Parses
 * debit alerts and persists them — works while the app process is alive or
 * after a cold start triggered by [MoneyGuardNotificationListenerService].
 */
@Single
class NotificationExpenseProcessor(
    private val expenseRepository: ExpenseRepository,
    private val parser: PaymentNotificationParser,
    @Named("IODispatcher") private val ioDispatcher: CoroutineDispatcher,
) {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    fun onNotificationPosted(sbn: StatusBarNotification) {
        scope.launch {
            process(sbn)
        }
    }

    private suspend fun process(sbn: StatusBarNotification) {
        val packageName = sbn.packageName ?: return
        if (packageName == OWN_PACKAGE) return

        val combinedText = NotificationTextExtractor.extract(sbn.notification)
        if (combinedText.isBlank()) return

        val parsed = parser.parse(packageName, combinedText) ?: return

        val inserted =
            expenseRepository.insertNotificationExpense(
                sourceKey = sbn.key,
                title = parsed.title,
                amountRupees = parsed.amountRupees,
                note = parsed.note,
                category = parsed.category,
                paymentSource = parsed.paymentSource,
                createdAtEpochMs = sbn.postTime,
            )

        if (inserted) {
            Log.i(
                TAG,
                "Logged debit ₹${parsed.amountRupees} · ${parsed.title} ($packageName)",
            )
        }
    }

    companion object {
        private const val TAG = "MoneyGuardNotifProc"
        private const val OWN_PACKAGE = "com.example.moneyguard"
    }
}
