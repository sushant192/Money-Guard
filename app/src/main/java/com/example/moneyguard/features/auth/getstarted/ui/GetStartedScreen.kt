package com.example.moneyguard.features.auth.getstarted.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneyguard.R
import com.example.moneyguard.core.arch.BaseScreen
import com.example.moneyguard.ui.theme.BrandGradient
import com.example.moneyguard.ui.theme.MoneyGuardTheme

@Composable
fun GetStartedScreen(viewModel: GetStartedViewModel) {
    BaseScreen(viewModel) { state ->
        GetStartedUiComponents(
            state = state.value,
            event = viewModel
        )
    }
}

@Composable
private fun GetStartedUiComponents(
    state: GetStartedUiState,
    event: GetStartedUiEvents
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = BrandGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            BrandIcon()

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_name),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.5).sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.get_started_tagline),
                color = Color.White.copy(alpha = 0.60f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = event::onGetStartedClick,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.18f),
                    contentColor = Color.White,
                    disabledContainerColor = Color.White.copy(alpha = 0.10f),
                    disabledContentColor = Color.White.copy(alpha = 0.50f)
                ),
                border = BorderStroke(
                    width = 1.5.dp,
                    color = Color.White.copy(alpha = 0.30f)
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.get_started_cta),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(10.dp))

            TextButton(
                onClick = event::onLoginClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = buildLoginPrompt(),
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BrandIcon() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.20f),
                shape = RoundedCornerShape(22.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.CreditCard,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
private fun buildLoginPrompt() = buildAnnotatedString {
    withStyle(SpanStyle(color = Color.White.copy(alpha = 0.55f))) {
        append(stringResource(R.string.get_started_login_prefix))
        append(" ")
    }
    withStyle(
        SpanStyle(
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    ) {
        append(stringResource(R.string.get_started_login_action))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GetStartedPreview() {
    MoneyGuardTheme {
        GetStartedUiComponents(
            state = GetStartedUiState.Initial,
            event = object : GetStartedUiEvents {
                override fun onGetStartedClick() = Unit
                override fun onLoginClick() = Unit
            }
        )
    }
}
