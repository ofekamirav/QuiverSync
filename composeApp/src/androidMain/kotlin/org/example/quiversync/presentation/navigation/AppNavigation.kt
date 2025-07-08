package org.example.quiversync.presentation.navigation

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.example.quiversync.R
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.screens.LoginScreen
import org.example.quiversync.presentation.screens.ProfileScreen
import org.example.quiversync.presentation.screens.register.RegisterScreen
import org.example.quiversync.presentation.screens.home.HomeScreen
import org.example.quiversync.presentation.screens.quiver.QuiverScreen
import org.example.quiversync.presentation.screens.quiver.add_board.AddSurfboardFlowScreen
import org.example.quiversync.presentation.screens.quiver.add_board.AddSurfboardScreen
import org.example.quiversync.presentation.screens.register.CompleteRegisterScreen
import org.example.quiversync.presentation.screens.register.OnboardingScreen
import org.example.quiversync.presentation.screens.rentals.RentalsHubScreen
import org.example.quiversync.presentation.screens.spots.FavoriteSpotsScreen
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(sessionManager: SessionManager = koinInject()) {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }
    var hasSeenWelcome by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        val uid = sessionManager.getUid()
        Log.d("AppNavigation","User ID from SessionManager: $uid")
        isLoggedIn = uid != null
        hasSeenWelcome = sessionManager.hasSeenWelcome()
    }

    if (isLoggedIn == null || hasSeenWelcome == null) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ){
            LoadingAnimation(
                isLoading = true,
                animationFileName = "quiver_sync_loading_animation.json"
            )
            return
        }
    }

    val items = listOf(
        BottomNavItem(
            "Home",
            painterResource(id = R.drawable.ic_home),
            Screen.Home.route,
            "QuiverSync"
        ),
        BottomNavItem(
            "Spots",
            painterResource(id = R.drawable.ic_map),
            Screen.Spots.route,
            "Surf Spots"
        ),
        BottomNavItem(
            "Rentals",
            painterResource(id = R.drawable.ic_rentals),
            Screen.Rentals.route,
            "Rentals"
        ),
        BottomNavItem(
            "Quiver",
            painterResource(id = R.drawable.ic_quiver),
            Screen.Quiver.route,
            "My Quiver"
        ),
        BottomNavItem(
            "Profile",
            painterResource(id = R.drawable.ic_profile),
            Screen.Profile.route,
            "Profile"
        )
    )
    val startDestination = if (isLoggedIn == true) Screen.Home.route else Screen.Login.route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hideTopBarRoutes = listOf(Screen.Login.route, Screen.Register.route, Screen.CompleteRegister.route)
    val showTopBar = currentRoute !in hideTopBarRoutes
    val currentScreen = Screen::class.sealedSubclasses
        .mapNotNull { it.objectInstance }
        .find { it.route == currentRoute }
    val currentScreenTitle = currentScreen?.title ?: ""
    val isDark = isSystemInDarkTheme()
    val windowInfo = LocalWindowInfo.current
    val showBottomBar =
        (windowInfo.widthSize == WindowWidthSize.COMPACT) && currentRoute !in hideTopBarRoutes

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        Text(
                            text = currentScreenTitle,
                            color = OceanPalette.DeepBlue,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                )
            } else null
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OceanPalette.DeepBlue,
                                unselectedIconColor = OceanPalette.SkyBlue,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onSignInSuccess = {
                        isLoggedIn = true
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0)
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onSignUpSuccess = {
                        navController.navigate(Screen.CompleteRegister.route)
                    },
                    onLoginClick = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    showWelcomeBottomSheetOnStart = hasSeenWelcome == false,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(Screen.Spots.route) {
                FavoriteSpotsScreen(modifier = Modifier.padding(innerPadding))
            }
            composable(Screen.Rentals.route) {
                RentalsHubScreen(modifier = Modifier.padding(innerPadding))
            }
            composable(Screen.Quiver.route) {
                QuiverScreen(
                    modifier = Modifier.padding(innerPadding),
                    onAddClick = { navController.navigate(Screen.AddSurfboard.route) }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    modifier = Modifier.padding(innerPadding),
                    onLogout = {
                        isLoggedIn = false
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }

                    }
                )
            }
            composable(Screen.AddSurfboard.route) {
                AddSurfboardScreen(
                    modifier = Modifier.padding(innerPadding),
                    onFinish = {
                        navController.navigate(Screen.Quiver.route) {
                            popUpTo(Screen.Quiver.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                )
            }
            composable(Screen.CompleteRegister.route) {
                OnboardingScreen(
                    onCompleteClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0)
                        }
                    }
                )
            }
        }
    }
}
