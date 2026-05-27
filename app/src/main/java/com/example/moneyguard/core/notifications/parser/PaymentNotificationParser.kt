package com.example.moneyguard.core.notifications.parser

import com.example.moneyguard.core.notifications.model.ParsedPaymentNotification
import com.example.moneyguard.features.dashboard.home.ui.ExpenseIconStyle
import kotlin.math.roundToInt

/**
 * Parses combined notification title + body text from payment apps and banks.
 * Only returns a result for **debit** (money-out) alerts — credits are ignored.
 */
import org.koin.core.annotation.Single

@Single
class PaymentNotificationParser {

    fun parse(packageName: String, combinedText: String): ParsedPaymentNotification? {
        val text = combinedText.trim()
        if (text.length < 8) return null
        if (isNoiseNotification(text)) return null
        if (isPromotionalNotification(text)) return null
        if (isCreditNotification(text, packageName)) return null

        val amountRupees = extractAmountRupees(text) ?: return null
        if (!isDebitNotification(text, packageName)) return null

        val merchant = extractMerchantTitle(text) ?: "Payment"
        val title = formatDisplayTitle(merchant)
        val paymentSource = resolvePaymentLabel(text, packageName)
        val category = inferCategory(text, merchant)
        val transactionRef = extractTransactionRef(text)
        val note = transactionRef.orEmpty()

        return ParsedPaymentNotification(
            amountRupees = amountRupees,
            title = title,
            note = note,
            category = category,
            paymentSource = paymentSource,
            transactionRef = transactionRef,
        )
    }

    private fun isNoiseNotification(text: String): Boolean {
        val lower = text.lowercase()
        return SKIP_KEYWORDS.any { lower.contains(it) }
    }

    /**
     * Marketing, retail promos, and reward-credit alerts — not real account movements.
     * Catches Gmail/Google Store style copy that mentions amounts and words like "purchase".
     */
    private fun isPromotionalNotification(text: String): Boolean {
        val lower = text.lowercase()
        if (PROMO_KEYWORDS.any { lower.contains(it) }) return true
        return PROMO_PATTERNS.any { it.containsMatchIn(lower) }
    }

    private fun isCreditNotification(text: String, packageName: String): Boolean {
        val lower = text.lowercase()
        // "Credit card" spend alerts are debits, not incoming credits.
        if (lower.contains("credit card") && isDebitNotification(text, packageName)) {
            return false
        }
        // Promotional / loyalty credits are not bank deposits.
        if (PROMOTIONAL_CREDIT_PHRASES.any { lower.contains(it) }) return true
        return CREDIT_PATTERNS.any { it.containsMatchIn(lower) }
    }

    private fun isDebitNotification(text: String, packageName: String): Boolean {
        val lower = text.lowercase()
        if (STRONG_DEBIT_PATTERNS.any { it.containsMatchIn(lower) }) return true

        // GPay / some wallets: "You spent ₹…" without the word "debited".
        if (SPEND_PATTERNS.any { it.containsMatchIn(lower) } && extractAmountRupees(text) != null) {
            return true
        }

        // Known payment apps: amount + outward txn wording is enough.
        if (PaymentAppPackages.looksLikePaymentApp(packageName)) {
            return OUTWARD_TXN_HINTS.any { lower.contains(it) }
        }

        // Gmail, shopping, news, etc. — ignore weak cues like "purchase" in promos.
        return false
    }

    internal fun extractAmountRupees(text: String): Int? {
        for (pattern in AMOUNT_PATTERNS) {
            val match = pattern.find(text) ?: continue
            val raw = match.groupValues[1].replace(",", "").trim()
            val value = raw.toDoubleOrNull() ?: continue
            if (value <= 0 || value > MAX_AMOUNT_RUPEES) continue
            return value.roundToInt().coerceAtLeast(1)
        }
        return null
    }

    internal fun extractMerchantTitle(text: String): String? {
        for (pattern in MERCHANT_PATTERNS) {
            val match = pattern.find(text) ?: continue
            val candidate = match.groupValues[1].trim()
            if (candidate.length in 2..80 && !candidate.looksLikeAmountFragment()) {
                return candidate.cleanMerchantTitle()
            }
        }
        return null
    }

    private fun extractTransactionRef(text: String): String? =
        TRANSACTION_REF_PATTERN.find(text)?.groupValues?.get(1)?.trim()?.takeIf { it.isNotEmpty() }

    private fun formatDisplayTitle(merchant: String): String {
        val name = merchant.cleanMerchantTitle()
        return if (name.equals("Payment", ignoreCase = true)) "Payment" else name
    }

    private fun resolvePaymentLabel(text: String, packageName: String): String =
        when {
            text.contains("credit card", ignoreCase = true) -> "Card"
            shouldUseUpiPresentation(text, merchant = "", packageName) -> "UPI"
            else -> "UPI"
        }

