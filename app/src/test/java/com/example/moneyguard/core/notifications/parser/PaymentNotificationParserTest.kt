package com.example.moneyguard.core.notifications.parser

import com.example.moneyguard.features.dashboard.home.ui.ExpenseIconStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PaymentNotificationParserTest {

    private lateinit var parser: PaymentNotificationParser

    @Before
    fun setUp() {
        parser = PaymentNotificationParser()
    }

    @Test
    fun gpaySpend_isParsedAsDebit() {
        val result =
            parser.parse(
                GPay,
                "You spent ₹499.00 at Swiggy on 16 May",
            )
        assertNotNull(result)
        assertEquals(499, result!!.amountRupees)
        assertEquals("UPI to Swiggy", result.title)
        assertEquals("UPI", result.paymentSource)
        assertEquals(ExpenseIconStyle.Food, result.category)
    }

    @Test
    fun phonePeDebit_isParsed() {
        val result =
            parser.parse(
                PhonePe,
                "Rs.1,250.00 debited from HDFC Bank XX1234 on 16-May-26",
            )
        assertNotNull(result)
        assertEquals(1250, result!!.amountRupees)
    }

    @Test
    fun bankCredit_isRejected() {
        val result =
            parser.parse(
                Hdfc,
                "Rs.5,000.00 credited to your A/c **1234 on 16-May-26",
            )
        assertNull(result)
    }

    @Test
    fun salaryCredit_isRejected() {
        val result =
            parser.parse(
                Hdfc,
                "Salary of INR 75,000 has been credited to A/c XX1234",
            )
        assertNull(result)
    }

    @Test
    fun otp_isRejected() {
        val result =
            parser.parse(
                PhonePe,
                "OTP 482910 is your One Time Password for transaction of Rs 500",
            )
        assertNull(result)
    }

    @Test
    fun paytmPaidToMerchant_isParsed() {
        val result =
            parser.parse(
                Paytm,
                "Paid Rs 89 to Zomato via Paytm UPI on 16 May",
            )
        assertNotNull(result)
        assertEquals(89, result!!.amountRupees)
        assertEquals("UPI to Zomato", result.title)
        assertEquals("UPI", result.paymentSource)
    }

    @Test
    fun gpaySentToPerson_formatsAsUpiTo() {
        val result =
            parser.parse(
                GPay,
                "You sent ₹50 to Aryan via Google Pay UPI on 17 May at 11:15 AM",
            )
        assertNotNull(result)
        assertEquals(50, result!!.amountRupees)
        assertEquals("UPI to Aryan", result.title)
        assertEquals("UPI", result.paymentSource)
        assertEquals(ExpenseIconStyle.Transfer, result.category)
    }

    @Test
    fun failedTransaction_isRejected() {
        val result =
            parser.parse(
                GPay,
                "Transaction failed for Rs 200 at Amazon",
            )
        assertNull(result)
    }

    @Test
    fun creditCardDebit_isParsed() {
        val result =
            parser.parse(
                Hdfc,
                "Your credit card was charged Rs 2,499.00 at Amazon on 16 May",
            )
        assertNotNull(result)
        assertEquals(2499, result!!.amountRupees)
    }

    @Test
    fun extractAmount_handlesCommasAndDecimals() {
        assertEquals(1235, parser.extractAmountRupees("debited Rs 1,234.56"))
    }

    @Test
    fun hdfcUpiDebit_extractsRailwaysMerchantAndTravelCategory() {
        val body =
            """
            Dear Customer, Greetings from HDFC Bank!
            Rs.29.10 is debited from your account ending 8540 towards VPA bdpg2.iruts@sbi (Indian Railways UTS) on 17-05-26.
            UPI transaction reference no.: 611013698812.
            If you did not authorize this transaction, please report it immediately.
            """.trimIndent()

        val result = parser.parse(Hdfc, body)

        assertNotNull(result)
        assertEquals(29, result!!.amountRupees)
        assertEquals("UPI to Indian Railways UTS", result.title)
        assertEquals("UPI", result.paymentSource)
        assertEquals(ExpenseIconStyle.Travel, result.category)
        assertEquals("611013698812", result.note)
    }

    companion object {
        private const val GPay = "com.google.android.apps.nbu.paisa.user"
        private const val PhonePe = "com.phonepe.app"
        private const val Paytm = "net.one97.paytm"
        private const val Hdfc = "com.snapwork.hdfc"
    }
}
