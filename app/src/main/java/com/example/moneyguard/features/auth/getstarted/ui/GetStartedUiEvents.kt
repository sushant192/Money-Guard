package com.example.moneyguard.features.auth.getstarted.ui

/**
 * Events the Get Started screen can emit. The ViewModel implements this
 * interface so the composable receives method references on the event sink.
 */
interface GetStartedUiEvents {

    /** User tapped the primary "Get started" button. */
    fun onGetStartedClick()

    /** User tapped the "Already have an account? Log in" link. */
    fun onLoginClick()
}
