package com.example.moneyguard.features.auth.login.ui

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.moneyguard.R
import com.example.moneyguard.core.arch.BaseComposeViewModel
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.core.validation.Validators
import com.example.moneyguard.features.auth.data.GoogleSignInHelper
import com.example.moneyguard.features.auth.domain.model.AuthError
import com.example.moneyguard.features.auth.domain.model.AuthErrorMessage
import com.example.moneyguard.features.auth.domain.model.AuthException
import com.example.moneyguard.features.auth.domain.model.toMessage
import com.example.moneyguard.features.auth.domain.model.unknownAuthErrorMessage
import com.example.moneyguard.features.auth.domain.usecase.LoginWithEmailUseCase
import com.example.moneyguard.features.auth.domain.usecase.SignInWithGoogleUseCase
import com.example.moneyguard.features.dashboard.setlimitandcategory.domain.usecase.HasCompletedBudgetSetupUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Named

@KoinViewModel
class LoginViewModel(
    @Named("AppNavigator") private val navigator: Navigator,
    private val loginWithEmail: LoginWithEmailUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val googleSignInHelper: GoogleSignInHelper,
    private val hasCompletedBudgetSetup: HasCompletedBudgetSetupUseCase,
) : BaseComposeViewModel<LoginUiState>(),
    LoginUiEvents {

    private val _uiState = MutableStateFlow(LoginUiState.Initial)
    override val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /** One-shot stream of (title, message) pairs surfaced as a custom toast. */
    private val _errorEvents = Channel<AuthErrorMessage>(Channel.BUFFERED)
    val errorEvents: Flow<AuthErrorMessage> = _errorEvents.receiveAsFlow()

    override fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    override fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    override fun onLoginClick() {
        val current = _uiState.value
        if (current.isLoading) return

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

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            loginWithEmail(current.email, current.password)
                .onSuccess { navigateToDashboard() }
                .onFailure { throwable -> handleAuthFailure(throwable) }
        }
    }

    override fun onForgotPasswordClick() {
        // Forgot-password flow is deferred — see firebase-auth-system plan.
    }

    override fun onContinueWithGoogleClick(activityContext: Context) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            googleSignInHelper.fetchIdToken(activityContext)
                .onSuccess { idToken ->
                    signInWithGoogle(idToken)
                        .onSuccess { navigateToDashboard() }
                        .onFailure { handleAuthFailure(it) }
                }
                .onFailure { handleAuthFailure(it) }
        }
    }

    override fun onSignUpClick() {
        viewModelScope.launch {
            navigator.navigate(Destination.SignUp)
        }
    }

    private suspend fun navigateToDashboard() {
        val target = if (hasCompletedBudgetSetup()) {
            Destination.Home
        } else {
            Destination.DashboardGraph
        }
        navigator.navigate(target) {
            popUpTo(Destination.AuthGraph) { inclusive = true }
            launchSingleTop = true
        }
    }

    private fun handleAuthFailure(throwable: Throwable) {
        val authError = (throwable as? AuthException)?.error
        _uiState.update { it.copy(isLoading = false) }
        // Silently swallow user-driven cancellations of the Google sheet —
        // the user knows what they did, we don't need to show anything.
        if (authError == AuthError.GoogleCancelled) return
        val msg = authError?.toMessage() ?: unknownAuthErrorMessage()
        _errorEvents.trySend(msg)
    }
}
