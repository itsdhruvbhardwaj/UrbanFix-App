package com.urbancrew.app.feature.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.urbancrew.app.R
import com.urbancrew.app.core.util.Constants
import com.urbancrew.app.ui.theme.Dimens
import com.urbancrew.app.ui.theme.RoleCustomerColor
import com.urbancrew.app.ui.theme.RoleWorkerColor
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { Constants.AUTH_PAGE_COUNT })
    val coroutineScope = rememberCoroutineScope()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- TOP BRANDING SECTION ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(Dimens.HeaderHeightFraction)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(Dimens.LogoContainerSize),
                shape = RoundedCornerShape(Dimens.LogoCornerRadius),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = Dimens.ElevationMedium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier.size(Dimens.LogoSize)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Dimens.SpacingLarge))
            
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Urban")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFFF97316))) { // Orange color
                        append(" Crew")
                    }
                },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stringResource(R.string.onboarding_subtitle_welcome),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens.PaddingLarge)
            )
        }

        // --- BOTTOM CONTENT CARD ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(Dimens.BottomCardHeightFraction)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(
                topStart = Dimens.CornerRadiusHuge, 
                topEnd = Dimens.CornerRadiusHuge
            ),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = Dimens.ElevationHigh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(
                        horizontal = Dimens.PaddingLarge, 
                        vertical = Dimens.SpacingExtraLarge
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Toolbar Area
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (pagerState.currentPage > Constants.PAGE_LANGUAGE) {
                        IconButton(onClick = { 
                            coroutineScope.launch { 
                                pagerState.animateScrollToPage(pagerState.currentPage - 1) 
                            } 
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(Dimens.IconSizeExtraLarge))
                    }

                    Row {
                        repeat(Constants.AUTH_PAGE_COUNT) { index ->
                            val isActive = pagerState.currentPage == index
                            val color = if (isActive) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.outlineVariant
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = Dimens.PaddingExtraSmall)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(if (isActive) Dimens.DotSizeActive else Dimens.DotSizeInactive)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(Dimens.IconSizeExtraLarge))
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(Dimens.WeightFull),
                    userScrollEnabled = true,
                    pageSpacing = Dimens.SpacingMedium
                ) { page ->
                    when (page) {
                        Constants.PAGE_LANGUAGE -> LanguagePage(
                            selectedLanguage = selectedLanguage,
                            onLanguageSelected = { viewModel.selectLanguage(it) },
                            onNext = { 
                                coroutineScope.launch { pagerState.animateScrollToPage(Constants.PAGE_ROLE) } 
                            }
                        )
                        Constants.PAGE_ROLE -> RoleSelectionPage(
                            onRoleSelected = { role ->
                                viewModel.selectRole(role)
                                coroutineScope.launch { pagerState.animateScrollToPage(Constants.PAGE_LOGIN) }
                            }
                        )
                        Constants.PAGE_LOGIN -> LoginOptionsPage(
                            role = selectedRole ?: Constants.ROLE_CUSTOMER,
                            onGoogleLogin = onLoginSuccess
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LanguagePage(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.onboarding_select_language),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(Dimens.SpacingExtraLarge))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
        ) {
            LanguageItem(
                label = stringResource(R.string.onboarding_language_english), 
                code = Constants.LANG_EN, 
                isSelected = selectedLanguage == Constants.LANG_EN, 
                onSelect = onLanguageSelected
            )
            LanguageItem(
                label = stringResource(R.string.onboarding_language_hindi), 
                code = Constants.LANG_HI, 
                isSelected = selectedLanguage == Constants.LANG_HI, 
                onSelect = onLanguageSelected
            )
        }
        
        Spacer(modifier = Modifier.weight(Dimens.WeightFull))
        
        TermsAndConditionsText()
        
        Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
        
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.AuthButtonHeight),
            shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(R.string.onboarding_next), 
                fontWeight = FontWeight.Bold, 
                fontSize = Dimens.TextSizeButton
            )
        }
    }
}

@Composable
fun LanguageItem(label: String, code: String, isSelected: Boolean, onSelect: (String) -> Unit) {
    Surface(
        onClick = { onSelect(code) },
        shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge),
        border = BorderStroke(
            Dimens.BorderMedium, 
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = Dimens.AlphaLow) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.LanguageItemPadding)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label, 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(Dimens.LanguageItemIndicatorSize)
                        .clip(CircleShape)
                        .border(Dimens.BorderThin, MaterialTheme.colorScheme.outline, CircleShape)
                )
            }
        }
    }
}

