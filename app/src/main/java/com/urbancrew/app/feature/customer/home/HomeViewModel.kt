package com.urbancrew.app.feature.customer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbancrew.app.data.local.UserPreferencesRepository
import com.urbancrew.app.data.model.User
import com.urbancrew.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            // Clear the persisted role so the app resets to a clean state
            userPreferencesRepository.saveRole("")
        }
    }
}
