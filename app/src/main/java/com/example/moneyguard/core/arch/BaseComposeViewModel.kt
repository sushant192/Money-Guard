package com.example.moneyguard.core.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

/**
 * Base ViewModel for Compose screens.
 *
 * Subclasses expose a single [uiState] [StateFlow] that the [BaseScreen] collects
 * with `collectAsStateWithLifecycle`. The [onActive] / [onInactive] hooks fire
 * when the hosting Composable enters / leaves the STARTED state.
 */
abstract class BaseComposeViewModel<State : UiState> : ViewModel() {

    abstract val uiState: StateFlow<State>

    /**
     * Called before [uiState] is collected by the screen. Use this to refresh
     * data that should be re-fetched whenever the screen comes back to the
     * foreground.
     */
    open fun onActive() {}

    /**
     * Called when the screen leaves STARTED. Useful for cancelling work that
     * should not survive while the screen is in the background.
     */
    open fun onInactive() {}

    /**
     * Converts a cold [Flow] into a hot [StateFlow] tied to [viewModelScope].
     */
    protected fun <T> Flow<T>.asState(
        defaultState: T,
        sharingStarted: SharingStarted = DEFAULT_SHARING_STARTED
    ): StateFlow<T> = stateIn(viewModelScope, sharingStarted, defaultState)

    /**
     * Converts a cold [Flow] into a hot [SharedFlow] tied to [viewModelScope].
     */
    protected fun <T> Flow<T>.asShared(
        sharingStarted: SharingStarted = DEFAULT_SHARING_STARTED
    ): SharedFlow<T> = shareIn(viewModelScope, sharingStarted)

    private companion object {
        val DEFAULT_SHARING_STARTED = WhileSubscribed(5.seconds.inWholeMilliseconds)
    }
}
