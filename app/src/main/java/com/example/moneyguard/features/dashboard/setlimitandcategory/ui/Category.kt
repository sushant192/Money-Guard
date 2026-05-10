package com.example.moneyguard.features.dashboard.setlimitandcategory.ui

import androidx.annotation.StringRes
import com.example.moneyguard.R

/**
 * Spend categories shown on the onboarding picker. Order here is the order
 * they appear on screen.
 *
 * This will eventually graduate to `domain/model/Category.kt` and be persisted
 * in Room — for now it lives next to the screen that uses it.
 */
enum class Category(@StringRes val displayName: Int) {
    FOOD(R.string.category_food),
    ENTERTAINMENT(R.string.category_entertainment),
    TRANSFER(R.string.category_transfer),
    TRAVEL(R.string.category_travel),
    BILLS(R.string.category_bills),
    SHOPPING(R.string.category_shopping)
}
