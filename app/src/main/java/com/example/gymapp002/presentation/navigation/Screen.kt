package com.example.gymapp002.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
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
    // 1. HOME
    object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    // 2. WORKOUT
    object Workout : Screen(
        route = "workout",
        title = "Workout",
        selectedIcon = Icons.Filled.DateRange,
        unselectedIcon = Icons.Outlined.DateRange
    )

    // 3. SCAN
    object Scan : Screen(
        route = "scan",
        title = "Scan",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )

    // 4. PROFILE
    object Profile : Screen(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    // --- GİZLİ EKRANLAR (BottomBar'da görünmez) ---

    // 5. CREATE WORKOUT
    object CreateWorkout : Screen(
        route = "create_workout",
        title = "Create Workout",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Filled.Add
    )

    // 6. WORKOUT DETAIL (Antrenman Detayı)
    object WorkoutDetail : Screen(
        route = "workout_detail/{workoutId}",
        title = "Workout Detail",
        selectedIcon = Icons.Filled.DateRange,
        unselectedIcon = Icons.Filled.DateRange
    )

    // 7. ACTIVE WORKOUT (Aktif Antrenman Modu - YENİ)
    object ActiveWorkout : Screen(
        route = "active_workout/{workoutId}",
        title = "Active Workout",
        selectedIcon = Icons.Filled.PlayArrow,
        unselectedIcon = Icons.Filled.PlayArrow
    )
}