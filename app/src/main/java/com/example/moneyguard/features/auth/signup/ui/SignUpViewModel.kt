package com.example.moneyguard.features.auth.signup.ui

import androidx.lifecycle.viewModelScope
import com.example.moneyguard.R
import com.example.moneyguard.core.arch.BaseComposeViewModel
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.Navigator
import com.example.moneyguard.core.validation.Validators
import com.example.moneyguard.features.auth.domain.model.AuthErrorMessage
import com.example.moneyguard.features.auth.domain.model.AuthException
import com.example.moneyguard.features.auth.domain.model.toMessage
import com.example.moneyguard.features.auth.domain.model.unknownAuthErrorMessage
import com.example.moneyguard.features.auth.domain.usecase.SignUpWithEmailUseCase
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
class SignUpViewModel(
    @Named("AppNavigator") private val navigator: Navigator,
    private val signUpWithEmail: SignUpWithEmailUseCase,
) : BaseComposeViewModel<SignUpUiState>(),
    SignUpUiEvents {

    private val _uiState = MutableStateFlow(SignUpUiState.Initial)
    override val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    /**
     * One-shot stream of (title, message) pairs surfaced as a custom toast.
     * Collected by the screen via [com.example.moneyguard.core.arch.ObserveAsEvents].
     */
    private val _errorEvents = Channel<AuthErrorMessage>(Channel.BUFFERED)
    val errorEvents: Flow<AuthErrorMessage> = _errorEvents.receiveAsFlow()

    override fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null) }
    }

    override fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    override fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                passwordError = null,
                // If the user fixes the password, also clear a stale "mismatch"
                // on confirm — they'll re-validate on submit anyway.
                confirmPasswordError = null,
            )
        }
    }

    override fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    override fun onCreateAccountClick() {
        val current = _uiState.value
        if (current.isLoading) return

        val errors = validate(current)
        if (errors.hasAny) {
            _uiState.update {
                it.copy(
                    fullNameError = errors.fullName,
                    emailError = errors.email,
                    passwordError = errors.password,
                    confirmPasswordError = errors.confirmPassword,
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = signUpWithEmail(
                email = current.email,
                password = current.password,
                displayName = current.fullName,
            )
            result.onSuccess {
                navigator.navigate(Destination.DashboardGraph) {
                    popUpTo(Destination.AuthGraph) { inclusive = true }
                    launchSingleTop = true
                }
                // No need to clear isLoading — the screen is leaving anyway.
            }.onFailure { throwable ->
                val authError = (throwable as? AuthException)?.error
                _uiState.update { it.copy(isLoading = false) }
                val msg = authError?.toMessage() ?: unknownAuthErrorMessage()
                _errorEvents.trySend(msg)
            }
        }
    }

    override fun onLoginClick() {
        viewModelScope.launch {
            navigator.navigate(Destination.Login)
        }
    }

    private fun validate(state: SignUpUiState): FormErrors {
        val nameError = when {
            !Validators.isNotBlank(state.fullName) -> R.string.signup_error_full_name_required
            else -> null
        }

        val emailError = when {
            !Validators.isNotBlank(state.email) -> R.string.signup_error_email_required
            !Validators.isValidEmail(state.email) -> R.string.signup_error_email_invalid
            else -> null
        }

        val passwordError = when {
            !Validators.isNotBlank(state.password) -> R.string.signup_error_password_required
            !Validators.isValidPassword(state.password) -> R.string.signup_error_password_invalid
            else -> null
        }

        val confirmError = when {
            !Validators.isNotBlank(state.confirmPassword) -> R.string.signup_error_confirm_required
            !Validators.passwordsMatch(state.password, state.confirmPassword) ->
                R.string.signup_error_confirm_mismatch
            else -> null
        }

        return FormErrors(nameError, emailError, passwordError, confirmError)
    }

    private data class FormErrors(
        val fullName: Int?,
        val email: Int?,
        val password: Int?,
        val confirmPassword: Int?,
    ) {
        val hasAny: Boolean
            get() = fullName != null || email != null ||
                password != null || confirmPassword != null
    }
}
