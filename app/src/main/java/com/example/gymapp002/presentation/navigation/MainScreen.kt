package com.example.gymapp002

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.gymapp002.presentation.navigation.NavItem
import com.example.gymapp002.ui.screens.HomeScreen
import com.example.gymapp002.ui.screens.ProfileScreen
import com.example.gymapp002.ui.screens.WorkoutScreen
import com.example.gymapp002.ui.theme.MainColorScheme


val items = listOf(
    NavItem(
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    NavItem(
        label = "Workout",
        selectedIcon = Icons.Filled.DateRange,
        unselectedIcon = Icons.Outlined.DateRange
    ),
    NavItem(
        label = "Scan",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    ),
    NavItem(
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person)
    )


@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(contentColor = MainColorScheme.onSurfaceVariant,
                containerColor = MainColorScheme.surface) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(imageVector = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon, contentDescription = item.label) },
                        label = { Text(text = item.label) }
                    )
                }
            }
        }
    )  { innerPadding ->
        ContetScreen(modifier = Modifier.padding(innerPadding), selectedIndex = selectedIndex)
    }

}


@Composable
fun ContetScreen(modifier: Modifier = Modifier, selectedIndex: Int){
    when(selectedIndex){
        0 -> {HomeScreen()
             }
        1 -> { WorkoutScreen()
             }
        2 -> {//ScanScreen()
             }
        3 -> { ProfileScreen() }
    }
}