@Composable
fun RoleSelectionPage(onRoleSelected: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.onboarding_title_role),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.onboarding_subtitle_role),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(Dimens.SpacingHuge))

        RoleCard(
            title = stringResource(R.string.onboarding_role_customer),
            subtitle = stringResource(R.string.onboarding_role_customer_desc),
            icon = Icons.Default.Person,
            color = RoleCustomerColor,
            onClick = { onRoleSelected(Constants.ROLE_CUSTOMER) }
        )
        
        Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

        RoleCard(
            title = stringResource(R.string.onboarding_role_worker),
            subtitle = stringResource(R.string.onboarding_role_worker_desc),
            icon = Icons.Default.Build,
            color = RoleWorkerColor,
            onClick = { onRoleSelected(Constants.ROLE_WORKER) }
        )
        
        Spacer(modifier = Modifier.weight(Dimens.WeightFull))
        TermsAndConditionsText()
    }
}

@Composable
fun RoleCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.RoleCardCornerRadius),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.outline),
        shadowElevation = Dimens.ElevationLow
    ) {
        Row(
            modifier = Modifier.padding(Dimens.SpacingExtraLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.RoleIconContainerSize)
                    .clip(CircleShape)
                    .background(color.copy(alpha = Dimens.AlphaLow)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    tint = color, 
                    modifier = Modifier.size(Dimens.RoleIconSize)
                )
            }
            
            Spacer(modifier = Modifier.width(Dimens.SpacingMedium))
            
            Column(modifier = Modifier.weight(Dimens.WeightFull)) {
                Text(
                    text = title, 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle, 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, 
                contentDescription = null, 
                tint = color
            )
        }
    }
}

@Composable
fun LoginOptionsPage(role: String, onGoogleLogin: () -> Unit) {
    val titleRes = if (role == Constants.ROLE_WORKER) 
        R.string.auth_title_login_worker
    else 
        R.string.auth_title_login_customer
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.ProfileIconContainerSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person, 
                contentDescription = null, 
                modifier = Modifier.size(Dimens.IconSizeExtraLarge), 
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(Dimens.SpacingLarge))
        
        Text(
            text = stringResource(titleRes), 
            style = MaterialTheme.typography.titleLarge, 
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.auth_subtitle_login), 
            style = MaterialTheme.typography.bodyMedium, 
            color = MaterialTheme.colorScheme.onSurfaceVariant, 
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimens.SpacingHuge))

        SocialLoginButton(stringResource(R.string.auth_google_button), onGoogleLogin)
        Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
        SocialLoginButton(stringResource(R.string.auth_email_button), {})
        Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
        SocialLoginButton(stringResource(R.string.auth_phone_button), {})
        
        Spacer(modifier = Modifier.weight(Dimens.WeightFull))
        
        Row {
            Text(
                text = stringResource(R.string.auth_no_account) + " ", 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.auth_sign_up), 
                color = MaterialTheme.colorScheme.primary, 
                fontWeight = FontWeight.Bold, 
                modifier = Modifier.clickable {  }
            )
        }
    }
}

@Composable
fun SocialLoginButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.SocialButtonHeight),
        shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge),
        border = BorderStroke(Dimens.BorderThin, MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = text, 
            color = MaterialTheme.colorScheme.onSurface, 
            fontWeight = FontWeight.SemiBold, 
            fontSize = Dimens.TextSizeSocialButton
        )
    }
}

@Composable
fun TermsAndConditionsText() {
    val annotatedString = buildAnnotatedString {
        append(stringResource(R.string.onboarding_terms_prefix))
        pushStringAnnotation(tag = Constants.TAG_TERMS, annotation = Constants.TAG_TERMS)
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
            append(stringResource(R.string.onboarding_terms_of_service))
        }
        pop()
        append(stringResource(R.string.onboarding_separator))
        pushStringAnnotation(tag = Constants.TAG_PRIVACY, annotation = Constants.TAG_PRIVACY)
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
            append(stringResource(R.string.onboarding_privacy_policy))
        }
        pop()
    }
    Text(
        text = annotatedString, 
        style = MaterialTheme.typography.bodySmall, 
        textAlign = TextAlign.Center, 
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
