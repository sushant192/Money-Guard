package com.example.moneyguard.core.notifications.alerts

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuietHoursHelperTest {

    @Test
    fun overnightWindow_mutesLateNight() {
        assertTrue(QuietHoursHelper.isInQuietHours(nowMinutes = 23 * 60, startMinutes = 22 * 60, endMinutes = 7 * 60))
        assertTrue(QuietHoursHelper.isInQuietHours(nowMinutes = 6 * 60, startMinutes = 22 * 60, endMinutes = 7 * 60))
        assertFalse(QuietHoursHelper.isInQuietHours(nowMinutes = 12 * 60, startMinutes = 22 * 60, endMinutes = 7 * 60))
    }
}
