package com.example.moneyguard.features.auth.signup.ui

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
class SignUpViewModel(
    @Named("AppNavigator") private val navigator: Navigator
) : BaseComposeViewModel<SignUpUiState>(),
    SignUpUiEvents {

    private val _uiState = MutableStateFlow(SignUpUiState.Initial)
    override val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

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
                confirmPasswordError = null
            )
        }
    }

    override fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    override fun onCreateAccountClick() {
        val current = _uiState.value
        val errors = validate(current)
        if (errors.hasAny) {
            _uiState.update {
                it.copy(
                    fullNameError = errors.fullName,
                    emailError = errors.email,
                    passwordError = errors.password,
                    confirmPasswordError = errors.confirmPassword
                )
            }
            return
        }
        // No-op for now. Wiring to a real sign-up use case will land in the
        // next iteration once the data + domain layers are in place.
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
        val confirmPassword: Int?
    ) {
        val hasAny: Boolean
            get() = fullName != null || email != null ||
                password != null || confirmPassword != null
    }
}
