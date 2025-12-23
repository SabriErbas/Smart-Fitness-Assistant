package com.example.gymapp002.presentation.navigation


import androidx.compose.ui.graphics.vector.ImageVector


data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
