package com.itsdhruvbhardwaj.urbanfix.feature.auth

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsdhruvbhardwaj.urbanfix.core.util.Constants
import com.itsdhruvbhardwaj.urbanfix.data.local.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow(Constants.LANG_EN)
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _selectedRole = MutableStateFlow<String?>(null)
    val selectedRole: StateFlow<String?> = _selectedRole.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.selectedLanguage.collect { lang ->
                _selectedLanguage.value = lang
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.userRole.collect {
                _selectedRole.value = it
            }
        }
    }

    fun selectLanguage(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveLanguage(languageCode)
            // Immediately apply language change across the app
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }

    fun selectRole(role: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveRole(role)
        }
    }
}
