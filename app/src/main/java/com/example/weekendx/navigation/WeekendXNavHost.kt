package com.example.weekendx.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weekendx.ui.about.AboutScreen
import com.example.weekendx.ui.history.HistoryScreen
import com.example.weekendx.ui.home.HomeScreen
import com.example.weekendx.ui.home.WeekendXViewModel
import com.example.weekendx.ui.settings.SettingsScreen
import com.example.weekendx.ui.splash.SplashScreen

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun WeekendXNavHost(
    viewModel: WeekendXViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(
            route = WeekendXDestination.Home.route,
            label = "Find",
            selectedIcon = Icons.Filled.DateRange,
            unselectedIcon = Icons.Outlined.DateRange
        ),
        BottomNavItem(
            route = WeekendXDestination.History.route,
            label = "History",
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Outlined.History
        ),
        BottomNavItem(
            route = WeekendXDestination.Settings.route,
            label = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        ),
        BottomNavItem(
            route = WeekendXDestination.About.route,
            label = "About",
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info
        )
    )

    val showBottomBar = currentRoute != WeekendXDestination.Splash.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = WeekendXDestination.Splash.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            composable(WeekendXDestination.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(WeekendXDestination.Home.route) {
                            popUpTo(WeekendXDestination.Splash.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(WeekendXDestination.Home.route) {
                HomeScreen(viewModel = viewModel)
            }

            composable(WeekendXDestination.History.route) {
                HistoryScreen(
                    viewModel = viewModel,
                    onNavigateToHome = {
                        navController.navigate(WeekendXDestination.Home.route) {
                            popUpTo(WeekendXDestination.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(WeekendXDestination.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }

            composable(WeekendXDestination.About.route) {
                AboutScreen()
            }
        }
    }
}
