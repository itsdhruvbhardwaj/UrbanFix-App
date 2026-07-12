package com.urbancrew.app.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.urbancrew.app.core.util.Constants
import com.urbancrew.app.feature.customer.home.CustomerHomeScreen
import com.urbancrew.app.feature.worker.home.WorkerHomeScreen

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val userRole by viewModel.userRole.collectAsState()

    // Pure Dispatcher: Switch between completely independent mode implementations
    when (userRole) {
        Constants.ROLE_WORKER -> {
            WorkerHomeScreen()
        }
        else -> {
            CustomerHomeScreen()
        }
    }
}
