package com.example.moneyguard.features.dashboard.home.ui

import android.content.Context

interface HomeUiEvents {

    fun onTabSelected(tab: DashboardTab)

    fun onSeeAllExpensesClick()

    fun onFabClick()

    fun onDismissAddExpenseSheet()

    fun onExpenseClick(expenseId: Long)

    fun onDismissExpenseDetail()

    fun onDeleteExpense(expenseId: Long)

    fun onEditExpenseClick(expenseId: Long)

    fun onSaveManualExpense(
        amountRupees: Int,
        title: String,
        note: String,
        category: ExpenseIconStyle,
    )

    fun onAlertThresholdSelect(threshold: AlertThreshold)

    fun onDismissCustomAlertThresholdDialog()

    fun onCustomAlertThresholdConfirm(percent: Int)

    fun onGrantNotificationAccessClick(activityContext: Context)

    /** Called after the POST_NOTIFICATIONS system dialog is dismissed. */
    fun onPostNotificationPromptHandled(granted: Boolean)

    fun onNotificationsClick()

    fun onLogoutClick()
}
