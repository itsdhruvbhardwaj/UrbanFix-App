package com.urbancrew.app.feature.auth

import android.util.Patterns
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urbancrew.app.R
import com.urbancrew.app.core.util.Constants
import com.urbancrew.app.core.util.UiText
import com.urbancrew.app.data.local.UserPreferencesRepository
import com.urbancrew.app.data.model.User
import com.urbancrew.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    object VerificationSent : AuthState()
    data class Message(val msg: UiText) : AuthState()
    data class Error(val message: UiText) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow(Constants.LANG_EN)
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _selectedRole = MutableStateFlow<String?>(null)
    val selectedRole: StateFlow<String?> = _selectedRole.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

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
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }

    fun selectRole(role: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveRole(role)
            _selectedRole.value = role
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun loginWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error(UiText.StringResource(R.string.error_fill_all_fields))
            return
        }
        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error(UiText.StringResource(R.string.error_invalid_email))
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginWithEmail(email, password)
            
            if (result.isSuccess) {
                val user = result.getOrThrow()
                // Sequential await ensures role is persisted before navigating
                user.role?.let { userPreferencesRepository.saveRole(it) }
                _authState.value = AuthState.Success(user)
            } else {
                val error = result.exceptionOrNull()
                val errorMsg = if (error?.message == "EMAIL_NOT_VERIFIED") {
                    UiText.StringResource(R.string.error_email_not_verified)
                } else {
                    UiText.DynamicString(error?.message ?: "Login failed")
                }
                _authState.value = AuthState.Error(errorMsg)
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        val role = _selectedRole.value ?: Constants.ROLE_CUSTOMER
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginWithGoogle(idToken, role)
            if (result.isSuccess) {
                val user = result.getOrThrow()
                user.role?.let { userPreferencesRepository.saveRole(it) }
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error(UiText.StringResource(R.string.error_google_login_failed))
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error(UiText.StringResource(R.string.error_fill_all_fields))
            return
        }
        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error(UiText.StringResource(R.string.error_invalid_email))
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error(UiText.StringResource(R.string.error_password_too_short))
            return
        }

        val role = _selectedRole.value ?: Constants.ROLE_CUSTOMER
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signUp(name, email, password, role)
            _authState.value = result.fold(
                onSuccess = { AuthState.VerificationSent },
                onFailure = { AuthState.Error(UiText.DynamicString(it.message ?: "Sign up failed")) }
            )
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error(UiText.StringResource(R.string.error_fill_all_fields))
            return
        }
        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error(UiText.StringResource(R.string.error_invalid_email))
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.sendPasswordResetEmail(email)
            _authState.value = result.fold(
                onSuccess = { AuthState.Message(UiText.StringResource(R.string.msg_reset_sent)) },
                onFailure = { AuthState.Error(UiText.StringResource(R.string.error_reset_failed)) }
            )
        }
    }

    fun setAuthError(message: String) {
        _authState.value = AuthState.Error(UiText.DynamicString(message))
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}
