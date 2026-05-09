package com.example.moneyguard.core.arch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Wraps a Compose screen so that [BaseComposeViewModel.onActive] fires on START
 * and [BaseComposeViewModel.onInactive] fires on STOP, and exposes the latest
 * [UiState] as a Compose [State] to the [content] block.
 */
@Composable
fun <S : UiState> BaseScreen(
    viewModel: BaseComposeViewModel<S>,
    content: @Composable (State<S>) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.onActive()
                Lifecycle.Event.ON_STOP -> viewModel.onInactive()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    content(viewModel.uiState.collectAsStateWithLifecycle())
}
