package com.example.moneyguard.features.auth.signup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneyguard.R
import com.example.moneyguard.core.arch.BaseScreen
import com.example.moneyguard.core.arch.ObserveAsEvents
import com.example.moneyguard.core.validation.Validators
import com.example.moneyguard.ui.components.AppFilledTextField
import com.example.moneyguard.ui.components.AuthErrorToast
import com.example.moneyguard.ui.components.AuthErrorVisuals
import com.example.moneyguard.ui.components.PasswordVisibilityToggle
import com.example.moneyguard.ui.theme.BrandBlueMid
import com.example.moneyguard.ui.theme.MoneyGuardTheme
import com.example.moneyguard.ui.theme.MutedText
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(viewModel: SignUpViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(flow = viewModel.errorEvents) { msg ->
        scope.launch {
            snackbarHostState.showSnackbar(
                AuthErrorVisuals(titleRes = msg.titleRes, messageRes = msg.messageRes),
            )
        }
    }

    Scaffold(
        snackbarHost = {
            // Lift the snackbar above the system nav bar / keyboard since the
            // screen disables Scaffold's window insets.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(bottom = 12.dp),
            ) {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    AuthErrorToast(data = data)
                }
            }
        },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        containerColor = Color.Transparent,
    ) { _ ->
        BaseScreen(viewModel) { state ->
            SignUpUiComponents(
                state = state.value,
                event = viewModel
            )
        }
    }
}

@Composable
private fun SignUpUiComponents(
    state: SignUpUiState,
    event: SignUpUiEvents
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
    ) {
        SignUpHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp)
                .navigationBarsPadding()
        ) {
            AppFilledTextField(
                value = state.fullName,
                onValueChange = event::onFullNameChange,
                label = stringResource(R.string.signup_full_name),
                errorRes = state.fullNameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(Modifier.height(14.dp))

            AppFilledTextField(
                value = state.email,
                onValueChange = event::onEmailChange,
                label = stringResource(R.string.signup_email),
                errorRes = state.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(Modifier.height(14.dp))

            AppFilledTextField(
                value = state.password,
                onValueChange = event::onPasswordChange,
                label = stringResource(R.string.signup_password),
                errorRes = state.passwordError,
                helperRes = R.string.signup_password_helper.takeUnless {
                    Validators.isValidPassword(state.password)
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    PasswordVisibilityToggle(
                        visible = passwordVisible,
                        onToggle = { passwordVisible = !passwordVisible }
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(Modifier.height(14.dp))

            AppFilledTextField(
                value = state.confirmPassword,
                onValueChange = event::onConfirmPasswordChange,
                label = stringResource(R.string.signup_confirm_password),
                errorRes = state.confirmPasswordError,
                visualTransformation = if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    PasswordVisibilityToggle(
                        visible = confirmPasswordVisible,
                        onToggle = { confirmPasswordVisible = !confirmPasswordVisible }
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboard?.hide()
                        focusManager.clearFocus()
                        event.onCreateAccountClick()
                    }
                )
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    keyboard?.hide()
                    focusManager.clearFocus()
                    event.onCreateAccountClick()
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlueMid,
                    contentColor = Color.White,
                    disabledContainerColor = BrandBlueMid.copy(alpha = 0.65f),
                    disabledContentColor = Color.White,
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                if (state.isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.signup_creating_account),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.signup_cta),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = event::onLoginClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = buildLoginPrompt(),
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SignUpHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlueMid)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.signup_header_eyebrow),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = stringResource(R.string.signup_header_title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
        }
    }
}

@Composable
private fun buildLoginPrompt() = buildAnnotatedString {
    withStyle(SpanStyle(color = MutedText)) {
        append(stringResource(R.string.signup_login_prefix))
        append(" ")
    }
    withStyle(
        SpanStyle(
            color = BrandBlueMid,
            fontWeight = FontWeight.SemiBold
        )
    ) {
        append(stringResource(R.string.signup_login_action))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignUpPreview() {
    MoneyGuardTheme {
        SignUpUiComponents(
            state = SignUpUiState(
                fullName = "Rahul Sharma",
                email = "rahul@gmail.com",
                password = "secret123",
                confirmPassword = "secret123",
                fullNameError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                isLoading = false
            ),
            event = object : SignUpUiEvents {
                override fun onFullNameChange(value: String) = Unit
                override fun onEmailChange(value: String) = Unit
                override fun onPasswordChange(value: String) = Unit
                override fun onConfirmPasswordChange(value: String) = Unit
                override fun onCreateAccountClick() = Unit
                override fun onLoginClick() = Unit
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Errors")
@Composable
private fun SignUpErrorPreview() {
    MoneyGuardTheme {
        SignUpUiComponents(
            state = SignUpUiState(
                fullName = "",
                email = "rahul@",
                password = "abc",
                confirmPassword = "xyz",
                fullNameError = R.string.signup_error_full_name_required,
                emailError = R.string.signup_error_email_invalid,
                passwordError = R.string.signup_error_password_invalid,
                confirmPasswordError = R.string.signup_error_confirm_mismatch,
                isLoading = false
            ),
            event = object : SignUpUiEvents {
                override fun onFullNameChange(value: String) = Unit
                override fun onEmailChange(value: String) = Unit
                override fun onPasswordChange(value: String) = Unit
                override fun onConfirmPasswordChange(value: String) = Unit
                override fun onCreateAccountClick() = Unit
                override fun onLoginClick() = Unit
            }
        )
    }
}
