package com.example.quiversync.android.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.example.quiversync.android.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quiversync.android.navigation.RootNavGraph
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.*
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

@Destination<RootNavGraph>(start = true)
@Composable
fun MainScreen() {
    val items = listOf(
        BottomNavItem("Home", painterResource(id = R.drawable.ic_home), HomeScreenDestination),
        BottomNavItem("Spots", painterResource(id = R.drawable.ic_map), SpotsScreenDestination),
        BottomNavItem(
            "Quiver",
            painterResource(id = R.drawable.ic_quiver),
            QuiverScreenDestination
        ),
        BottomNavItem(
            "Profile",
            painterResource(id = R.drawable.ic_profile),
            ProfileScreenDestination
        ),
    )
    var selectedItem by remember { mutableStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFFFFFFF)
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = Color(0xFF007BFF),
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFFEFF6FF),
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
                1 -> SpotsScreen()
                2 -> QuiverScreen()
                3 -> ProfileScreen()
            }
        }

    }
}
data class BottomNavItem(
    val label: String,
    val icon: Painter,
    val destination: DirectionDestinationSpec
)

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}

