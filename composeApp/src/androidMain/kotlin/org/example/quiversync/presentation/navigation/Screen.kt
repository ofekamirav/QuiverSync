package org.example.quiversync.presentation.navigation

sealed class Screen(
    val route: String,
    val title: String = "",
    val showInBottomBar: Boolean = false
) {
    object Main : Screen("main_screen")

    object Login : Screen("login_screen")
    object Register : Screen("register_screen")

    object Home : Screen("home_screen", "QuiverSync", true)
    object Spots : Screen("spots_screen", "Surf Spots", true)
    object Rentals : Screen("rentals_screen", "Rentals", true)
    object Quiver : Screen("quiver_screen", "My Quiver", true)
    object Profile : Screen("profile_screen", "Profile", true)

    object AddSpot : Screen("add_spot_screen", "Add Spot")
    object AddSurfboard : Screen("add_surfboard_screen", "Add Surfboard")

    object BottomSheet : Screen("bottom_sheet_screen")
}
