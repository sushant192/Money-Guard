package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.runtime.Immutable
import com.example.moneyguard.core.arch.UiState

@Immutable
data class HomeUiState(
    val selectedTab: DashboardTab,
    /** "Good morning," / "Good afternoon," / "Good evening," */
    val greetingPrefix: String,
    val userName: String,
    val spentTodayRupees: Int,
    val dailyLimitRupees: Int,
    /** 0–100 for progress bar */
    val budgetUsedPercent: Int,
    val remainingRupees: Int,
    val transactionCount: Int,
    val savedRupees: Int,
    val todayExpenses: List<ExpenseItemUi>,
    val isLoading: Boolean,
) : UiState {
    companion object {
        val Initial = HomeUiState(
            selectedTab = DashboardTab.Home,
            greetingPrefix = "Good morning,",
            userName = "Rahul",
            spentTodayRupees = 950,
            dailyLimitRupees = 1500,
            budgetUsedPercent = 63,
            remainingRupees = 550,
            transactionCount = 3,
            savedRupees = 200,
            todayExpenses = sampleExpenses(),
            isLoading = false,
        )

        fun sampleExpenses(): List<ExpenseItemUi> = listOf(
            ExpenseItemUi(
                title = "Netflix",
                metaLine = "Entertainment · 8:30 AM",
                amountRupees = 500,
                paymentLabel = "Monthly",
                iconStyle = ExpenseIconStyle.Entertainment,
            ),
            ExpenseItemUi(
                title = "UPI to Aryan",
                metaLine = "Transfer · 11:15 AM",
                amountRupees = 50,
                paymentLabel = "UPI",
                iconStyle = ExpenseIconStyle.Transfer,
            ),
            ExpenseItemUi(
                title = "McDonald's",
                metaLine = "Food · 1:45 PM",
                amountRupees = 400,
                paymentLabel = "Card",
                iconStyle = ExpenseIconStyle.Food,
            ),
        )
    }
}
