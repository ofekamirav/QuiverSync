package org.example.quiversync.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.quiversync.presentation.components.WelcomeBottomSheet
import org.example.quiversync.presentation.screens.LoginScreen
import org.example.quiversync.presentation.screens.RegisterScreen

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Main : Screen("main_screen")
    object bottomSheet : Screen("bottom_sheet")
}


@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }

    var startDestination = if (isLoggedIn) Screen.Main.route else Screen.Login.route


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route){
            LoginScreen(
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onSignInClick = {
                    isLoggedIn = true
                    navController.navigate(Screen.Main.route)
                }
            )
        }
        composable(Screen.Register.route){
            RegisterScreen()
        }
        composable(Screen.Main.route){
            MainScreen()
        }
        composable(Screen.bottomSheet.route){
            WelcomeBottomSheet(
                show = true,
                onDismiss = {
                    navController.navigate(Screen.Login.route)
                },
                onGetStarted = {
                    navController.navigate(Screen.Main.route)
                }
            )
        }

    }
}
