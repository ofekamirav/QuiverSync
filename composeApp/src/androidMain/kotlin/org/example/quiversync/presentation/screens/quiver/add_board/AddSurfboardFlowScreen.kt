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
@Composable
fun AddSurfboardFlowScreen(
    modifier: Modifier = Modifier,
    state: AddBoardState.Idle,
    onEvent: (AddBoardEvent) -> Unit,
    onBack: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        HorizontalPager(
            count = state.data.totalSteps,
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.weight(1f),
        ) { page ->
            when (page) {
                0 -> AddSurfboardStep1(state, onEvent)
                1 -> AddSurfboardStep2(state, onEvent)
            }
        }
        WizardNavigationBar(
            currentStep = pagerState.currentPage + 1,
            totalSteps = state.data.totalSteps,
            onPreviousClicked = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onNextClicked = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            onFinishClicked = {
                onEvent(AddBoardEvent.SubmitClicked)
            },
            onBack = onBack
        )
    }
}

@Composable
fun WizardNavigationBar(
    currentStep: Int,
    totalSteps: Int,
    onPreviousClicked: () -> Unit,
    onBack: () -> Unit,
    onNextClicked: () -> Unit,
    onFinishClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentStep > 1) {
            OutlinedButton(
                onClick = onPreviousClicked,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
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