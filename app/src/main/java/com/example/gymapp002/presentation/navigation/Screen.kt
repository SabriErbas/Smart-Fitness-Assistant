package com.example.gymapp002.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

// Tüm ekranların kimlik kartları ve rotaları burada
sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    // 1. HOME EKRANI
    object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    // 2. WORKOUT (PLAN) EKRANI
    object Workout : Screen(
        route = "workout",
        title = "Workout",
        selectedIcon = Icons.Filled.DateRange,
        unselectedIcon = Icons.Outlined.DateRange
    )

    // 3. SCAN (SEARCH) EKRANI
    object Scan : Screen(
        route = "scan",
        title = "Scan",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )

    // 4. PROFILE EKRANI
    object Profile : Screen(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    // 5. CREATE WORKOUT (GİZLİ EKRAN)
    // Bu ekran BottomBar'da görünmeyeceği için ikonları rastgele verebiliriz,
    // ancak route kısmı ("create_workout") MainScreen'deki NavHost ile birebir aynı olmalı.
    object CreateWorkout : Screen(
        route = "create_workout",
        title = "Create Workout",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Filled.Add
    )

    // {workoutId} kısmı bir değişkendir. Buraya ID gelecek.
    object WorkoutDetail : Screen(
        route = "workout_detail/{workoutId}",
        title = "Workout Detail",
        selectedIcon = Icons.Filled.DateRange, // İkonlar çok önemli değil burada
        unselectedIcon = Icons.Filled.DateRange
    )
}