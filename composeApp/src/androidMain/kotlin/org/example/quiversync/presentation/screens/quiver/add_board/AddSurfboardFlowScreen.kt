package org.example.quiversync.presentation.screens.quiver.add_board

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.copy
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import org.example.quiversync.features.quiver.add_board.AddBoardEvent
import org.example.quiversync.features.quiver.add_board.AddBoardViewModel
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import org.example.quiversync.features.quiver.add_board.AddBoardState
import org.example.quiversync.features.register.OnboardingEvent
import org.example.quiversync.features.register.OnboardingState
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddSurfboardScreen(
    modifier: Modifier = Modifier,
    viewModel: AddBoardViewModel = koinViewModel(),
    onFinish: () -> Unit,
    onBack: () -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when(val currentState = uiState){
        is AddBoardState.Idle -> {
            AddSurfboardFlowScreen(
                modifier = modifier,
                state = currentState,
                onEvent = viewModel::onEvent,
                onBack = onBack
            )
        }
        is AddBoardState.Loaded ->{
            LaunchedEffect(Unit) {
                onFinish()
            }
        }
        is AddBoardState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(isLoading = true, animationFileName = "quiver_sync_loading_animation.json", animationSize = 240.dp)
            }
        }
        is AddBoardState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${(uiState as AddBoardState.Error).message}")
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AddSurfboardFlowScreen(
    modifier: Modifier = Modifier,
    state: AddBoardState.Idle,
    onEvent: (AddBoardEvent) -> Unit,
    onBack: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val pagerState = rememberPagerState(initialPage = 0)

    LaunchedEffect(state.data.currentStep) {
        pagerState.animateScrollToPage(state.data.currentStep - 1)
    }

    LaunchedEffect(state.data.currentStep) {
        if (windowInfo.widthSize == WindowWidthSize.COMPACT) {
            pagerState.animateScrollToPage(state.data.currentStep - 1)
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (windowInfo.widthSize) {
            WindowWidthSize.COMPACT -> {
                HorizontalPager(
                    count = state.data.totalSteps,
                    state = pagerState,
                    userScrollEnabled = false,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 80.dp),
                ) { page ->
                    when (page) {
                        0 -> AddSurfboardStep1(state, onEvent)
                        1 -> AddSurfboardStep2(state, onEvent)
                    }
                }
            }
            else -> { // (Medium & Expanded)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 80.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(max = 400.dp)
                    ) {
                        AddSurfboardStep1(state, onEvent)
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(max = 400.dp)
                    ) {
                        AddSurfboardStep2(state, onEvent)
                    }
                }
            }
        }

        // Floating transparent navigation bar
        WizardNavigationBar(
            currentStep = state.data.currentStep,
            totalSteps = state.data.totalSteps,
            onPreviousClicked = { onEvent(AddBoardEvent.PreviousStepClicked) },
            onNextClicked = { onEvent(AddBoardEvent.NextStepClicked) },
            onFinishClicked = { onEvent(AddBoardEvent.SubmitClicked) },
            onBack = onBack,
            isTabletLayout = windowInfo.widthSize != WindowWidthSize.COMPACT,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}

@Composable
fun WizardNavigationBar(
    currentStep: Int,
    totalSteps: Int,
    onPreviousClicked: () -> Unit,
    onBack: () -> Unit,
    isTabletLayout: Boolean = false,
    onNextClicked: () -> Unit,
    onFinishClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentStep > 1) {
            OutlinedButton(
                onClick = onPreviousClicked,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text("Back")
            }
        } else {
            OutlinedButton(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text("Back")
            }
        }

        Button(
            onClick = if (currentStep < totalSteps) onNextClicked else onFinishClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (currentStep < totalSteps) "Next" else "Finish",
                color = Color.White
            )
        }
    }
}