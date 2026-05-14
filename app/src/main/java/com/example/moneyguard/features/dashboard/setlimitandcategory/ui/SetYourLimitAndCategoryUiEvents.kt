package com.example.moneyguard.features.dashboard.setlimitandcategory.ui

import android.content.Context

interface SetYourLimitAndCategoryUiEvents {

    /** Slider drag — receives the new daily limit (already snapped to int). */
    fun onLimitChange(value: Int)

    /** Tapping a category chip toggles its selection. */
    fun onCategoryToggle(category: Category)

    /**
     * Primary CTA at the bottom of the screen. On the limit step it advances
     * to the categories step; on the categories step it advances to the
     * notification-access step.
     */
    fun onContinueClick()

    /**
     * Secondary CTA, only shown on the categories step — takes the user back
     * to the limit step on the same screen.
     */
    fun onBackClick()

    /** Opens Android's Notification Access settings screen. */
    fun onOpenNotificationSettingsClick(activityContext: Context)

    /** Finalises onboarding (used for both "All done" and "I'll do it later"). */
    fun onFinishClick()
}
