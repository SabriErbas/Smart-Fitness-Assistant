package com.example.gymapp002.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gymapp002.ui.navigation.Screen
import com.example.gymapp002.ui.screens.CreateWorkoutScreen
import com.example.gymapp002.ui.screens.HomeScreen
import com.example.gymapp002.ui.screens.ProfileScreen
import com.example.gymapp002.ui.screens.SearchScreen
import com.example.gymapp002.ui.screens.WorkoutScreen
import com.example.gymapp002.ui.theme.MainColorScheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // Alt menüde görünecek ekranlar
    val bottomBarScreens = listOf(
        Screen.Home,
        Screen.Workout,
        Screen.Scan,
        Screen.Profile
    )

    Scaffold(
        modifier = modifier, // Parametreden gelen modifier'ı buraya verelim
        bottomBar = {
            NavigationBar(
                containerColor = MainColorScheme.surface,
                contentColor = MainColorScheme.onSurfaceVariant
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomBarScreens.forEach { screen ->
                    // Seçili olma durumu kontrolü
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = MainColorScheme.primary,
                            indicatorColor = MainColorScheme.primary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Navigasyon Yöneticisi
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. HOME
            composable(Screen.Home.route) { HomeScreen() }

            // 2. WORKOUT
            composable(Screen.Workout.route) {
                WorkoutScreen(
                    onNavigateToCreateWorkout = {
                        navController.navigate(Screen.CreateWorkout.route)
                    }
                )
            }

            // 3. SCAN
            composable(Screen.Scan.route) { SearchScreen() }

            // 4. PROFILE
            composable(Screen.Profile.route) { ProfileScreen() }

            // 5. CREATE WORKOUT (Gizli Ekran)
            composable(Screen.CreateWorkout.route) {
                CreateWorkoutScreen(
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { navController.popBackStack() }
                )
            }
        }
    }
}