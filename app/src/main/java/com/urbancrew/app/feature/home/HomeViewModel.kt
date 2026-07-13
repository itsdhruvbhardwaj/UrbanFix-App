package com.urbancrew.app.feature.home

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbancrew.app.core.util.LocationService
import com.urbancrew.app.data.local.UserPreferencesRepository
import com.urbancrew.app.data.model.User
import com.urbancrew.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _currentLocation = MutableStateFlow("Fetching location...")
    val currentLocation: StateFlow<String> = _currentLocation.asStateFlow()

    val userRole: StateFlow<String?> = userPreferencesRepository.userRole
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val selectedLanguage: StateFlow<String> = userPreferencesRepository.selectedLanguage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "en"
        )

    init {
        fetchLocation()
    }

    fun fetchLocation() {
        viewModelScope.launch {
            val location = locationService.getCurrentLocationName()
            _currentLocation.value = location ?: "Location not available"
        }
    }

    fun selectLanguage(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveLanguage(languageCode)
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }

    // Synchronous-like check for initial session validation
    suspend fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            userPreferencesRepository.saveRole("")
        }
    }
}
