package com.urbancrew.app.feature.auth.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.urbancrew.app.R
import com.urbancrew.app.core.util.Constants
import com.urbancrew.app.feature.auth.AuthState
import com.urbancrew.app.ui.theme.Dimens
import com.urbancrew.app.ui.theme.RoleCustomerColor
import com.urbancrew.app.ui.theme.RoleWorkerColor

@Composable
fun VerificationSentUI(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.MarkEmailRead,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.auth_verify_email),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.auth_verify_email_desc),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = stringResource(R.string.auth_back_to_login), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LoginFormPage(
    role: String,
    authState: AuthState,
    onLogin: (String, String) -> Unit,
    onSignUp: (String, String, String) -> Unit,
    onGoogleLogin: () -> Unit,
    onForgotPassword: (String) -> Unit,
    onResetError: () -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current

    val roleName = when (role) {
        Constants.ROLE_CUSTOMER -> stringResource(R.string.role_customer)
        Constants.ROLE_WORKER -> stringResource(R.string.role_worker)
        else -> role
    }

    // Modern email processor: Auto-appends @gmail.com if no domain is detected
    fun getProcessedEmail(): String {
        val trimmed = email.trim().lowercase().filter { !it.isWhitespace() }
        return if (trimmed.isNotEmpty() && !trimmed.contains("@")) {
            "$trimmed@gmail.com"
        } else {
            trimmed
        }
    }

    val isEmailValid = email.length >= 3
    val isPasswordValid = password.length >= 6

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
            text = if (isLoginMode) stringResource(R.string.auth_welcome_back) else stringResource(R.string.auth_join_as, roleName),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (isLoginMode) stringResource(R.string.auth_login_desc) else stringResource(R.string.auth_signup_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!isLoginMode) {
            OutlinedTextField(
                value = name, 
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.auth_full_name)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Person, null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Email field with auto-complete indicator
        Column {
            OutlinedTextField(
                value = email, 
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.auth_email)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                singleLine = true,
                placeholder = { Text("username") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                trailingIcon = {
                    if (email.isNotEmpty() && !email.contains("@")) {
                        Text(
                            text = "@gmail.com",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .clickable { email = email.trim() + "@gmail.com" }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                isError = email.isNotEmpty() && !isEmailValid
            )
            if (email.isNotEmpty() && !email.contains("@")) {
                Text(
                    text = "Tip: Log in with ${email.trim().lowercase()}@gmail.com",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        // Password field with visibility toggle
        OutlinedTextField(
            value = password, 
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.auth_password)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = null)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { 
                focusManager.clearFocus()
                if (isEmailValid && isPasswordValid) {
                    val processedEmail = getProcessedEmail()
                    if (isLoginMode) onLogin(processedEmail, password)
                    else onSignUp(name, processedEmail, password)
                }
            }),
            isError = password.isNotEmpty() && !isPasswordValid
        )
        if (password.isNotEmpty() && !isPasswordValid) {
            Text(
                text = stringResource(R.string.error_password_too_short),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }

        if (isLoginMode) {
            TextButton(
                onClick = { if (email.isNotBlank()) onForgotPassword(getProcessedEmail()) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.auth_forgot_password))
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Professional Error Area
        if (authState is AuthState.Error) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ErrorOutline, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = authState.message.asString(),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (authState is AuthState.Message) {
            Text(
                text = authState.msg.asString(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Action Button
        Button(
            onClick = {
                val processedEmail = getProcessedEmail()
                if (isLoginMode) onLogin(processedEmail, password)
                else onSignUp(name, processedEmail, password)
            },
            modifier = Modifier.fillMaxWidth().height(Dimens.AuthButtonHeight),
            shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
            enabled = authState !is AuthState.Loading && email.isNotBlank() && isPasswordValid
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = if (isLoginMode) stringResource(R.string.auth_login) else stringResource(R.string.auth_sign_up),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onGoogleLogin,
            modifier = Modifier.fillMaxWidth().height(Dimens.AuthButtonHeight),
            shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
            enabled = authState !is AuthState.Loading,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = stringResource(R.string.auth_google_button), color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = if (isLoginMode) stringResource(R.string.auth_new_here) else stringResource(R.string.auth_already_account),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (isLoginMode) stringResource(R.string.auth_sign_up) else stringResource(R.string.auth_login),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    isLoginMode = !isLoginMode
                    onResetError()
                }
            )
        }
    }
}

@Composable
fun LanguagePage(selectedLanguage: String, onLanguageSelected: (String) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.onboarding_select_language),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        LanguageItem(
            label = stringResource(R.string.onboarding_language_english),
            code = Constants.LANG_EN,
            isSelected = selectedLanguage == Constants.LANG_EN,
            onSelect = onLanguageSelected
        )
        Spacer(modifier = Modifier.height(12.dp))
        LanguageItem(
            label = stringResource(R.string.onboarding_language_hindi),
            code = Constants.LANG_HI,
            isSelected = selectedLanguage == Constants.LANG_HI,
            onSelect = onLanguageSelected
        )
        Spacer(modifier = Modifier.weight(1f))
        TermsAndConditionsText()
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp)) {
            Text(text = stringResource(R.string.onboarding_next), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RoleSelectionPage(onRoleSelected: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.onboarding_title_role), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            text = stringResource(R.string.onboarding_subtitle_role),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        RoleCard(
            title = stringResource(R.string.onboarding_role_customer),
            subtitle = stringResource(R.string.onboarding_role_customer_desc),
            icon = Icons.Default.Person,
            color = RoleCustomerColor
        ) { onRoleSelected(Constants.ROLE_CUSTOMER) }
        Spacer(modifier = Modifier.height(16.dp))
        RoleCard(
            title = stringResource(R.string.onboarding_role_worker),
            subtitle = stringResource(R.string.onboarding_role_worker_desc),
            icon = Icons.Default.Build,
            color = RoleWorkerColor
        ) { onRoleSelected(Constants.ROLE_WORKER) }
        Spacer(modifier = Modifier.weight(1f))
        TermsAndConditionsText()
    }
}
