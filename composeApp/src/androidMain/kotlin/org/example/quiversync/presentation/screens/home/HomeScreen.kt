package org.example.quiversync.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.forecast.WeeklyForecast // ייבוא אם רלוונטי
import org.example.quiversync.domain.model.prediction.GeminiPrediction // ייבוא אם רלוונטי
import org.example.quiversync.domain.model.Surfboard // ייבוא אם רלוונטי
import org.example.quiversync.features.home.HomePageData // ייבוא HomePageData
import org.example.quiversync.features.home.HomeState
import org.example.quiversync.features.home.HomeViewModel
import org.example.quiversync.presentation.screens.WelcomeBottomSheet
import org.example.quiversync.presentation.screens.skeletons.HomeSkeleton
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.widgets.ErrorScreen
import org.example.quiversync.presentation.widgets.home_screen.ForecastPanel
import org.example.quiversync.presentation.widgets.home_screen.MainConditions
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    showWelcomeBottomSheetOnStart: Boolean,
    modifier: Modifier = Modifier
){
    val state by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            viewModel.refreshWithLocation()
        }
    }

    LaunchedEffect(showWelcomeBottomSheetOnStart) {
        if (showWelcomeBottomSheetOnStart) {
            showBottomSheet = true
        }
    }
    if (showBottomSheet) {
        WelcomeBottomSheet(
            show = showBottomSheet,
            onDismiss = { showBottomSheet = false },
        )
    }

    if (!locationPermissionState.status.isGranted) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ErrorScreen(
                message = "Please grant location permission to access weather data.",
                modifier = Modifier.padding(16.dp),
                icon = Icons.Default.LocationOn,
                buttonText = "Grant Permission",
                onButtonClick = {
                    locationPermissionState.launchPermissionRequest()
                }
            )
        }
        return
    }

    when (state) {
        is HomeState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                HomeSkeleton()
            }
        }
        is HomeState.Loaded -> {
            val data = (state as HomeState.Loaded).homePageData
            HomeScreenContent(
                homePageData = data,
                modifier = modifier
            )
        }
        is HomeState.Error -> {
            val message = (state as HomeState.Error).message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: $message", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    homePageData: HomePageData,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    val windowInfo = LocalWindowInfo.current

    val weeklyForecast = homePageData.weeklyForecast
    val predictionForToday = homePageData.predictionForToday
    val surfboard = homePageData.surfboard

    val todayForecast = weeklyForecast.firstOrNull()

    if (todayForecast == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No forecast data available.", color = MaterialTheme.colorScheme.onSurface)
        }
        return
    }

    when (windowInfo.widthSize) {
        WindowWidthSize.COMPACT -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MainConditions(
                    forecast = todayForecast,
                    prediction = predictionForToday,
                    surfboard = surfboard,
                    expanded = expanded,
                    onExpandToggle = { expanded = !expanded }
                )
                ForecastPanel(weeklyForecast)
            }
        }

        else -> { // MEDIUM or EXPANDED
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(0.6f)) {
                    MainConditions(
                        forecast = todayForecast,
                        prediction = predictionForToday,
                        surfboard = surfboard,
                        expanded = expanded,
                        onExpandToggle = { expanded = !expanded }
                    )
                }
                Column(modifier = Modifier.weight(0.4f)) {
                    ForecastPanel(weeklyForecast)
                }
            }
        }
    }
}