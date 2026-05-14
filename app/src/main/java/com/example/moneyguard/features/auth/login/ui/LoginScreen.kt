package com.example.moneyguard.features.auth.login.ui

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
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
import com.example.moneyguard.ui.components.AppFilledTextField
import com.example.moneyguard.ui.components.AuthErrorToast
import com.example.moneyguard.ui.components.AuthErrorVisuals
import com.example.moneyguard.ui.components.PasswordVisibilityToggle
import com.example.moneyguard.ui.theme.BrandBlueMid
import com.example.moneyguard.ui.theme.FieldBackground
import com.example.moneyguard.ui.theme.MoneyGuardTheme
import com.example.moneyguard.ui.theme.MutedText
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Each error event from the VM becomes a snackbar carrying our custom
    // AuthErrorVisuals — the SnackbarHost below renders it via AuthErrorToast.
    ObserveAsEvents(flow = viewModel.errorEvents) { msg ->
        scope.launch {
            snackbarHostState.showSnackbar(
                AuthErrorVisuals(titleRes = msg.titleRes, messageRes = msg.messageRes),
            )
        }
    }

    Scaffold(
        snackbarHost = {
            // The screen disables Scaffold's window insets (it handles status /
            // nav bars internally), so we need to lift the snackbar above the
            // nav bar + keyboard ourselves; otherwise it sits behind the
            // gesture bar and the body text gets clipped.
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
        // The screen owns its own status / nav bar handling internally.
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        containerColor = Color.Transparent,
    ) { _ ->
        BaseScreen(viewModel) { state ->
            LoginUiComponents(
                state = state.value,
                event = viewModel
            )
        }
    }
}

@Composable
private fun LoginUiComponents(
    state: LoginUiState,
    event: LoginUiEvents
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
    ) {
        LoginHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp)
                .navigationBarsPadding()
        ) {
            AppFilledTextField(
                value = state.email,
                onValueChange = event::onEmailChange,
                label = stringResource(R.string.login_email),
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
                label = stringResource(R.string.login_password),
                errorRes = state.passwordError,
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
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboard?.hide()
                        focusManager.clearFocus()
                        event.onLoginClick()
                    }
                )
            )

            Spacer(Modifier.height(4.dp))

            TextButton(
                onClick = event::onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = stringResource(R.string.login_forgot_password),
                    color = BrandBlueMid,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    keyboard?.hide()
                    focusManager.clearFocus()
                    event.onLoginClick()
                },
                enabled = !state.isEmailLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlueMid,
                    contentColor = Color.White,
                    // Slightly dimmer blue while the request is in flight,
                    // matching the design.
                    disabledContainerColor = BrandBlueMid.copy(alpha = 0.65f),
                    disabledContentColor = Color.White,
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                if (state.isEmailLoading) {
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
                            text = stringResource(R.string.login_logging_in),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.login_cta),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            OrDivider()

            Spacer(Modifier.height(20.dp))

            GoogleButton(
                enabled = !state.isGoogleLoading,
                onClick = { event.onContinueWithGoogleClick(context) },
            )

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = event::onSignUpClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = buildSignUpPrompt(),
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LoginHeader() {
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
                text = stringResource(R.string.login_header_eyebrow),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = stringResource(R.string.login_header_title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
        }
    }
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MutedText.copy(alpha = 0.25f)
        )
        Text(
            text = stringResource(R.string.login_or),
            color = MutedText,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MutedText.copy(alpha = 0.25f)
        )
    }
}

@Composable
private fun GoogleButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FieldBackground,
            contentColor = BrandBlueMid
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_google_g),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.login_google_cta),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun buildSignUpPrompt() = buildAnnotatedString {
    withStyle(SpanStyle(color = MutedText)) {
        append(stringResource(R.string.login_signup_prefix))
        append(" ")
    }
    withStyle(
        SpanStyle(
            color = BrandBlueMid,
            fontWeight = FontWeight.SemiBold
        )
    ) {
        append(stringResource(R.string.login_signup_action))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginPreview() {
    MoneyGuardTheme {
        LoginUiComponents(
            state = LoginUiState(
                email = "rahul@gmail.com",
                password = "secret123",
                emailError = null,
                passwordError = null,
                isEmailLoading = false,
                isGoogleLoading = false,
            ),
            event = object : LoginUiEvents {
                override fun onEmailChange(value: String) = Unit
                override fun onPasswordChange(value: String) = Unit
                override fun onLoginClick() = Unit
                override fun onForgotPasswordClick() = Unit
                override fun onContinueWithGoogleClick(activityContext: android.content.Context) = Unit
                override fun onSignUpClick() = Unit
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Errors")
@Composable
private fun LoginErrorPreview() {
    MoneyGuardTheme {
        LoginUiComponents(
            state = LoginUiState(
                email = "rahul@",
                password = "",
                emailError = R.string.signup_error_email_invalid,
                passwordError = R.string.signup_error_password_required,
                isEmailLoading = false,
                isGoogleLoading = false,
            ),
            event = object : LoginUiEvents {
                override fun onEmailChange(value: String) = Unit
                override fun onPasswordChange(value: String) = Unit
                override fun onLoginClick() = Unit
                override fun onForgotPasswordClick() = Unit
                override fun onContinueWithGoogleClick(activityContext: android.content.Context) = Unit
                override fun onSignUpClick() = Unit
            }
        )
    }
}
