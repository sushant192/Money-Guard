package com.example.moneyguard.core.notifications.alerts

import com.example.moneyguard.features.dashboard.notificationsettings.domain.model.NotificationSettings
import java.util.Calendar

internal object QuietHoursHelper {

    fun isMuted(settings: NotificationSettings, now: Calendar = Calendar.getInstance()): Boolean {
        if (!settings.quietHoursEnabled) return false
        val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        return isInQuietHours(
            nowMinutes = nowMinutes,
            startMinutes = settings.quietHoursStartMinutes,
            endMinutes = settings.quietHoursEndMinutes,
        )
    }

    fun isInQuietHours(nowMinutes: Int, startMinutes: Int, endMinutes: Int): Boolean =
        if (startMinutes <= endMinutes) {
            nowMinutes in startMinutes until endMinutes
        } else {
            // Overnight window (e.g. 22:00 → 07:00)
            nowMinutes >= startMinutes || nowMinutes < endMinutes
        }
}
