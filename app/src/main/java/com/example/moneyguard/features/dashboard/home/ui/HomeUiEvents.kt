package com.example.moneyguard.features.dashboard.home.ui

import android.content.Context

interface HomeUiEvents {

    fun onTabSelected(tab: DashboardTab)

    fun onSeeAllExpensesClick()

    fun onFabClick()

    fun onDismissAddExpenseSheet()

    fun onSaveManualExpense(
        amountRupees: Int,
        title: String,
        note: String,
        category: ExpenseIconStyle,
    )

    fun onAlertThresholdSelect(threshold: AlertThreshold)

    fun onGrantNotificationAccessClick(activityContext: Context)

    fun onLogoutClick()
}
