package org.example.quiversync.presentation.navigation

import androidx.compose.ui.graphics.painter.Painter

data class BottomNavItem(
    val label: String,
    val icon: Painter,
    val route: String,
    val title: String
)
