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
import org.example.quiversync.domain.model.Quiver
import org.example.quiversync.features.quiver.QuiverState
import org.example.quiversync.features.quiver.QuiverViewModel
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.presentation.components.ErrorContent
import org.example.quiversync.presentation.screens.skeletons.QuiverScreenSkeleton
import org.example.quiversync.presentation.screens.skeletons.BoardCardSkeleton
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.widgets.quiver_screen.BoardCard
import org.example.quiversync.presentation.widgets.quiver_screen.SurfboardDetailDialog
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuiverScreen(
    viewModel: QuiverViewModel = QuiverViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    when (uiState) {
        is QuiverState.Error -> ErrorContent((uiState).message)
        is QuiverState.Loading -> { QuiverScreenSkeleton() }
        is QuiverState.Loaded -> QuiverContent(uiState.quiver)
    }
}
@Composable
fun QuiverContent(boards: Quiver) {
    var selectedBoard by remember { mutableStateOf<Surfboard?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 170.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(boards.items.size) { index ->
                BoardCard(
                    board = boards.items[index],
                    onClick = { selectedBoard = boards.items[index] },
                    onPublishToggle = { /* Handle Publish Toggle */ }
                )
            }
        }


        Spacer(Modifier.weight(1f))

        FloatingActionButton(
            onClick = { /* Handle Add */ },
            modifier = Modifier
                .padding(16.dp)
                .size(60.dp)
                .align(Alignment.End),
            containerColor = OceanPalette.SurfBlue,
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
            onDismiss = { selectedBoard = null },
            onDelete = { /* Handle Delete */ }
        )
    }
}


fun getDrawableIdByName(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}



@Preview(showBackground = true)
@Composable
fun QuiverScreenPreview() {
    QuiverSyncTheme{
        QuiverScreen(viewModel = QuiverViewModel())
    }
}