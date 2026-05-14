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
    val userEmail: String,
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
    val hasNotificationAccess: Boolean,
    val isLoading: Boolean,
) : UiState {
    companion object {
        val Initial = HomeUiState(
            selectedTab = DashboardTab.Home,
            greetingPrefix = "Good morning,",
            userName = "Rahul Sharma",
            userEmail = "rahul@gmail.com",
            spentTodayRupees = 0,
            dailyLimitRupees = 1500,
            budgetUsedPercent = 0,
            remainingRupees = 1500,
            transactionCount = 0,
            savedRupees = 0,
            todayExpenses = emptyList(),
            historyGroups = emptyList(),
            monthSpentRupees = 0,
            monthlyBudgetRupees = 45_000,
            categoryBreakdown = emptyList(),
            weekDailyBars = sampleWeekBars(),
            weeklyLimitRupees = 8_000,
            weeklySpentRupees = 0,
            alertThreshold = AlertThreshold.Seventy,
            hasNotificationAccess = true,
            isLoading = false,
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
            DayBarUi(label = "Mon", heightFraction = 0f, kind = BarKind.Past),
            DayBarUi(label = "Tue", heightFraction = 0f, kind = BarKind.Past),
            DayBarUi(label = "Wed", heightFraction = 0f, kind = BarKind.Past),
            DayBarUi(label = "Thu", heightFraction = 0f, kind = BarKind.Past),
            DayBarUi(label = "Fri", heightFraction = 0f, kind = BarKind.Today),
            DayBarUi(label = "Sat", heightFraction = 0f, kind = BarKind.Future),
            DayBarUi(label = "Sun", heightFraction = 0f, kind = BarKind.Future),
        )
    }
}
