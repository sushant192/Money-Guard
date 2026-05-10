package com.example.moneyguard.features.auth.login.ui

interface LoginUiEvents {

    fun onEmailChange(value: String)

    fun onPasswordChange(value: String)

    fun onLoginClick()

    fun onForgotPasswordClick()

    fun onContinueWithGoogleClick()

    fun onSignUpClick()
}
