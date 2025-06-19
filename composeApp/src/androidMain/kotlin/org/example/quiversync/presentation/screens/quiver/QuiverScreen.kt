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
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuiverScreen(
    viewModel: QuiverViewModel = QuiverViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    when (uiState) {
        is QuiverState.Error-> ErrorContent((uiState as QuiverState.Error).message)
        is QuiverState.Loading -> LoadingAnimation(isLoading = true, animationFileName = "assets/quiver_sync_loading_animation.json")
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
            columns = GridCells.Fixed(2),
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


@Composable
fun BoardCard(
    board: Surfboard,
    onClick: () -> Unit,
    onPublishToggle: (Boolean) -> Unit
) {
    val isPublished = board.isRentalPublished
    val baseBackgroundColor = if (isSystemInDarkTheme()) OceanPalette.DarkSurface else Color.White
    val animatedBackgroundColor by animateColorAsState(
        targetValue = baseBackgroundColor,
        animationSpec = tween(300)
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isPublished) OceanPalette.SandOrange else Color.Transparent,
        animationSpec = tween(300)
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isPublished) 8.dp else 4.dp,
        animationSpec = tween(300)
    )

    val context = LocalContext.current
    val imageResId = remember(board.imageRes) {
        getDrawableIdByName(context, board.imageRes)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
        colors = CardDefaults.cardColors(containerColor = animatedBackgroundColor),
        border = BorderStroke(2.dp, animatedBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Surfboard Image",
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 12.dp)
            )
            Text(
                board.model,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = OceanPalette.DeepBlue
            )
            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "For Rent",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Switch(
                    checked = board.isRentalPublished,
                    onCheckedChange = onPublishToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = OceanPalette.SandOrange,
                        checkedTrackColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f),
                        uncheckedThumbColor = OceanPalette.SandOrange.copy(alpha = 0.4f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.4f),
                        uncheckedBorderColor = Color.Transparent,
                    )
                )
            }
        }
    }
}

fun getDrawableIdByName(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}


@Composable
fun SurfboardDetailDialog(
    board: Surfboard,
    visible: Boolean,
    onDismiss: () -> Unit,
    onDelete: (Surfboard) -> Unit = {}
) {
    val context = LocalContext.current
    val imageResId = remember(board.imageRes) {
        getDrawableIdByName(context, board.imageRes)
    }
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Dialog(onDismissRequest = onDismiss) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(16.dp),
                    elevation = CardDefaults.cardElevation(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = board.model,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = "Surfboard Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(vertical = 8.dp)
                            )

                            RowItem("Company:", board.company)
                            RowItem("Height:", board.height)
                            RowItem("Width:", board.width)
                            RowItem("Volume:", board.volume)
                            RowItem("Added:", board.addedDate)

                            if (board.isRentalPublished) {
                                RowItem("Price / Day:", "${board.pricePerDay} $")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    onDelete(board)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Delete Surfboard", color = Color.White)
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Light)
    }
}

@Preview(showBackground = true)
@Composable
fun QuiverScreenPreview() {
    QuiverSyncTheme{
        QuiverScreen(viewModel = QuiverViewModel())
    }
}