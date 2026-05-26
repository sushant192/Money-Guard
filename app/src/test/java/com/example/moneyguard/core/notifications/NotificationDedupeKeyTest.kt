package com.example.moneyguard.core.notifications

import com.example.moneyguard.core.notifications.model.ParsedPaymentNotification
import com.example.moneyguard.features.dashboard.home.ui.ExpenseIconStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class NotificationDedupeKeyTest {

    @Test
    fun samePaymentWithinTimeBucket_usesSameKey() {
        val parsed =
            sampleParsed(amountRupees = 1600, title = "Merchant", transactionRef = null)
        val keyA = NotificationDedupeKey.build(PKG, parsed, postTimeEpochMs = 1_700_000_000_000L)
        val keyB = NotificationDedupeKey.build(PKG, parsed, postTimeEpochMs = 1_700_000_030_000L)
        assertEquals(keyA, keyB)
    }

    @Test
    fun transactionRef_overridesTimeBucket() {
        val parsed =
            sampleParsed(amountRupees = 1600, title = "A", transactionRef = "611013698812")
        val keyA = NotificationDedupeKey.build(PKG, parsed, postTimeEpochMs = 1_700_000_000_000L)
        val keyB = NotificationDedupeKey.build(PKG, parsed, postTimeEpochMs = 1_700_100_000_000L)
        assertEquals(keyA, keyB)
        assertEquals("notif|$PKG|ref|611013698812", keyA)
    }

    @Test
    fun differentAmounts_produceDifferentKeys() {
        val keyA =
            NotificationDedupeKey.build(
                PKG,
                sampleParsed(1600, "Shop", null),
                1_700_000_000_000L,
            )
        val keyB =
            NotificationDedupeKey.build(
                PKG,
                sampleParsed(1700, "Shop", null),
                1_700_000_000_000L,
            )
        assertNotEquals(keyA, keyB)
    }

    private fun sampleParsed(
        amountRupees: Int,
        title: String,
        transactionRef: String?,
    ) = ParsedPaymentNotification(
        amountRupees = amountRupees,
        title = title,
        note = transactionRef.orEmpty(),
        category = ExpenseIconStyle.Transfer,
        paymentSource = "UPI",
        transactionRef = transactionRef,
    )

    companion object {
        private const val PKG = "com.phonepe.app"
    }
}
