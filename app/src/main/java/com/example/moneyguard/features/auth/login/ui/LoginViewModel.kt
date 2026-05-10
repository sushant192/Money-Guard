package com.example.moneyguard.features.auth.login.ui

import androidx.lifecycle.viewModelScope
import com.example.moneyguard.R
import com.example.moneyguard.core.arch.BaseComposeViewModel
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.core.validation.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Named

@KoinViewModel
class LoginViewModel(
    @Named("AppNavigator") private val navigator: Navigator
) : BaseComposeViewModel<LoginUiState>(),
    LoginUiEvents {

    private val _uiState = MutableStateFlow(LoginUiState.Initial)
    override val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    override fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    override fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    override fun onLoginClick() {
        val current = _uiState.value
        val emailError = when {
            !Validators.isNotBlank(current.email) -> R.string.signup_error_email_required
            !Validators.isValidEmail(current.email) -> R.string.signup_error_email_invalid
            else -> null
        }
        val passwordError = when {
            !Validators.isNotBlank(current.password) -> R.string.signup_error_password_required
            else -> null
        }

        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(emailError = emailError, passwordError = passwordError)
            }
            return
        }

        // No real auth yet — treat passing validation as "logged in".
        // Returning users land directly on Home (skipping the limit/category
        // onboarding, which is sign-up-only); the entire auth graph is popped
        // so the user can't swipe back into login.
        viewModelScope.launch {
            navigator.navigate(Destination.Home) {
                popUpTo(Destination.AuthGraph) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    override fun onForgotPasswordClick() {
        // No-op for now. Will navigate to a "Forgot password" flow once that
        // screen is implemented.
    }

    override fun onContinueWithGoogleClick() {
        // No-op for now. Google sign-in wiring lands once we add the auth client.
    }

    override fun onSignUpClick() {
        viewModelScope.launch {
            navigator.navigate(Destination.SignUp)
        }
    }
}
