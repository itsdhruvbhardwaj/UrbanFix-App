package com.urbancrew.app.core.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Auth : Screen("auth")
    data object Booking : Screen("booking")
    data object Profile : Screen("profile")
    data object Search : Screen("search")
    data object Notification : Screen("notification")
    data object Settings : Screen("settings")
}
