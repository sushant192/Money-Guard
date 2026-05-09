package com.example.moneyguard.features.auth.signup.ui

/**
 * Events the SignUp screen can emit. The ViewModel implements this interface
 * so the composable receives method references on the event sink.
 */
interface SignUpUiEvents {

    fun onFullNameChange(value: String)

    fun onEmailChange(value: String)

    fun onPasswordChange(value: String)

    fun onConfirmPasswordChange(value: String)

    fun onCreateAccountClick()

    fun onLoginClick()
}
