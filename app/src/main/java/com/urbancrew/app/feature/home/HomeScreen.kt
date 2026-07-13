package com.urbancrew.app.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.urbancrew.app.core.util.Constants
import com.urbancrew.app.feature.customer.home.CustomerHomeScreen
import com.urbancrew.app.feature.worker.home.WorkerHomeScreen

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val userRole by viewModel.userRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    // We use isAuthVerified to ensure we don't redirect until the SDK has spoken
    var isAuthVerified by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Initial check against the source of truth
        if (viewModel.isUserLoggedIn()) {
            isAuthVerified = true
        } else {
            onLogout()
        }
    }

    LaunchedEffect(currentUser) {
        // If the user actually becomes null after we've verified the session, then logout
        if (isAuthVerified && currentUser == null) {
            onLogout()
        }
    }

    // Show loading while we are verifying the session or waiting for the profile to load
    if (!isAuthVerified || currentUser == null || userRole == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Dispatch to correct screen based on role
    when (userRole) {
        Constants.ROLE_WORKER -> {
            WorkerHomeScreen()
        }
        else -> {
            // Default to Customer view if role is unknown or still syncing
            CustomerHomeScreen(onLogout = onLogout)
        }
    }
}
