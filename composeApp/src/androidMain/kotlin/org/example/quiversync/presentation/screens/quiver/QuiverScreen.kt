package org.example.quiversync.presentation.screens.quiver

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.example.quiversync.R
import org.example.quiversync.data.remote.dto.RentalPublishDetails
import org.example.quiversync.features.quiver.QuiverState
import org.example.quiversync.features.quiver.QuiverViewModel
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.features.quiver.QuiverEvent
import org.example.quiversync.presentation.components.CustomDialog
import org.example.quiversync.presentation.components.ErrorContent
import org.example.quiversync.presentation.screens.skeletons.QuiverScreenSkeleton
import org.example.quiversync.presentation.screens.skeletons.BoardCardSkeleton
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.widgets.quiver_screen.BoardCard
import org.example.quiversync.presentation.widgets.quiver_screen.PublishSurfboardDialog
import org.example.quiversync.presentation.widgets.quiver_screen.SurfboardDetailDialog
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuiverScreen(
    modifier: Modifier = Modifier,
    viewModel: QuiverViewModel = koinViewModel(),
    onAddClick: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsState().value
    val boardToPublish by viewModel.boardToPublish.collectAsState()
    when (uiState) {
        is QuiverState.Error -> ErrorContent((uiState).message)
        is QuiverState.Loading -> { QuiverScreenSkeleton(modifier) }
        is QuiverState.Loaded -> QuiverContent(uiState.boards, viewModel::onEvent, onAddClick ,modifier, boardToPublish)
    }
}
@Composable
fun QuiverContent(boards: List<Surfboard>, onEvent: (QuiverEvent) -> Unit = {}, onAddClick: () -> Unit = {}, modifier: Modifier, boardToPublish: Surfboard?) {
    var selectedBoard by remember { mutableStateOf<Surfboard?>(null) }
    var boardToDelete by remember { mutableStateOf<Surfboard?>(null) }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
    if (boards.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No Surfboards Found",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add your first surfboard by clicking the button below.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 170.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(boards, key = { it.id }) { board ->
            BoardCard(
                board = board,
                onClick = { selectedBoard = board },
                onPublishToggle = { toggledBoard, isChecked ->
                    if (isChecked && !(toggledBoard.isRentalPublished ?: false)) {
                        onEvent(QuiverEvent.ShowPublishDialog(toggledBoard.id))
                    }
                    else if (!isChecked && (toggledBoard.isRentalPublished ?: false)) {
                        onEvent(QuiverEvent.UnpublishSurfboard(toggledBoard.id))
                    }
                }
            )
        }
    }

        FloatingActionButton(
            onClick = { onAddClick() },
            modifier = Modifier
                .padding(16.dp)
                .size(60.dp)
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Surfboard",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }

    selectedBoard?.let {
        SurfboardDetailDialog(
            board = it,
            visible = true,
            onDelete = { surfboardToDelete -> boardToDelete = surfboardToDelete },
            onDismiss = { selectedBoard = null },
        )
    }
    boardToDelete?.let { surfboard ->
        CustomDialog(
            title = "Delete Surfboard",
            message = "Are you sure you want to delete the surfboard: '${surfboard.model}'?",
            onDismiss = { boardToDelete = null },
            onConfirm = {
                onEvent(QuiverEvent.onDeleteClick(surfboard.id))
                selectedBoard = null
                boardToDelete = null
            },
            confirmText = "Delete",
            cancelText = "Cancel",
        )
    }

    boardToPublish?.let { surfboard ->
        PublishSurfboardDialog(
            surfboard = surfboard,
            onConfirm = { pricePerDay ->
                onEvent(
                    QuiverEvent.ConfirmPublish(
                        surfboard.id,
                        pricePerDay
                    )
                )
            },
            onDismiss = {
                onEvent(QuiverEvent.DismissPublishDialog)
            },
            visible = true,
        )
    }

}
