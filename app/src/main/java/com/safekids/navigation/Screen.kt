package com.safekids.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Registration : Screen("registration")
    object Login : Screen("login")
    object ModeSelection : Screen("mode_selection")
    object ParentDashboard : Screen("parent_dashboard")
    object ChildHome : Screen("child_home")
    
    // Week 3-4: Usage Stats, App Management, Time Limits
    object Dashboard : Screen("dashboard")
    object AppList : Screen("app_list")
    object TimeLimit : Screen("time_limit?packageName={packageName}&appName={appName}") {
        fun createRoute(packageName: String, appName: String): String {
            return "time_limit?packageName=$packageName&appName=$appName"
        }
    }
}
