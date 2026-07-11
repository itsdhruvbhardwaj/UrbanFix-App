package com.itsdhruvbhardwaj.urbanfix.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsdhruvbhardwaj.urbanfix.core.util.Constants
import com.itsdhruvbhardwaj.urbanfix.feature.customer.home.CustomerHomeScreen
import com.itsdhruvbhardwaj.urbanfix.feature.worker.home.WorkerHomeScreen

/**
 * The main Home entry point.
 * Its only responsibility is to dispatch the user to the correct Mode (Customer or Worker)
 * based on their role stored in preferences.
 *
 * This architecture allows for 2-directional development:
 * - Customer features are isolated in `feature.customer`
 * - Worker features are isolated in `feature.worker`
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val userRole by viewModel.userRole.collectAsState()

    // Dispatcher: Switch between completely independent full-screen implementations
    when (userRole) {
        Constants.ROLE_WORKER -> {
            WorkerHomeScreen()
        }
        else -> {
            CustomerHomeScreen()
        }
    }
}
