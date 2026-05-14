package com.example.moneyguard.features.auth.splash.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moneyguard.R
import com.example.moneyguard.core.arch.BaseScreen
import com.example.moneyguard.ui.theme.BrandBlueDeep
import com.example.moneyguard.ui.theme.BrandBlueMid

/**
 * Painted to look like the system splash so the swap from the Android-12+
 * splash screen API to our Compose splash is invisible. SplashViewModel
 * navigates away as soon as the auth check resolves (one frame in practice).
 */
@Composable
fun SplashScreen(viewModel: SplashViewModel) {
    BaseScreen(viewModel) { _ ->
        SplashUiComponents()
    }
}

@Composable
private fun SplashUiComponents() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BrandBlueMid, BrandBlueDeep))),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_moneyguard_logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(96.dp),
        )
    }
}
