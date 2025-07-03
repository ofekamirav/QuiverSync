package org.example.quiversync.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.domain.model.forecast.WeeklyForecast
import org.example.quiversync.features.home.HomeState
import org.example.quiversync.features.home.HomeViewModel
import org.example.quiversync.presentation.screens.WelcomeBottomSheet
import org.example.quiversync.presentation.screens.skeletons.HomeSkeleton
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.widgets.home_screen.ForecastPanel
import org.example.quiversync.presentation.widgets.home_screen.MainConditions
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    showWelcomeBottomSheetOnStart: Boolean,
    modifier: Modifier = Modifier
){
    val state by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
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
            val forecast = (state as HomeState.Loaded).forecast
            HomeScreenContent(
                forecast = forecast,
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
    forecast: WeeklyForecast,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    val windowInfo = LocalWindowInfo.current

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
                    forecast = forecast.list.first(),
                    expanded = expanded,
                    onExpandToggle = { expanded = !expanded }
                )
                ForecastPanel(forecast)
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
                        forecast = forecast.list.first(),
                        expanded = expanded,
                        onExpandToggle = { expanded = !expanded }
                    )
                }
                Column(modifier = Modifier.weight(0.4f)) {
                    ForecastPanel(forecast)
                }
            }
        }
    }
}
