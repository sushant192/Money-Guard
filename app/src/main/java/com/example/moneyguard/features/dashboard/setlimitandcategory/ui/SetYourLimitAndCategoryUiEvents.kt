package com.example.moneyguard.features.dashboard.setlimitandcategory.ui

interface SetYourLimitAndCategoryUiEvents {

    /** Slider drag — receives the new daily limit (already snapped to int). */
    fun onLimitChange(value: Int)

    /** Tapping a category chip toggles its selection. */
    fun onCategoryToggle(category: Category)

    /**
     * Primary CTA at the bottom of the screen. On the limit step it advances
     * to the categories step; on the categories step it finishes onboarding.
     */
    fun onContinueClick()

    /**
     * Secondary CTA, only shown on the categories step — takes the user back
     * to the limit step on the same screen.
     */
    fun onBackClick()
}
