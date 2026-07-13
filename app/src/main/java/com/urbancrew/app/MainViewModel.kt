package com.urbancrew.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbancrew.app.core.navigation.Screen
import com.urbancrew.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isUserLoggedIn()
            _startDestination.value = if (isLoggedIn) {
                Screen.Home.route
            } else {
                Screen.Auth.route
            }
        }
    }
}