    private fun shouldUseUpiPresentation(
        text: String,
        merchant: String,
        packageName: String,
    ): Boolean {
        if (text.contains("credit card", ignoreCase = true)) return false
        val lower = text.lowercase()
        return lower.contains("upi") ||
            lower.contains("vpa") ||
            merchant.contains("@") ||
            PaymentAppPackages.looksLikePaymentApp(packageName)
    }

    internal fun inferCategory(text: String, merchantTitle: String = ""): ExpenseIconStyle {
        val lower = "${text.lowercase()} ${merchantTitle.lowercase()}"
        return when {
            FOOD_KEYWORDS.any { lower.contains(it) } -> ExpenseIconStyle.Food
            ENTERTAINMENT_KEYWORDS.any { lower.contains(it) } -> ExpenseIconStyle.Entertainment
            TRAVEL_KEYWORDS.any { lower.contains(it) } -> ExpenseIconStyle.Travel
            BILLS_KEYWORDS.any { lower.contains(it) } -> ExpenseIconStyle.Bills
            else -> ExpenseIconStyle.Transfer
        }
    }

    private fun String.looksLikeAmountFragment(): Boolean =
        matches(Regex("""^[\d,.\s₹RsINR]+$""", RegexOption.IGNORE_CASE))

    private fun String.cleanMerchantTitle(): String {
        var cleaned =
            trim()
                .removeSuffix(".")
                .removeSuffix(",")
                .replace(Regex("""\s+"""), " ")
        if (cleaned.startsWith("VPA ", ignoreCase = true)) {
            cleaned = cleaned.removePrefix("VPA ").trim()
        }
        return cleaned.take(80)
    }

