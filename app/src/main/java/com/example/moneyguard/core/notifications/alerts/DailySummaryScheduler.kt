package com.example.moneyguard.core.notifications.alerts

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Schedules a one-shot worker at ~9 PM local time; the worker reschedules the next run.
 */
object DailySummaryScheduler {

    private const val WORK_NAME = "moneyguard_daily_summary"

    fun schedule(context: Context) {
        val delayMs = millisUntilNextRun()
        val request =
            OneTimeWorkRequestBuilder<DailySummaryWorker>()
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .addTag(WORK_NAME)
                .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    internal fun millisUntilNextRun(
        hourOfDay: Int = SUMMARY_HOUR,
        minute: Int = SUMMARY_MINUTE,
        now: Calendar = Calendar.getInstance(),
    ): Long {
        val target =
            (now.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    private const val SUMMARY_HOUR = 21
    private const val SUMMARY_MINUTE = 0
}
