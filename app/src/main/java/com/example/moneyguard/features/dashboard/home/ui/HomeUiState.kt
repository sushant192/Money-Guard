package com.example.moneyguard.features.dashboard.home.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.moneyguard.R
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
    val historyGroups: List<HistoryGroup>,
    // Stats tab data
    val monthSpentRupees: Int,
    val monthlyBudgetRupees: Int,
    val categoryBreakdown: List<CategorySpendUi>,
    val weekDailyBars: List<DayBarUi>,
    // Limits tab
    val weeklyLimitRupees: Int,
    val weeklySpentRupees: Int,
    val alertThreshold: AlertThreshold,
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
            historyGroups = sampleHistoryGroups(),
            monthSpentRupees = 12_450,
            monthlyBudgetRupees = 45_000,
            categoryBreakdown = sampleCategoryBreakdown(),
            weekDailyBars = sampleWeekBars(),
            weeklyLimitRupees = 8_000,
            weeklySpentRupees = 3_500,
            alertThreshold = AlertThreshold.Seventy,
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

        fun sampleHistoryGroups(): List<HistoryGroup> = listOf(
            HistoryGroup(
                title = "Today",
                items = listOf(
                    HistoryItemUi(
                        title = "Netflix",
                        time = "8:30 AM",
                        amountRupees = 500,
                        iconStyle = ExpenseIconStyle.Entertainment,
                    ),
                    HistoryItemUi(
                        title = "UPI to Aryan",
                        time = "11:15 AM",
                        amountRupees = 50,
                        iconStyle = ExpenseIconStyle.Transfer,
                    ),
                    HistoryItemUi(
                        title = "McDonald's",
                        time = "1:45 PM",
                        amountRupees = 400,
                        iconStyle = ExpenseIconStyle.Food,
                    ),
                ),
            ),
            HistoryGroup(
                title = "Yesterday",
                items = listOf(
                    HistoryItemUi(
                        title = "Ola cab",
                        time = "6:20 PM",
                        amountRupees = 180,
                        iconStyle = ExpenseIconStyle.Travel,
                    ),
                    HistoryItemUi(
                        title = "Swiggy order",
                        time = "8:00 PM",
                        amountRupees = 320,
                        iconStyle = ExpenseIconStyle.Food,
                    ),
                ),
            ),
        )

        fun sampleCategoryBreakdown(): List<CategorySpendUi> = listOf(
            CategorySpendUi(
                nameRes = R.string.category_food,
                amountRupees = 4_200,
                color = Color(0xFF2563EB),
            ),
            CategorySpendUi(
                nameRes = R.string.category_entertainment,
                amountRupees = 2_800,
                color = Color(0xFF7C4DFF),
            ),
            CategorySpendUi(
                nameRes = R.string.category_transfer,
                amountRupees = 1_950,
                color = Color(0xFF2E7D32),
            ),
            CategorySpendUi(
                nameRes = R.string.category_travel,
                amountRupees = 1_800,
                color = Color(0xFF8E24AA),
            ),
            CategorySpendUi(
                nameRes = R.string.category_bills,
                amountRupees = 1_700,
                color = Color(0xFFEF6C00),
            ),
        )

        fun sampleWeekBars(): List<DayBarUi> = listOf(
            DayBarUi(label = "Mon", heightFraction = 0.55f, kind = BarKind.Past),
            DayBarUi(label = "Tue", heightFraction = 0.85f, kind = BarKind.Past),
            DayBarUi(label = "Wed", heightFraction = 0.42f, kind = BarKind.Past),
            DayBarUi(label = "Thu", heightFraction = 0.78f, kind = BarKind.Past),
            DayBarUi(label = "Fri", heightFraction = 1.00f, kind = BarKind.Today),
            DayBarUi(label = "Sat", heightFraction = 0.18f, kind = BarKind.Future),
            DayBarUi(label = "Sun", heightFraction = 0.18f, kind = BarKind.Future),
        )
    }
}