    companion object {
        private const val MAX_AMOUNT_RUPEES = 50_000_000

        private val TRANSACTION_REF_PATTERN =
            Regex("""(?i)(?:upi\s+)?transaction\s+reference\s+no\.?\s*:?\s*([A-Za-z0-9]+)""")

        private val AMOUNT_PATTERNS =
            listOf(
                Regex("""₹\s*([\d,]+(?:\.\d{1,2})?)"""),
                Regex("""(?i)rs\.?\s*([\d,]+(?:\.\d{1,2})?)"""),
                Regex("""(?i)inr\s*([\d,]+(?:\.\d{1,2})?)"""),
            )

        private val MERCHANT_PATTERNS =
            listOf(
                Regex("""(?i)upi\s+to\s+(.+?)(?:\s+on|\s+via|\s+using|\.|$)"""),
                Regex("""(?i)(?:you\s+)?sent\s+(?:₹|inr|rs\.?)\s*[\d,.]+\s+to\s+(.+?)(?:\s+on|\s+via|\.|$)"""),
                Regex("""(?i)paid\s+(?:₹|inr|rs\.?)\s*[\d,.]+\s+to\s+(.+?)(?:\s+on|\s+via|\.|$)"""),
                // HDFC / banks: debited … towards VPA x@y (Merchant Name) on DD-MM-YY
                Regex("""(?i)towards\s+vpa\s+\S+\s+\(([^)]+)\)"""),
                // Merchant in parentheses immediately before a date
                Regex("""\(([^)()]{2,80})\)\s+on\s+\d{1,2}[-/]\d{1,2}[-/]\d{2,4}"""),
                Regex("""(?i)spent\s+₹?[\d,.]+\s+at\s+(.+?)(?:\s+on|\s+using|\s+via|\.|$)"""),
                Regex("""(?i)spent\s+(?:inr|rs\.?)\s*[\d,.]+\s+at\s+(.+?)(?:\s+on|\s+using|\.|$)"""),
                Regex("""(?i)paid\s+₹?[\d,.]+\s+to\s+(.+?)(?:\s+on|\s+via|\.|$)"""),
                Regex("""(?i)paid\s+(?:inr|rs\.?)\s*[\d,.]+\s+to\s+(.+?)(?:\s+on|\s+via|\.|$)"""),
                Regex("""(?i)to\s+([A-Za-z0-9][A-Za-z0-9 .&'-]{1,60}?)\s+on\s+\d"""),
                Regex("""(?i)at\s+([A-Za-z0-9][A-Za-z0-9 .&'-]{1,60}?)\s+on\s+\d"""),
                Regex("""(?i)txn\s+at\s+(.+?)(?:\s+on|\s+ref|\.|$)"""),
                Regex("""(?i)trf\s+to\s+(.+?)(?:\s+on|\s+ref|\.|$)"""),
            )

        private val CREDIT_PATTERNS =
            listOf(
                Regex("""\bcredited\b"""),
                Regex("""\bcredit\s+of\b"""),
                Regex("""\bhas\s+been\s+credited\b"""),
                Regex("""\breceived\s+(?:inr|rs\.?|₹)"""),
                Regex("""\breceived\s+from\b"""),
                Regex("""\bcashback\s+credited\b"""),
                Regex("""\brefund\s+(?:has\s+been\s+)?credited\b"""),
                Regex("""\bsalary\s+(?:has\s+been\s+)?credited\b"""),
                Regex("""\binterest\s+credited\b"""),
                Regex("""\bdeposited\s+to\s+your\b"""),
                Regex("""\bcredit\s+alert\b"""),
            )

        /** Confirmed money-out alerts — safe for banks, wallets, and email-forwarded SMS. */
        private val STRONG_DEBIT_PATTERNS =
            listOf(
                Regex("""\bdebited\b"""),
                Regex("""\bdebit\s+alert\b"""),
                Regex("""\bdebit\s+of\b"""),
                Regex("""\bhas\s+been\s+debited\b"""),
                Regex("""\bwithdrawn\b"""),
                Regex("""\bpayment\s+made\b"""),
                Regex("""\bupi\s+txn\b.*\bdebited\b"""),
                Regex("""(?i)\bcredit\s+card\s+was\s+charged\b"""),
                Regex("""(?i)\bwas\s+charged\s+(?:rs|inr|₹)"""),
                Regex("""(?i)\bcharged\s+(?:rs\.?|inr|₹)\s*[\d,]"""),
                Regex("""(?i)\bpaid\s+(?:rs\.?|inr|₹)\s*[\d,]"""),
                Regex("""(?i)\byou\s+sent\s+(?:₹|inr|rs)"""),
            )

        private val PROMOTIONAL_CREDIT_PHRASES =
            listOf(
                "store credit",
                "shopping credit",
                "bonus credit",
                "loyalty credit",
                "reward credit",
                "wallet credit when you",
            )

        private val PROMO_KEYWORDS =
            listOf(
                "get an extra",
                "get up to",
                "get upto",
                "limited time offer",
                "limited-time offer",
                "exclusive offer",
                "special offer",
                "when you buy",
                "when you purchase",
                "shop now",
                "order now",
                "pre-order",
                "preorder",
                "free shipping",
                "trade-in",
                "trade in",
                "upgrade to",
                "eligible for",
                "claim your",
                "deal of the day",
                "flash sale",
                "promo code",
                "coupon code",
                "use code",
                "% off",
                "percent off",
                "discount on",
                "unsubscribe",
                "promotional",
                "no purchase necessary",
                "terms and conditions",
                "t&c apply",
                "learn more about",
                "introducing the",
                "new launch",
                "launch offer",
                "launch day",
                "register now",
                "sign up to get",
                "refer a friend",
                "invite friends",
                "earn up to",
                "stand a chance",
                "you could win",
                "google store",
                "pixel 10",
                "cashback when you",
                "save up to",
                "save upto",
            )

        private val PROMO_PATTERNS =
            listOf(
                Regex("""(?i)\bget\s+(?:an\s+)?extra\s+[\d,₹rs.]"""),
                Regex("""(?i)\b(?:save|earn)\s+(?:up\s+to|upto)\s+[\d,₹rs.]"""),
                Regex("""(?i)\bbuy\s+(?:a|the)\s+\w+.*\b(?:and|to)\s+(?:get|save|earn)\b"""),
                Regex("""(?i)\b(?:offer|sale)\s+(?:ends|valid)\s+"""),
            )

        private val SPEND_PATTERNS =
            listOf(
                Regex("""\byou\s+(?:have\s+)?spent\b"""),
                Regex("""\bspent\s+₹"""),
                Regex("""\bspent\s+(?:inr|rs)"""),
            )

        private val OUTWARD_TXN_HINTS =
            listOf(
                "debited",
                "debit",
                "spent",
                "paid",
                "payment",
                "sent",
                "purchase",
                "withdrawn",
                "txn",
                "transaction",
                "upi",
            )

        private val SKIP_KEYWORDS =
            listOf(
                "otp",
                "one time password",
                "one-time password",
                "verification code",
                "verify your",
                "transaction failed",
                "payment failed",
                "declined",
                "unsuccessful",
                "could not be completed",
                "login attempt",
                "new device",
                "password reset",
                "statement is ready",
                "e-statement",
                "bill reminder",
                "due on",
                "minimum due",
                "pre-approved loan",
                "apply now",
                "click here to",
                "won ",
                "congratulations",
                "cashback offer",
                "marketing",
                "newsletter",
                "sale starts",
                "sale ends",
            )

        private val FOOD_KEYWORDS =
            listOf("swiggy", "zomato", "blinkit", "zepto", "dominos", "restaurant", "cafe", "food")
        private val ENTERTAINMENT_KEYWORDS =
            listOf("netflix", "spotify", "prime video", "hotstar", "bookmyshow", "cinema", "game")
        private val TRAVEL_KEYWORDS =
            listOf(
                "uber",
                "ola",
                "rapido",
                "metro",
                "irctc",
                "railways",
                "railway",
                "indian railways",
                "uts",
                "makemytrip",
                "redbus",
                "fuel",
                "petrol",
                "diesel",
            )
        private val BILLS_KEYWORDS =
            listOf("electricity", "bescom", "recharge", "broadband", "wifi", "insurance", "emi")
    }
}
