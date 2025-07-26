package org.example.quiversync.presentation.navigation

import MainViewModel
import android.R.attr.type
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.quiversync.R
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.features.login.LoginState
import org.example.quiversync.features.login.LoginViewModel
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.components.LottieSplashScreen
import org.example.quiversync.presentation.components.SnackbarWithCountdown
import org.example.quiversync.presentation.screens.login.LoginScreen
import org.example.quiversync.presentation.screens.profile.ProfileScreen
import org.example.quiversync.presentation.screens.register.RegisterScreen
import org.example.quiversync.presentation.screens.home.HomeScreen
import org.example.quiversync.presentation.screens.login.ForgotPasswordScreen
import org.example.quiversync.presentation.screens.quiver.QuiverScreen
import org.example.quiversync.presentation.screens.quiver.add_board.AddSurfboardScreen
import org.example.quiversync.presentation.screens.register.OnboardingScreen
import org.example.quiversync.presentation.screens.rentals.RentalsHubScreen
import org.example.quiversync.presentation.screens.settings.EditProfileDetailsScreen
import org.example.quiversync.presentation.screens.settings.SecurityAndPrivacyScreen
import org.example.quiversync.presentation.screens.settings.SettingsScreen
import org.example.quiversync.presentation.screens.spots.AddSpotScreen
import org.example.quiversync.presentation.screens.spots.FavoriteSpotsScreen
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    mainViewModel: MainViewModel = koinInject()
) {
    val navController = rememberNavController()
    val uid by mainViewModel.uidState.collectAsState()
    val isInitialCheckDone by mainViewModel.isInitialCheckDone.collectAsState()
    val isAppReady by mainViewModel.isAppReady.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showLottieSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val minSplashTime = 3000L
        val startTime = System.currentTimeMillis()
        Log.d("AppNavigation", "Starting app initialization")

        while (!isInitialCheckDone) {
            delay(100)
        }

        val endTime = System.currentTimeMillis()
        val elapsed = endTime - startTime
        if (elapsed < minSplashTime) {
            delay(minSplashTime - elapsed)
        }
        showLottieSplash = false
        Log.d("AppNavigation", "App initialization completed. UID: $uid")
    }

    if (showLottieSplash) {
        LottieSplashScreen(
            animationFileName = "splash_intro_animation.json",
            iterations = 1,
            onAnimationFinished = {
            }
        )
        return
    }

    if (!isInitialCheckDone) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            LoadingAnimation(
                isLoading = true,
                animationFileName = "quiver_sync_loading_animation.json",
                animationSize = 240.dp

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
    val startDestination = when {
        uid != null && isAppReady -> Screen.Home.route
        uid != null && !isAppReady -> Screen.Login.route
        else -> Screen.Login.route
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hideTopBarRoutes = listOf(
        Screen.Login.route,
        Screen.Register.route,
        Screen.CompleteRegister.route,
        Screen.ForgotPassword.route
    )
    val navigationIconRoutes = listOf(
        Screen.AddSurfboard.route,
        Screen.Settings.route,
        Screen.AddSpot.route,
        Screen.EditProfile.route,
        Screen.SecurityAndPrivacy.route
    )
    val showNavigationIcon = currentRoute in navigationIconRoutes
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                SnackbarWithCountdown(data)
            }
        },
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
                    actions = {
                        if (currentRoute == Screen.Profile.route) {
                            IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        if (showNavigationIcon) {
                            IconButton(
                                onClick = {
                                    navController.popBackStack()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
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
            } else null
        },
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                //.padding(innerPadding)
        ) {
            if (!showBottomBar && currentRoute !in hideTopBarRoutes) {
                NavigationRail(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(72.dp),
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items.forEach { item ->
                            NavigationRailItem(
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
                                icon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(30.dp)
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = OceanPalette.DeepBlue,
                                    unselectedIconColor = OceanPalette.SkyBlue,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
            )
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                composable(Screen.Login.route) {
                    val viewModel: LoginViewModel = koinViewModel()
                    val loginState by viewModel.loginState.collectAsState()

                    LaunchedEffect(loginState) {
                        if (loginState is LoginState.NavigateToOnboarding) {
                            navController.navigate(Screen.CompleteRegister.route) {
                                popUpTo(0)
                            }
                        } else if (loginState is LoginState.Loaded) {
                            // המתן לסיום הסנכרון לפני המעבר
                            while (!isAppReady) {
                                delay(100)
                            }
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0)
                            }
                        }
                    }

                    LoginScreen(
                        viewModel = viewModel,
                        onRegisterClick = {
                            navController.navigate(Screen.Register.route)
                        },
                        onForgotPasswordClick = {
                            navController.navigate(Screen.ForgotPassword.route)
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
                composable(
                    route = Screen.Home.route,
                    arguments = listOf(
                        navArgument("show_welcome") {
                            type = NavType.BoolType
                            defaultValue = false
                        }
                    )
                ) { backStackEntry ->
                    val showWelcome = backStackEntry.arguments?.getBoolean("show_welcome") ?: false

                    HomeScreen(
                        showWelcomeBottomSheetOnStart = showWelcome,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                composable(Screen.Spots.route) {
                    FavoriteSpotsScreen(
                        modifier = Modifier.padding(innerPadding),
                        snackbarHostState = snackbarHostState,
                        onAddSpotClick = { navController.navigate(Screen.AddSpot.route) }
                    )
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
                    ProfileScreen(modifier = Modifier.padding(innerPadding))
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
                            navController.navigate(Screen.Home.createRoute(showWelcome = true)) {
                                popUpTo(0)
                            }
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        modifier = Modifier.padding(innerPadding),
                        onEditProfile = {
                            navController.navigate(Screen.EditProfile.route)
                        },
                        onSecuritySettings = {
                            navController.navigate(Screen.SecurityAndPrivacy.route)
                        },
                        onNotificationsSettings = {
                            //navController.navigate(Screen.NotificationsSettings.route)
                        },
                        onHelpSupport = {},
                        onLogout = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0)
                            }
                        },
                    )
                }

                composable(Screen.EditProfile.route) {
                    EditProfileDetailsScreen(
                        modifier = Modifier.padding(innerPadding),
                        onSave = {
                            navController.popBackStack()
                        },
                    )
                }
                composable(Screen.SecurityAndPrivacy.route) {
                    SecurityAndPrivacyScreen(
                        modifier = Modifier.padding(innerPadding),
                        onSuccess = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.ForgotPassword.route) {
                    ForgotPasswordScreen(
                        modifier = Modifier.padding(innerPadding),
                        onLoginClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Screen.AddSpot.route) {
                    AddSpotScreen(
                        modifier = Modifier.padding(innerPadding),
                        onSpotAdded = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
