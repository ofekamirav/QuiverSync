package org.example.quiversync.presentation.navigation

import androidx.annotation.NavigationRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.R
import org.example.quiversync.presentation.screens.home.HomeScreen
import org.example.quiversync.presentation.screens.ProfileScreen
import org.example.quiversync.presentation.screens.quiver.QuiverScreen
import org.example.quiversync.presentation.screens.spots.FavoriteSpotsScreen
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val items = listOf(
        BottomNavItem("Home", painterResource(id = R.drawable.ic_home), "home_screen", "QuiverSync"),
        BottomNavItem("Spots", painterResource(id = R.drawable.ic_map), "spots_screen", "Surf Spots"),
        BottomNavItem("Rentals", painterResource(id = R.drawable.ic_rentals), "rentals_screen", "Rentals"),
        BottomNavItem("Quiver", painterResource(id = R.drawable.ic_quiver), "quiver_screen", "My Quiver"),
        BottomNavItem("Profile", painterResource(id = R.drawable.ic_profile), "profile_screen", "Profile")
    )
    var selectedItem by remember { mutableStateOf(0) }
    val currentScreenTitle = items[selectedItem].title
    val canNavigateBack = false //if there is an inside page
    val isDark = isSystemInDarkTheme()
    val windowInfo = LocalWindowInfo.current
    val showBottomBar = windowInfo.widthSize == WindowWidthSize.COMPACT

    Row(modifier = Modifier.fillMaxSize()) {
        if (!showBottomBar) {
            NavigationRail {
                items.forEachIndexed { index, item ->
                    NavigationRailItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = if (selectedItem == index) OceanPalette.DeepBlue else OceanPalette.SkyBlue,
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationRailItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        )
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = currentScreenTitle,  color = OceanPalette.DeepBlue, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = if (isDark) OceanPalette.SkyBlue else OceanPalette.DeepBlue
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = if (selectedItem == index) OceanPalette.DeepBlue else OceanPalette.SkyBlue,
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        )
                    )

                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (selectedItem) {
                0 -> HomeScreen()
                1 -> FavoriteSpotsScreen()
                2 -> FavoriteSpotsScreen()
                3 -> QuiverScreen()
                4 ->  ProfileScreen(
                    onLogout = { /* handle logout */ },
                    onEdit = { /* handle edit profile */ }
                )
            }
        }

    }
}

@Preview(
    showBackground = true,
    //uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MainScreenPreview() {
    QuiverSyncTheme(/*darkTheme = true*/) {
        MainScreen()
    }
}