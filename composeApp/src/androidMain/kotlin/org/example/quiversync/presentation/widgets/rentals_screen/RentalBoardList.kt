package org.example.quiversync.presentation.widgets.rentals_screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.example.quiversync.R
import org.example.quiversync.domain.model.BoardForRent
import org.example.quiversync.features.rentals.explore.BoardForDisplay
import org.example.quiversync.presentation.screens.skeletons.RentalBoardCardSkeleton
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.utils.rememberShimmerBrush


@Composable
fun ExploreBoardGrid(
    boards: List<BoardForDisplay>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    val gridState = rememberLazyGridState()
    val brush = rememberShimmerBrush()

    //Load more when reaching the end of the list
    LaunchedEffect(gridState) {
        snapshotFlow {
            gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
            .filter { lastVisible ->
                lastVisible == boards.lastIndex
            }
            .distinctUntilChanged()
            .collect {
                onLoadMore()
            }
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(boards) { boardForDisplay  ->
            RentalBoardCardWrapper(boardForDisplay,brush)
        }
        if (isLoadingMore) {
            item {
                if (boards.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun RentalBoardCardWrapper(boardForDisplay: BoardForDisplay,brush: Brush) {
    if (boardForDisplay.board == null) {
        RentalBoardCardSkeleton(brush)
    } else {
        RentalBoardCard(board = boardForDisplay.board!!)
    }
}


@Composable
fun RentalBoardCard(board: BoardForRent, modifier: Modifier = Modifier) {
    val cardColor = if (isSystemInDarkTheme()) OceanPalette.DarkSurface else Color.White
    val placeHolderRes =
        if (isSystemInDarkTheme()) R.drawable.ic_board_placeholder_dark else R.drawable.ic_board_placeholder_light
    val context = LocalContext.current
    Card(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = board.surfboardPic,
                contentDescription = "",
                placeholder = painterResource(id = placeHolderRes),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit,
            )

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = board.model,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${board.type} - ${board.height}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val ownerPic = if (isSystemInDarkTheme()){
                        painterResource(id = R.drawable.placeholder_dark)
                    } else {
                        painterResource(id = R.drawable.placeholder_light)
                    }
                    AsyncImage(
                        model = board.ownerPic,
                        placeholder = ownerPic,
                        contentDescription = "Owner Profile Picture",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                    Text(
                        text = board.ownerName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
//                    IconButton(onClick = {
//                        val url = "https://wa.me/${board.ownerPhoneNumber.removePrefix("+")}"
//                        val intent = Intent(Intent.ACTION_VIEW).apply {
//                            data = Uri.parse(url)
//                        }
//                        //if no whatsapp installed show error message
//                        intent.setPackage("com.whatsapp")
//                        // Check if WhatsApp is installed
//                        if (intent.resolveActivity(context.packageManager) == null) {
//                            // Handle the case where WhatsApp is not installed
//                            Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
//                            return@IconButton
//                        }
//                        context.startActivity(intent)
//                    }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_whatsapp),
//                            contentDescription = "Chat on WhatsApp",
//                            tint = Color(0xFF25D366),
//                            modifier = Modifier.size(24.dp)
//                        )
//                    }

                    Text(
                        text = "$${String.format("%.0f", board.pricePerDay)}/day",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}