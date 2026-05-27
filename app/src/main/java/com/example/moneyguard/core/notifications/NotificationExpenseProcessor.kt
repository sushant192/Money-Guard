package com.example.moneyguard.core.notifications

import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.moneyguard.core.notifications.alerts.SpendAlertCoordinator
import com.example.moneyguard.core.notifications.parser.PaymentNotificationParser
import com.example.moneyguard.features.dashboard.home.domain.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

/**
 * Runs on a background coroutine when the system posts a notification. Parses
 * debit alerts and persists them — works while the app process is alive or
 * after a cold start triggered by [MoneyGuardNotificationListenerService].
 *
 * A single visible alert (e.g. one bank email on the shade) can still invoke
 * [onNotificationPosted] more than once when Android updates or groups it.
 */
@Single
class NotificationExpenseProcessor(
    private val expenseRepository: ExpenseRepository,
    private val parser: PaymentNotificationParser,
    private val spendAlertCoordinator: SpendAlertCoordinator,
    @Named("IODispatcher") private val ioDispatcher: CoroutineDispatcher,
) {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val processMutex = Mutex()

    fun onNotificationPosted(sbn: StatusBarNotification) {
        if (!NotificationPostedFilter.shouldProcess(sbn)) {
            Log.d(TAG, "Ignored notification post (${sbn.packageName}, key=${sbn.key})")
            return
        }
        scope.launch {
            processMutex.withLock {
                process(sbn)
            }
        }
    }

    private suspend fun process(sbn: StatusBarNotification) {
        val packageName = sbn.packageName ?: return
        if (packageName == OWN_PACKAGE) return

        val combinedText = NotificationTextExtractor.extract(sbn.notification)
        if (combinedText.isBlank()) return

        val parsed = parser.parse(packageName, combinedText) ?: return

        val sourceKey =
            NotificationDedupeKey.build(
                packageName = packageName,
                parsed = parsed,
                postTimeEpochMs = sbn.postTime,
            )

        val inserted =
            expenseRepository.insertNotificationExpense(
                sourceKey = sourceKey,
                listenerNotificationKey = sbn.key,
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
                "Logged debit ₹${parsed.amountRupees} · ${parsed.title} ($packageName, key=${sbn.key})",
            )
            spendAlertCoordinator.onExpenseRecorded(
                amountRupees = parsed.amountRupees,
                merchantTitle = parsed.title,
                recordedAtEpochMs = sbn.postTime,
            )
        } else {
            Log.d(
                TAG,
                "Skipped duplicate debit ₹${parsed.amountRupees} · ${parsed.title} ($packageName, key=${sbn.key})",
            )
        }
    }

    companion object {
        private const val TAG = "MoneyGuardNotifProc"
        private const val OWN_PACKAGE = "com.example.moneyguard"
    }
}
