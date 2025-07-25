package org.example.quiversync.presentation.navigation

sealed class Screen(
    val route: String,
    val title: String = "",
    val showInBottomBar: Boolean = false
) {
    object Main : Screen("main_screen")

    //Auth Screens
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object CompleteRegister: Screen("complete_register_screen")
    object ForgotPassword : Screen("forgot_password_screen")

    //Main Screens
    object Home : Screen( "home_screen?show_welcome={show_welcome}", "QuiverSync", true)
    object Spots : Screen("spots_screen", "Surf Spots", true)
    object Rentals : Screen("rentals_screen", "Rentals", true)
    object Quiver : Screen("quiver_screen", "My Quiver", true)
    object Profile : Screen("profile_screen", "Profile", true)
    //Settings
    object Settings: Screen("settings_screen", "Settings")
    object EditProfile : Screen("edit_profile_screen", "Edit Profile")
    object SecurityAndPrivacy : Screen("security_and_privacy_screen", "Security & Privacy")


    object AddSpot : Screen("add_spot_screen", "Add Spot")
    object AddSurfboard : Screen("add_surfboard_screen", "Add Surfboard")

    object BottomSheet : Screen("bottom_sheet_screen")

    fun createRoute(showWelcome: Boolean) = "home_screen?show_welcome=$showWelcome"
}
