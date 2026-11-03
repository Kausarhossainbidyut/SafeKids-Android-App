package com.safekids.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.safekids.ui.screen.applist.AppListScreen
import com.safekids.ui.screen.dashboard.DashboardScreen
import com.safekids.ui.screen.timelimit.TimeLimitScreen

@Composable
fun WeekThreeFourNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        // Dashboard Screen
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAppList = {
                    navController.navigate(Screen.AppList.route)
                },
                onNavigateToTimeLimit = {
                    navController.navigate(Screen.TimeLimit.route)
                }
            )
        }

        // App List Screen
        composable(route = Screen.AppList.route) {
            AppListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTimeLimit = { packageName, appName ->
                    navController.navigate(
                        Screen.TimeLimit.createRoute(packageName, appName)
                    )
                }
            )
        }

        // Time Limit Screen
        composable(
            route = Screen.TimeLimit.route,
            arguments = listOf(
                navArgument("packageName") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("appName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            val appName = backStackEntry.arguments?.getString("appName") ?: ""
            
            TimeLimitScreen(
                packageName = packageName,
                appName = appName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
