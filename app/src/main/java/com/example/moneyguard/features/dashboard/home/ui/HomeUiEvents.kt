package com.example.moneyguard.features.dashboard.home.ui

interface HomeUiEvents {

    fun onTabSelected(tab: DashboardTab)

    fun onSeeAllExpensesClick()

    fun onFabClick()

    fun onAlertThresholdSelect(threshold: AlertThreshold)

    fun onLogoutClick()
}
