package com.example.moneyguard.core.notifications.parser

/**
 * Known UPI / wallet / bank app package names. Used as a weak signal together
 * with debit-shaped notification text — we never ingest based on package alone.
 */
internal object PaymentAppPackages {

  private val PACKAGE_LABELS: Map<String, String> =
      mapOf(
          "com.google.android.apps.nbu.paisa.user" to "GPay",
          "com.google.android.apps.walletnfcrel" to "Google Wallet",
          "net.one97.paytm" to "Paytm",
          "com.phonepe.app" to "PhonePe",
          "in.org.npci.upiapp" to "BHIM",
          "com.amazon.mShop.android.shopping" to "Amazon",
          "com.mobikwik_new" to "Mobikwik",
          "com.freecharge.android" to "Freecharge",
          "com.csam.icici.bank.imobile" to "ICICI Bank",
          "com.snapwork.hdfc" to "HDFC Bank",
          "com.sbi.lotusintouch" to "SBI",
          "com.axis.mobile" to "Axis Bank",
          "com.kotak811" to "Kotak",
          "com.idfcfirstbank.mobile" to "IDFC First",
          "com.yesbank.yesbank" to "Yes Bank",
          "com.indusind.indie" to "IndusInd",
          "com.rbl.rblmobilebanking" to "RBL",
          "com.bankofbaroda.mconnect" to "Bank of Baroda",
          "com.unionbank.ebanking" to "Union Bank",
          "com.canarabank.mobility" to "Canara Bank",
          "com.pnbindia.online" to "PNB",
      )

  fun labelFor(packageName: String): String =
      PACKAGE_LABELS[packageName]
          ?: packageName.substringAfterLast('.').replaceFirstChar { it.uppercase() }

  fun looksLikePaymentApp(packageName: String): Boolean {
    if (packageName in PACKAGE_LABELS) return true
    val lower = packageName.lowercase()
    return lower.contains("bank") ||
        lower.contains("upi") ||
        lower.contains("paisa") ||
        lower.contains("paytm") ||
        lower.contains("phonepe") ||
        lower.contains("wallet") ||
        lower.contains("imobile") ||
        lower.contains("mbanking")
  }
}
