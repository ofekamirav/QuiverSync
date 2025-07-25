package org.example.quiversync.presentation.widgets.rentals_screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Divider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.example.quiversync.R
import org.example.quiversync.domain.model.BoardForRent
import org.example.quiversync.domain.model.SurfboardType
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

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = board.surfboardPic,
                contentDescription = "Surfboard Image",
                placeholder = painterResource(id = R.drawable.logo_placeholder),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(130.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = board.model,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "$${board.pricePerDay.toInt()}/day",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                HorizontalDivider()

                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoColumn(
                            label = "Type",
                            value = board.type.serverName,
                            modifier = Modifier.weight(1f)
                        )
                        InfoColumn(
                            label = "Height",
                            value = board.height,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoColumn(
                            label = "Width",
                            value = board.width,
                            modifier = Modifier.weight(1f)
                        )
                        InfoColumn(
                            label = "Volume",
                            value = board.volume,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    AsyncImage(
                        model = board.ownerPic,
                        contentDescription = "Owner Profile",
                        placeholder = painterResource(id = R.drawable.logo_placeholder),
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        board.ownerName,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Preview(showBackground = true)
@Composable
fun RentalBoardCardPreview() {
    val sampleBoard = BoardForRent(
        model = "Sample Surfboard",
        type = SurfboardType.SHORTBOARD,
        height = "6'0\"",
        width = "18.5\"",
        volume = "30L",
        pricePerDay = 25.0,
        surfboardPic = "https://example.com/surfboard.jpg",
        ownerName = "John Doe",
        surfboardId = "12345",
        ownerPic = "https://example.com/owner.jpg",
        ownerPhoneNumber = "123-456-7890",
    )

    RentalBoardCard(board = sampleBoard)
}