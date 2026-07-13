package com.urbancrew.app.feature.customer.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.urbancrew.app.R
import com.urbancrew.app.ui.theme.Dimens
import kotlinx.coroutines.launch

@Composable
fun CustomerHomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentUser by viewModel.currentUser.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout_confirm_title)) },
            text = { Text(stringResource(R.string.logout_confirm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout()
                    }
                ) {
                    Text(stringResource(R.string.logout_confirm_yes), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.logout_confirm_no))
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomerHomeDrawer(
                user = currentUser,
                onCloseClick = { scope.launch { drawerState.close() } },
                onLogoutClick = { 
                    showLogoutDialog = true
                    scope.launch { drawerState.close() } 
                },
                onItemClick = { _ ->
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                CustomerTopBar(onMenuClick = {
                    scope.launch { drawerState.open() }
                })
            },
            bottomBar = { CustomerBottomNavigation() },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                item { GreetingSection(name = currentUser?.name?.split(" ")?.firstOrNull() ?: "User") }
                item { LocationSection(location = "Sector 62, Noida, Uttar Pradesh") }
                item { SearchSection() }
                item { PopularServicesSection() }
                item { VerifiedBanner() }
                item { HowItWorksSection() }
                item { TopRatedSection() }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}
