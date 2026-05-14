package com.example.moneyguard.features.auth.login.ui

import android.content.Context

interface LoginUiEvents {

    fun onEmailChange(value: String)

    fun onPasswordChange(value: String)

    fun onLoginClick()

    fun onForgotPasswordClick()

    /**
     * Activity context is forwarded to Credential Manager which needs an
     * Activity to host its bottom-sheet UI. The screen passes
     * `LocalContext.current` (which is the Activity in normal usage).
     */
    fun onContinueWithGoogleClick(activityContext: Context)

    fun onSignUpClick()
}
