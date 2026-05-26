package com.example.moneyguard.core.notifications

import android.app.Notification
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationPostedFilterTest {

    @Test
    fun groupSummary_isIgnored() {
        assertFalse(
            NotificationPostedFilter.shouldProcess(
                notificationFlags = Notification.FLAG_GROUP_SUMMARY,
                text = "Rs.1600 debited from your account",
            ),
        )
    }

    @Test
    fun inProgressOngoing_isIgnored() {
        assertFalse(
            NotificationPostedFilter.shouldProcess(
                notificationFlags = Notification.FLAG_ONGOING_EVENT,
                text = "Payment of Rs 1600 is processing, please wait",
            ),
        )
    }

    @Test
    fun completedDebit_isProcessed() {
        assertTrue(
            NotificationPostedFilter.shouldProcess(
                notificationFlags = 0,
                text = "Rs.1600 debited from your account on 16-May-26",
            ),
        )
    }
}
