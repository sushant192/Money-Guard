package com.example.moneyguard.features.auth.getstarted.ui

import androidx.lifecycle.viewModelScope
import com.example.moneyguard.core.arch.BaseComposeViewModel
import com.example.moneyguard.core.navigation.Destination
import com.example.moneyguard.core.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Named

@KoinViewModel
class GetStartedViewModel(
    @Named("AppNavigator") private val navigator: Navigator
) : BaseComposeViewModel<GetStartedUiState>(),
    GetStartedUiEvents {

    private val _uiState = MutableStateFlow(GetStartedUiState.Initial)
    override val uiState: StateFlow<GetStartedUiState> = _uiState.asStateFlow()

    override fun onGetStartedClick() {
        viewModelScope.launch {
            navigator.navigate(Destination.SignUp)
        }
    }

    override fun onLoginClick() {
        viewModelScope.launch {
            navigator.navigate(Destination.Login)
        }
    }
}
