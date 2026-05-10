package com.example.moneyguard.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.moneyguard.R
import com.example.moneyguard.ui.theme.BrandBlueMid
import com.example.moneyguard.ui.theme.ErrorMain
import com.example.moneyguard.ui.theme.FieldBackground
import com.example.moneyguard.ui.theme.MutedText

/**
 * App-wide filled-style text field used across the auth screens.
 *
 * - Container: [FieldBackground] (soft blue-grey)
 * - Borders: invisible by default, vibrant red ([ErrorMain]) on error
 * - Input text: black for clear readability
 * - Supporting text: shows [errorRes] (red) when not null, otherwise [helperRes] (muted).
 *   Pass [helperRes] = null to hide the helper line entirely.
 */
@Composable
fun AppFilledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    @StringRes errorRes: Int? = null,
    @StringRes helperRes: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val isError = errorRes != null
    val supportingText: (@Composable () -> Unit)? = when {
        errorRes != null -> {
            { Text(stringResource(errorRes)) }
        }
        helperRes != null -> {
            { Text(stringResource(helperRes)) }
        }
        else -> null
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            errorTextColor = Color.Black,
            disabledTextColor = Color.Black.copy(alpha = 0.4f),

            focusedContainerColor = FieldBackground,
            unfocusedContainerColor = FieldBackground,
            disabledContainerColor = FieldBackground,
            errorContainerColor = FieldBackground,

            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            errorBorderColor = ErrorMain,

            focusedLabelColor = MutedText,
            unfocusedLabelColor = MutedText,
            errorLabelColor = ErrorMain,

            focusedSupportingTextColor = MutedText,
            unfocusedSupportingTextColor = MutedText,
            errorSupportingTextColor = ErrorMain,

            cursorColor = BrandBlueMid,
            errorCursorColor = ErrorMain
        )
    )
}

/**
 * Eye / eye-off toggle suitable for the [trailingIcon] of an
 * [AppFilledTextField] holding a password.
 */
@Composable
fun PasswordVisibilityToggle(
    visible: Boolean,
    onToggle: () -> Unit
) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
            contentDescription = stringResource(
                if (visible) R.string.password_hide else R.string.password_show
            ),
            tint = MutedText
        )
    }
}
