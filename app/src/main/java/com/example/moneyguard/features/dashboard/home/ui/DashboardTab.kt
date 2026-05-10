package com.example.moneyguard.features.dashboard.home.ui

import androidx.annotation.StringRes
import com.example.moneyguard.R

enum class DashboardTab(
    @StringRes val labelRes: Int,
) {
    Home(R.string.tab_home),
    History(R.string.tab_history),
    Stats(R.string.tab_stats),
    Limits(R.string.tab_limits),
}
