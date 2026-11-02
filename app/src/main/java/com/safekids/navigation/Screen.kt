package com.safekids.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Registration : Screen("registration")
    object Login : Screen("login")
    object ModeSelection : Screen("mode_selection")
    object ParentDashboard : Screen("parent_dashboard")
    object ChildHome : Screen("child_home")
}
