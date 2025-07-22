package org.example.quiversync.presentation.screens.rentals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExploreOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.example.quiversync.domain.model.BoardForRent
import org.example.quiversync.features.rentals.explore.ExploreState
import org.example.quiversync.features.rentals.explore.ExploreViewModel
import org.example.quiversync.presentation.screens.skeletons.ExploreTabSkeleton
import org.example.quiversync.presentation.widgets.ErrorScreen
import org.example.quiversync.presentation.widgets.rentals_screen.ExploreBoardGrid
import org.example.quiversync.presentation.widgets.rentals_screen.RentalBoardCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExploreTab(
    viewModel: ExploreViewModel = koinViewModel()
){
    val uiState = viewModel.uiState.collectAsState().value
    val isLoadingMore = viewModel.isLoadingMore.collectAsState().value

    when(uiState) {
        is ExploreState.Loading -> {
            ExploreTabSkeleton()
        }
        is ExploreState.Loaded -> {
            val boards = uiState.communityBoards
            if (boards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No boards available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            } else {
                ExploreBoardGrid(
                    boards = boards,
                    isLoadingMore = isLoadingMore,
                    onLoadMore = { viewModel.loadNextPage() }
                )
            }
        }
        is ExploreState.Error ->{
            ErrorScreen(
                message = uiState.message,
                icon = Icons.Filled.ExploreOff,
            )
        }
    }

}