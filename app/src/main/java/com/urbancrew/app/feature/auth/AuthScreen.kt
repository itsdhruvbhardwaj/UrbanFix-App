package com.urbancrew.app.feature.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.urbancrew.app.R
import com.urbancrew.app.core.util.Constants
import com.urbancrew.app.feature.auth.components.LanguagePage
import com.urbancrew.app.feature.auth.components.LoginFormPage
import com.urbancrew.app.feature.auth.components.RoleSelectionPage
import com.urbancrew.app.feature.auth.components.VerificationSentUI
import com.urbancrew.app.ui.theme.Dimens
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    val savedPage = rememberSaveable { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        initialPage = savedPage.intValue,
        pageCount = { Constants.AUTH_PAGE_COUNT }
    )
    
    LaunchedEffect(pagerState.currentPage) {
        savedPage.intValue = pagerState.currentPage
    }

    val coroutineScope = rememberCoroutineScope()
    val authState by viewModel.authState.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token -> viewModel.loginWithGoogle(token) }
        } catch (e: ApiException) {
            viewModel.resetAuthState()
        }
    }

    fun launchGoogleSignIn() {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        val webClientId = if (resId != 0) context.getString(resId) else ""
        if (webClientId.isNotEmpty() && !webClientId.contains("placeholder")) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
            googleSignInLauncher.launch(GoogleSignIn.getClient(context, gso).signInIntent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.35f)
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(Dimens.LogoContainerSize),
                shape = RoundedCornerShape(Dimens.LogoCornerRadius),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = Dimens.ElevationMedium
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.65f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = Dimens.ElevationHigh
        ) {
            if (authState is AuthState.VerificationSent) {
                VerificationSentUI { viewModel.resetAuthState() }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(Constants.AUTH_PAGE_COUNT) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(CircleShape)
                                    .background(if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.LightGray)
                                    .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f),
                        userScrollEnabled = authState !is AuthState.Loading
                    ) { page ->
                        when (page) {
                            Constants.PAGE_LANGUAGE -> LanguagePage(
                                selectedLanguage = selectedLanguage,
                                onLanguageSelected = { 
                                    viewModel.selectLanguage(it)
                                },
                                onNext = { coroutineScope.launch { pagerState.animateScrollToPage(Constants.PAGE_ROLE) } }
                            )
                            Constants.PAGE_ROLE -> RoleSelectionPage(
                                onRoleSelected = { role ->
                                    viewModel.selectRole(role)
                                    coroutineScope.launch { pagerState.animateScrollToPage(Constants.PAGE_LOGIN) }
                                }
                            )
                            Constants.PAGE_LOGIN -> LoginFormPage(
                                role = selectedRole ?: Constants.ROLE_CUSTOMER,
                                authState = authState,
                                onLogin = { e, p -> viewModel.loginWithEmail(e, p) },
                                onSignUp = { n, e, p -> viewModel.signUp(n, e, p) },
                                onGoogleLogin = { launchGoogleSignIn() },
                                onForgotPassword = { viewModel.resetPassword(it) },
                                onResetError = { viewModel.resetAuthState() }
                            )
                        }
                    }
                }
            }
        }
    }
}
